/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.tool;

import com.skelril.nitro.registry.Craftable;
import com.skelril.nitro.registry.item.CustomItem;
import com.skelril.nitro.selector.EventAwareContent;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.registry.item.CustomItemTypes;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;

public class FocusTeleporter extends CustomItem implements Craftable, EventAwareContent {

    @Override
    public String __getID() {
        return "focus_teleporter";
    }

    @Override
    public int __getMaxStackSize() {
        return 1;
    }

    @Override
    public CreativeTabs __getCreativeTab() {
        return CreativeTabs.tabTools;
    }

    @Override
    public void registerRecipes() {
        GameRegistry.addRecipe(
                new ItemStack(this),
                "AAA",
                "BCB",
                "BBB",
                'A', new ItemStack(CustomItemTypes.RED_SHARD),
                'B', new ItemStack(CustomItemTypes.SEA_CRYSTAL),
                'c', new ItemStack(CustomItemTypes.ENDER_FOCUS)
        );
    }

    @Listener
    public void onLeftClick(InteractBlockEvent.Primary event) {
        Optional<Player> optPlayer = event.getCause().first(Player.class);

        if (!optPlayer.isPresent()) return;

        Player player = optPlayer.get();

        Optional<org.spongepowered.api.item.inventory.ItemStack> optHeldItem = player.getItemInHand();

        if (optHeldItem.isPresent()) {
            org.spongepowered.api.item.inventory.ItemStack held = optHeldItem.get();
            if (held.getItem() == this) {
                setDestination(held, player.getLocation());
                player.setItemInHand(held);
            }
        }
    }

    @Listener
    public void onRightClick(InteractBlockEvent.Secondary event) {
        Optional<Player> optPlayer = event.getCause().first(Player.class);

        if (!optPlayer.isPresent()) return;

        Player player = optPlayer.get();

        Optional<org.spongepowered.api.item.inventory.ItemStack> optHeldItem = player.getItemInHand();

        if (optHeldItem.isPresent()) {
            org.spongepowered.api.item.inventory.ItemStack held = optHeldItem.get();
            if (held.getItem() == this) {
                Optional<Location<World>> optDestination = getDestination(held);
                InventoryPlayer playerInv = ((EntityPlayer) player).inventory;
                if (optDestination.isPresent() && playerInv.hasItem(CustomItemTypes.ENDER_FOCUS)) {
                    playerInv.consumeInventoryItem(CustomItemTypes.ENDER_FOCUS);
                    ((EntityPlayer) player).inventoryContainer.detectAndSendChanges();
                    player.setLocation(optDestination.get());
                }
            }
        }
    }

    private void setDestination(org.spongepowered.api.item.inventory.ItemStack stack, Location<World> target) {
        setDestination((ItemStack) (Object) stack, target);
    }

    private void setDestination(ItemStack stack, Location<World> target) {
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
    }

    private Optional<Location<World>> getDestination(org.spongepowered.api.item.inventory.ItemStack stack) {
        return getDestination((ItemStack) (Object) stack);
    }

    private Optional<Location<World>> getDestination(ItemStack stack) {
        if (stack.getTagCompound() == null || !stack.getTagCompound().hasKey("skree_dest_data")) {
            return Optional.empty();
        }

        NBTTagCompound tag = stack.getTagCompound().getCompoundTag("skree_dest_data");
        String worldName = tag.getString("world");
        double x = tag.getDouble("x");
        double y = tag.getDouble("y");
        double z = tag.getDouble("z");
        Optional<World> optWorld = SkreePlugin.inst().getGame().getServer().getWorld(worldName);
        if (optWorld.isPresent()) {
            return Optional.of(new Location<>(optWorld.get(), x, y, z));
        }
        return Optional.empty();
    }

    private Optional<String> getClientDestination(org.spongepowered.api.item.inventory.ItemStack stack) {
        return getClientDestination((ItemStack) (Object) stack);
    }

    private Optional<String> getClientDestination(ItemStack stack) {
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
        tooltip.add("Destination: " + (optDestStr.isPresent() ? optDestStr.get() : "Not set"));
    }
}
