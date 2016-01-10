/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.tool;

import com.skelril.nitro.registry.item.CustomItem;
import com.skelril.nitro.selector.EventAwareContent;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;

import static com.skelril.nitro.transformer.ForgeTransformer.tf;

public class NetherBowl extends CustomItem implements EventAwareContent {

    @Override
    public String __getID() {
        return "nether_bowl";
    }

    @Override
    public List<String> __getMeshDefinitions() {
        List<String> baseList = super.__getMeshDefinitions();
        baseList.add("nether_bowl_full");
        return baseList;
    }

    @Override
    public int __getMaxStackSize() {
        return 1;
    }

    @Override
    public CreativeTabs __getCreativeTab() {
        return CreativeTabs.tabMaterials;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(net.minecraft.item.Item itemIn, CreativeTabs tab, List subItems) {
        subItems.add(new ItemStack(itemIn, 1, 0));
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.DRINK;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, net.minecraft.world.World worldIn, EntityPlayer playerIn) {
        Optional<Location<World>> optLoc = getDestination(stack);
        if (optLoc.isPresent()) {
            tf(playerIn).setLocation(optLoc.get());
        }

        return new ItemStack(this);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, net.minecraft.world.World worldIn, EntityPlayer playerIn) {
        if (getDestination(itemStackIn).isPresent()) {
            playerIn.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
        }

        return itemStackIn;
    }

    public static void setDestination(org.spongepowered.api.item.inventory.ItemStack stack, Location<World> target) {
        setDestination(tf(stack), target);
    }

    public static void setDestination(ItemStack stack, Location<World> target) {
        if (stack.getTagCompound() == null) {
            stack.setTagCompound(new NBTTagCompound());
        }

        if (!stack.getTagCompound().hasKey("skree_dest_data")) {
            stack.getTagCompound().setTag("skree_dest_data", new NBTTagCompound());
        }

        NBTTagCompound tag = stack.getTagCompound().getCompoundTag("skree_dest_data");
        tag.setString("world", target.getExtent().getName());
        tag.setDouble("x", target.getX());
        tag.setDouble("y", target.getY());
        tag.setDouble("z", target.getZ());

        stack.setItemDamage(1);
    }

    private static Optional<Location<World>> getDestination(org.spongepowered.api.item.inventory.ItemStack stack) {
        return getDestination(tf(stack));
    }

    private static Optional<Location<World>> getDestination(ItemStack stack) {
        if (stack.getTagCompound() == null || !stack.getTagCompound().hasKey("skree_dest_data")) {
            return Optional.empty();
        }

        NBTTagCompound tag = stack.getTagCompound().getCompoundTag("skree_dest_data");
        String worldName = tag.getString("world");
        double x = tag.getDouble("x");
        double y = tag.getDouble("y");
        double z = tag.getDouble("z");
        Optional<World> optWorld = Sponge.getServer().getWorld(worldName);
        if (optWorld.isPresent()) {
            return Optional.of(new Location<>(optWorld.get(), x, y, z));
        }
        return Optional.empty();
    }

    private static Optional<String> getClientDestination(org.spongepowered.api.item.inventory.ItemStack stack) {
        return getClientDestination(tf(stack));
    }

    private static Optional<String> getClientDestination(ItemStack stack) {
        if (stack.getTagCompound() == null || !stack.getTagCompound().hasKey("skree_dest_data")) {
            return Optional.empty();
        }

        NBTTagCompound tag = stack.getTagCompound().getCompoundTag("skree_dest_data");
        String worldName = tag.getString("world");
        double x = tag.getDouble("x");
        double y = tag.getDouble("y");
        double z = tag.getDouble("z");

        return Optional.of(worldName + " at " + (int) x + ", " + (int) y + ", " + (int) z);
    }


    // Modified Native Item methods

    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced) {
        Optional<String> optDestStr = getClientDestination(stack);
        if (optDestStr.isPresent()) {
            tooltip.add("Drink to return to your grave.");
        } else {
            tooltip.add("An evil looking bowl.");
        }
    }
}