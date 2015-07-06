/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.item.sword;

import com.google.common.collect.Multimap;
import com.skelril.nitro.registry.item.ICustomItem;
import com.skelril.nitro.registry.item.DegradableItem;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

public interface ICustomSword extends ICustomItem, DegradableItem {
    // Skelril Methods

    // General

    @Override
    default int __getMaxStackSize() {
        return 1;
    }

    int __getMaxUses();

    String __getType();

    @Override
    default CreativeTabs __getCreativeTab() {
        return CreativeTabs.tabCombat;
    }

    // Repair
    ItemStack __getRepairItemStack();

    // Combat Data
    default int __getDamageForUseOnEntity() {
        return 1;
    }

    double __getHitPower();

    // Enchantability
    int __getEnchantability();

    // Block Modification Data
    default int __getDamageForUseOnBlock() {
        return 2;
    }

    @Override
    default String __getID() {
        return __getType() + "_sword";
    }

    // Native compatibility methods

    boolean __superGetIsRepairable(ItemStack toRepair, ItemStack repair);

    Multimap __superGetItemAttributeModifiers();

    UUID __itemModifierUUID();

    // Modified Native ItemTool methods

    default float getStrVsBlock(ItemStack stack, Block p_150893_2_) {
        if (p_150893_2_ == Blocks.web) {
            return 15.0F;
        } else {
            Material material = p_150893_2_.getMaterial();
            return material != Material.plants && material != Material.vine && material != Material.coral && material != Material.leaves && material != Material.gourd ? 1.0F : 1.5F;
        }
    }

    /**
     * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
     * the damage on the stack.
     *
     * @param target The Entity being hit
     * @param attacker the attacking entity
     */
    default boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        stack.damageItem(__getDamageForUseOnEntity(), attacker);
        return true;
    }

    /**
     * Called when a Block is destroyed using this Item. Return true to trigger the "Use Item" statistic.
     */
    default boolean onBlockDestroyed(ItemStack stack, World worldIn, Block blockIn, BlockPos pos, EntityLivingBase playerIn) {
        if ((double) blockIn.getBlockHardness(worldIn, pos) != 0.0D) {
            stack.damageItem(__getDamageForUseOnBlock(), playerIn);
        }

        return true;
    }

    /**
     * Returns True is the item is renderer in full 3D when hold.
     */
    @SideOnly(Side.CLIENT)
    default boolean isFull3D() {
        return true;
    }

    /**
     * returns the action that specifies what animation to play when the items is being used
     */
    default EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BLOCK;
    }

    /**
     * How long it takes to use or consume an item
     */
    default int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    default ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn) {
        playerIn.setItemInUse(itemStackIn, this.getMaxItemUseDuration(itemStackIn));
        return itemStackIn;
    }

    /**
     * Check whether this Item can harvest the given Block
     */
    default boolean canHarvestBlock(Block blockIn) {
        return blockIn == Blocks.web;
    }

    /**
     * Return the enchantability factor of the item, most of the time is based on material.
     */
    default int getItemEnchantability() {
        return __getEnchantability();
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
     * Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
     */
    default Multimap getItemAttributeModifiers() {
        Multimap multimap = __superGetItemAttributeModifiers();
        multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(__itemModifierUUID(), "Weapon modifier", __getHitPower(), 0));
        return multimap;
    }
}
