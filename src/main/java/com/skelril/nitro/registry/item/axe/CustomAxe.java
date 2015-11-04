/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.item.axe;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Set;
import java.util.UUID;

public abstract class CustomAxe extends ItemAxe implements ICustomAxe {
    protected CustomAxe() {
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
    public float getStrVsBlock(ItemStack stack, Block block) {
        return ICustomAxe.super.getStrVsBlock(stack, block);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        return ICustomAxe.super.hitEntity(stack, target, attacker);
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World worldIn, Block blockIn, BlockPos pos, EntityLivingBase playerIn) {
        return ICustomAxe.super.onBlockDestroyed(stack, worldIn, blockIn, pos, playerIn);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean isFull3D() {
        return ICustomAxe.super.isFull3D();
    }

    @Override
    public int getItemEnchantability() {
        return ICustomAxe.super.getItemEnchantability();
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return ICustomAxe.super.getIsRepairable(toRepair, repair);
    }

    @Override
    public Multimap getItemAttributeModifiers() {
        return ICustomAxe.super.getItemAttributeModifiers();
    }

    @Override
    public int getHarvestLevel(ItemStack stack, String toolClass) {
        return ICustomAxe.super.getHarvestLevel(stack, toolClass);
    }

    @Override
    public Set<String> getToolClasses(ItemStack stack) {
        return ICustomAxe.super.getToolClasses(stack);
    }

    @Override
    public float getDigSpeed(ItemStack stack, net.minecraft.block.state.IBlockState state) {
        return ICustomAxe.super.getDigSpeed(stack, state);
    }
}
