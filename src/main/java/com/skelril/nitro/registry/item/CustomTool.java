/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.item;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Set;

public abstract class CustomTool extends ItemTool implements ICustomTool {

    public CustomTool() {
        super(0, 0, ToolMaterial.DIAMOND, Sets.newHashSet());
        this.maxStackSize = __getMaxStackSize();
        this.setCreativeTab(__getCreativeTab());

        this.setMaxDamage(__getMaxUses());
    }

    // Modified Native ItemTool methods

    @Override
    public boolean __superGetIsRepairable(ItemStack toRepair, ItemStack repair) {
        return super.getIsRepairable(toRepair, repair);
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
    public Multimap<String, AttributeModifier> __superGetItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
        return super.getItemAttributeModifiers(equipmentSlot);
    }

    @Override
    public float getStrVsBlock(ItemStack stack, IBlockState block) {
        return ICustomTool.super.getStrVsBlock(stack, block);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        return ICustomTool.super.hitEntity(stack, target, attacker);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean isFull3D() {
        return ICustomTool.super.isFull3D();
    }

    @Override
    public int getItemEnchantability() {
        return ICustomTool.super.getItemEnchantability();
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return ICustomTool.super.getIsRepairable(toRepair, repair);
    }

    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
        return ICustomTool.super.getItemAttributeModifiers(equipmentSlot);
    }

    @Override
    public int getHarvestLevel(ItemStack stack, String toolClass) {
        return ICustomTool.super.getHarvestLevel(stack, toolClass);
    }

    @Override
    public Set<String> getToolClasses(ItemStack stack) {
        return ICustomTool.super.getToolClasses(stack);
    }
}
