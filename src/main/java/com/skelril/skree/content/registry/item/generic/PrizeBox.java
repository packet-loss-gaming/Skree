/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.generic;

import com.skelril.nitro.registry.item.CustomItem;
import com.skelril.skree.content.registry.item.CustomItemTypes;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Optional;

import static com.skelril.nitro.item.ItemStackFactory.newItemStack;
import static com.skelril.nitro.transformer.ForgeTransformer.tf;

public class PrizeBox extends CustomItem {
    @Override
    public String __getID() {
        return "prize_box";
    }

    @Override
    public int __getMaxStackSize() {
        return 1;
    }

    @Override
    public CreativeTabs __getCreativeTab() {
        return null;
    }

    @Override
    public Optional<ItemMeshDefinition> __getCustomMeshDefinition() {
        // DO NOT use a lambda here, converting this code to a lambda results
        // in the model being unresolvable

        //noinspection Convert2Lambda
        return Optional.of(new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                Optional<org.spongepowered.api.item.inventory.ItemStack> optContained = getPrizeStack(stack);

                return optContained.isPresent() ? new ModelResourceLocation(
                        optContained.get().getItem().getId(),
                        "inventory"
                ) : null;
            }
        });
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true;
    }

    @Override
    public String getHighlightTip(ItemStack item, String displayName) {
        Optional<org.spongepowered.api.item.inventory.ItemStack> optContained = getPrizeStack(item);

        return optContained.isPresent() ? tf(optContained.get()).getDisplayName() + " " + displayName: displayName;
    }


    public static org.spongepowered.api.item.inventory.ItemStack makePrizeBox(org.spongepowered.api.item.inventory.ItemStack stack) {
        org.spongepowered.api.item.inventory.ItemStack newStack = newItemStack(CustomItemTypes.PRIZE_BOX);
        setPrizeStack(newStack, stack);
        return newStack;
    }

    private static void setPrizeStack(org.spongepowered.api.item.inventory.ItemStack stack, org.spongepowered.api.item.inventory.ItemStack held) {
        setPrizeStack(tf(stack), tf(held));
    }

    private static void setPrizeStack(ItemStack stack, ItemStack held) {
        if (stack.getTagCompound() == null) {
            stack.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound heldItem = new NBTTagCompound();
        held.writeToNBT(heldItem);

        stack.getTagCompound().setTag("skree_held_prize_data", heldItem);
    }

    public static Optional<org.spongepowered.api.item.inventory.ItemStack> getPrizeStack(org.spongepowered.api.item.inventory.ItemStack stack) {
        return getPrizeStack(tf(stack));
    }

    public static Optional<org.spongepowered.api.item.inventory.ItemStack> getPrizeStack(ItemStack stack) {
        if (stack.getTagCompound() == null) {
            stack.setTagCompound(new NBTTagCompound());
        }

        NBTTagCompound tag = stack.getTagCompound().getCompoundTag("skree_held_prize_data");

        if (tag != null) {
            ItemStack returned = new ItemStack(Blocks.AIR);
            returned.readFromNBT(tag);
            return Optional.of(tf(returned));
        }
        return Optional.empty();
    }

    // Modified Native Item methods

    @SuppressWarnings("unchecked")
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced) {
        Optional<org.spongepowered.api.item.inventory.ItemStack> optPrize = getPrizeStack(stack);
        if (optPrize.isPresent()) {
            org.spongepowered.api.item.inventory.ItemStack prize = optPrize.get();
            tooltip.add("Contains: " + tf(prize).getDisplayName() + " x" + prize.getQuantity());
        }
    }
}
