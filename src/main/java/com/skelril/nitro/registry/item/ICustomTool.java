/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.item;

import com.google.common.collect.Multimap;
import com.skelril.nitro.registry.HarvestTier;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public interface ICustomTool extends CustomItem, DegradableItem {
    // Skelril Methods

    // General

    @Override
    default String __getID() {
        return __getType() + "_" + __getToolClass();
    }

    @Override
    default int __getMaxStackSize() {
        return 1;
    }

    String __getType();

    String __getToolClass();

    @Override
    default CreativeTabs __getCreativeTab() {
        return CreativeTabs.tabTools;
    }

    // Repair
    ItemStack __getRepairItemStack();

    // Combat Data
    default int __getDamageForUseOnEntity() {
        return 2;
    }

    double __getHitPower();

    // Enchantability
    int __getEnchantability();

    // Block Modification Data

    default int __getDamageForUseOnBlock() {
        return 1;
    }

    HarvestTier __getHarvestTier();

    float __getSpecializedSpeed();

    default float __getGeneralizedSpeed() {
        return 1.0F;
    }

    Collection<Block> __getEffectiveBlocks();

    // Native compatibility methods

    boolean __superGetIsRepairable(ItemStack toRepair, ItemStack repair);

    Multimap __superGetItemAttributeModifiers();

    UUID __itemModifierUUID();

    int __superGetHarvestLevel(ItemStack stack, String toolClass);

    Set<String> __superGetToolClasses(ItemStack stack);

    float __superGetDigSpeed(ItemStack stack, net.minecraft.block.state.IBlockState state);

    // Modified Native ItemTool methods

    default float getStrVsBlock(ItemStack stack, Block block) {
        return __getEffectiveBlocks().contains(block) ? __getSpecializedSpeed() : __getGeneralizedSpeed();
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
        multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(__itemModifierUUID(), "Tool modifier", __getHitPower(), 0));
        return multimap;
    }

    // Modified Forge ItemTool methods

    default int getHarvestLevel(ItemStack stack, String toolClass) {
        int level = __superGetHarvestLevel(stack, toolClass);
        if (level == -1 && toolClass != null && toolClass.equals(__getToolClass())) {
            return __getHarvestTier().getTranslation();
        } else {
            return level;
        }
    }

    default Set<String> getToolClasses(ItemStack stack) {
        return __getToolClass() != null ? com.google.common.collect.ImmutableSet.of(__getToolClass()) : __superGetToolClasses(
                stack
        );
    }

    default float getDigSpeed(ItemStack stack, net.minecraft.block.state.IBlockState state) {
        for (String type : getToolClasses(stack)) {
            if (state.getBlock().isToolEffective(type, state)) {
                return __getSpecializedSpeed();
            }
        }
        return __superGetDigSpeed(stack, state);
    }
}
