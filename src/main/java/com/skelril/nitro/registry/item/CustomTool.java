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

public abstract class CustomTool extends DegradableItem implements CustomItem {

    // Skelril Methods

    // General

    @Override
    public String __getID() {
        return __getType() + "_" + __getToolClass();
    }

    @Override
    public int __getMaxStackSize() {
        return 1;
    }

    public abstract String __getType();

    public abstract String __getToolClass();

    @Override
    public CreativeTabs __getCreativeTab() {
        return CreativeTabs.tabTools;
    }

    // Repair
    public abstract ItemStack __getRepairItemStack();

    // Combat Data
    public int __getDamageForUseOnEntity() {
        return 2;
    }

    public abstract double __getHitPower();

    // Enchantability
    public abstract int __getEnchantability();

    // Block Modification Data

    public int __getDamageForUseOnBlock() {
        return 1;
    }

    public abstract HarvestTier __getHarvestTier();

    public abstract float __getSpecializedSpeed();

    public float __getGeneralizedSpeed() {
        return 1.0F;
    }

    public abstract Collection<Block> __getEffectiveBlocks();


    // Modified Native ItemTool methods

    @Override
    public float getStrVsBlock(ItemStack stack, Block block) {
        return __getEffectiveBlocks().contains(block) ? __getSpecializedSpeed() : __getGeneralizedSpeed();
    }

    /**
     * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
     * the damage on the stack.
     *
     * @param target The Entity being hit
     * @param attacker the attacking entity
     */
    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        stack.damageItem(__getDamageForUseOnEntity(), attacker);
        return true;
    }

    /**
     * Called when a Block is destroyed using this Item. Return true to trigger the "Use Item" statistic.
     */
    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, Block blockIn, BlockPos pos, EntityLivingBase playerIn) {
        if ((double) blockIn.getBlockHardness(worldIn, pos) != 0.0D) {
            stack.damageItem(__getDamageForUseOnBlock(), playerIn);
        }
        return true;
    }

    /**
     * Returns True is the item is renderer in full 3D when hold.
     */
    @SideOnly(Side.CLIENT)
    public boolean isFull3D() {
        return true;
    }


    /**
     * Return the enchantability factor of the item, most of the time is based on material.
     */
    public int getItemEnchantability() {
        return __getEnchantability();
    }

    /**
     * Return whether this item is repairable in an anvil.
     *
     * @param toRepair The ItemStack to be repaired
     * @param repair The ItemStack that should repair this Item (leather for leather armor, etc.)
     */
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        ItemStack mat = __getRepairItemStack();
        if (mat != null && net.minecraftforge.oredict.OreDictionary.itemMatches(mat, repair, false)) return true;
        return super.getIsRepairable(toRepair, repair);
    }

    /**
     * Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
     */
    public Multimap getItemAttributeModifiers()
    {
        Multimap multimap = super.getItemAttributeModifiers();
        multimap.put(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName(), new AttributeModifier(itemModifierUUID, "Tool modifier", __getHitPower(), 0));
        return multimap;
    }

    // Modified Forge ItemTool methods

    @Override
    public int getHarvestLevel(ItemStack stack, String toolClass) {
        int level = super.getHarvestLevel(stack, toolClass);
        if (level == -1 && toolClass != null && toolClass.equals(__getToolClass())) {
            return __getHarvestTier().getTranslation();
        } else {
            return level;
        }
    }

    @Override
    public Set<String> getToolClasses(ItemStack stack) {
        return __getToolClass() != null ? com.google.common.collect.ImmutableSet.of(__getToolClass()) : super.getToolClasses(stack);
    }

    @Override
    public float getDigSpeed(ItemStack stack, net.minecraft.block.state.IBlockState state) {
        for (String type : getToolClasses(stack)) {
            if (state.getBlock().isToolEffective(type, state)) {
                return __getSpecializedSpeed();
            }
        }
        return super.getDigSpeed(stack, state);
    }
}
