/*
 * This Source Code Form is subject to the terms of the Mozilla default
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.item.armor;

import com.skelril.nitro.registry.item.DegradableItem;
import com.skelril.nitro.registry.item.ICustomItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface ICustomArmor extends ICustomItem, DegradableItem {

    static final int[] maxDamageArray = new int[] {11, 16, 15, 13};

    // Skelril Methods

    // General

    @Override
    default int __getMaxStackSize() {
        return 1;
    }

    default int __getMaxUses() {
        return maxDamageArray[__getArmorTypeIndex()] * __getMaxUsesBaseModifier();
    }

    int __getMaxUsesBaseModifier();

    String __getType();

    @Override
    default CreativeTabs __getCreativeTab() {
        return CreativeTabs.tabCombat;
    }

    // Repair
    ItemStack __getRepairItemStack();

    // Combat Data
    int __getDamageReductionAmount();

    // Enchantability
    int __getEnchantability();

    @Override
    default String __getID() {
        return __getType() + "_" + __getArmorCategory();
    }

    String __getArmorCategory();

    // Native compatibility methods

    int __getArmorTypeIndex();

    ItemArmor.ArmorMaterial __superGetArmorMaterial();

    boolean __superGetIsRepairable(ItemStack toRepair, ItemStack repair);

    // Modified Native ItemArmor methods

    default String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        // Derived from net.minecraft.client.renderer.entity.layers.LayerArmorBase$getArmorResource
        String texture = __getType();
        String domain = "skree";

        return String.format("%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, (slot == 2 ? 2 : 1), type == null ? "" : String.format("_%s", type));
    }

    default int getColorFromItemStack(ItemStack stack, int renderPass) {
        if (renderPass > 0) {
            return 16777215;
        } else {
            int j = this.getColor(stack);

            if (j < 0) {
                j = 16777215;
            }

            return j;
        }
    }


    /**
     * Return the enchantability factor of the item, most of the time is based on material.
     */
    default int getItemEnchantability() {
        return __getEnchantability();
    }

    /**
     * Return the armor material for this armor item.
     */
    default ItemArmor.ArmorMaterial getArmorMaterial() {
        return __superGetArmorMaterial();
    }

    /**
     * Return whether the specified armor ItemStack has a color.
     */
    default boolean hasColor(ItemStack stack) {
        return false;
    }

    /**
     * Return the color for the specified armor ItemStack.
     */
    default int getColor(ItemStack stack) {
        return -1;
    }

    /**
     * Remove the color from the specified armor ItemStack.
     */
    default void removeColor(ItemStack stack) {

    }

    /**
     * Sets the color of the specified armor ItemStack
     */
    default void setColor(ItemStack stack, int color) {
        throw new UnsupportedOperationException("Can\'t dye non-leather!");
    }

    /**
     * Return whether this item is repairable in an anvil.
     *
     * @param toRepair The ItemStack to be repaired
     * @param repair The ItemStack that should repair this Item (leather for leather armor, etc.)
     */
    default boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        ItemStack mat = __getRepairItemStack();
        if (mat != null && net.minecraftforge.oredict.OreDictionary.itemMatches(mat, repair, false)) return true;
        return __superGetIsRepairable(toRepair, repair);
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    default ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
        int i = EntityLiving.getArmorPosition(itemStackIn) - 1;
        ItemStack itemstack1 = playerIn.getCurrentArmor(i);

        if (itemstack1 == null) {
            playerIn.setCurrentItemOrArmor(i + 1, itemStackIn.copy()); //Forge: Vanilla bug fix associated with fixed setCurrentItemOrArmor indexs for players.
            itemStackIn.stackSize = 0;
        }

        return itemStackIn;
    }
}
