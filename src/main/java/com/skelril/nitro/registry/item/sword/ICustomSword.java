/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.item.sword;

import com.google.common.collect.Multimap;
import com.skelril.nitro.registry.item.DegradableItem;
import com.skelril.nitro.registry.item.ICustomItem;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.skelril.nitro.registry.item.ICustomTool.ATTACK_DAMAGE_MODIFIER;
import static com.skelril.nitro.registry.item.ICustomTool.ATTACK_SPEED_MODIFIER;

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
        return CreativeTabs.COMBAT;
    }

    // Repair
    ItemStack __getRepairItemStack();

    // Combat Data
    default int __getDamageForUseOnEntity() {
        return 1;
    }

    double __getHitPower();
    default double __getAttackSpeed() {
        return -2.4000000953674316D;
    }

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

    Multimap<String, AttributeModifier> __superGetItemAttributeModifiers(EntityEquipmentSlot equipmentSlot);

    // Modified Native ItemTool methods

    default float getStrVsBlock(ItemStack stack, IBlockState state) {
        Block block = state.getBlock();

        if (block == Blocks.WEB)
        {
            return 15.0F;
        }
        else
        {
            Material material = state.getMaterial();
            return material != Material.PLANTS && material != Material.VINE && material != Material.CORAL && material != Material.LEAVES && material != Material.GOURD ? 1.0F : 1.5F;
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
    default boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
        if ((double)state.getBlockHardness(worldIn, pos) != 0.0D) {
            stack.damageItem(2, entityLiving);
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
     * Check whether this Item can harvest the given Block
     */
    default boolean canHarvestBlock(IBlockState state) {
        return state == Blocks.WEB;
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
    @SuppressWarnings("unchecked")
    default Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
        Multimap<String, AttributeModifier> multimap = __superGetItemAttributeModifiers(equipmentSlot);

        if (equipmentSlot == EntityEquipmentSlot.MAINHAND)
        {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", __getHitPower(), 0));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getAttributeUnlocalizedName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", __getAttackSpeed(), 0));
        }

        return multimap;
    }
}
