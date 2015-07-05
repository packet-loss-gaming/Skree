/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.item.shovel;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Set;
import java.util.UUID;

public abstract class CustomShovel extends ItemSpade implements ICustomShovel{
    protected CustomShovel() {
        super(ToolMaterial.EMERALD);
        this.maxStackSize = __getMaxStackSize();
        this.setCreativeTab(__getCreativeTab());

        this.setMaxDamage(__getMaxUses());
    }

    // Modified Native ItemTool methods

    @Override
    public boolean __superGetIsRepairable(ItemStack toRepair, ItemStack repair) {
        return false; // Use functionality defined in Item
    }

    @Override
    public Multimap __superGetItemAttributeModifiers() {
        return HashMultimap.create(); // Use functionality defined in Item
    }

    @Override
    public UUID __itemModifierUUID() {
        return itemModifierUUID;
    }

    @Override
    public int __superGetHarvestLevel(ItemStack stack, String toolClass) {
        return super.getHarvestLevel(stack, toolClass);
    }

    @Override
    public Set<String> __superGetToolClasses(ItemStack stack) {
        return super.getToolClasses(stack);
    }

    @Override
    public float __superGetDigSpeed(ItemStack stack, IBlockState state) {
        return super.getDigSpeed(stack, state);
    }

    @Override
    public boolean canHarvestBlock(Block blockIn) {
        return ICustomShovel.super.canHarvestBlock(blockIn);
    }
    
    @Override
    public float getStrVsBlock(ItemStack stack, Block block) {
        return ICustomShovel.super.getStrVsBlock(stack, block);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        return ICustomShovel.super.hitEntity(stack, target, attacker);
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, Block blockIn, BlockPos pos, EntityLivingBase playerIn) {
        return ICustomShovel.super.onBlockDestroyed(stack, worldIn, blockIn, pos, playerIn);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean isFull3D() {
        return ICustomShovel.super.isFull3D();
    }

    @Override
    public int getItemEnchantability() {
        return ICustomShovel.super.getItemEnchantability();
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return ICustomShovel.super.getIsRepairable(toRepair, repair);
    }

    @Override
    public Multimap getItemAttributeModifiers() {
        return ICustomShovel.super.getItemAttributeModifiers();
    }

    @Override
    public int getHarvestLevel(ItemStack stack, String toolClass) {
        return ICustomShovel.super.getHarvestLevel(stack, toolClass);
    }

    @Override
    public Set<String> getToolClasses(ItemStack stack) {
        return ICustomShovel.super.getToolClasses(stack);
    }

    @Override
    public float getDigSpeed(ItemStack stack, net.minecraft.block.state.IBlockState state) {
        return ICustomShovel.super.getDigSpeed(stack, state);
    }
}
