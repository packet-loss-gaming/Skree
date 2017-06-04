/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.item.shovel;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Set;

public abstract class CustomShovel extends ItemSpade implements ICustomShovel {
  protected CustomShovel() {
    super(ToolMaterial.DIAMOND);
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
  public Multimap<String, AttributeModifier> __superGetItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
    return HashMultimap.create(); // Use functionality defined in Item
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
  public boolean canHarvestBlock(IBlockState blockIn) {
    return ICustomShovel.super.canHarvestBlock(blockIn);
  }

  @Override
  public float getStrVsBlock(ItemStack stack, IBlockState state) {
    return ICustomShovel.super.getStrVsBlock(stack, state);
  }

  @Override
  public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
    return ICustomShovel.super.hitEntity(stack, target, attacker);
  }

  @Override
  public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
    return ICustomShovel.super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
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
  public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
    return ICustomShovel.super.getItemAttributeModifiers(equipmentSlot);
  }

  @Override
  public int getHarvestLevel(ItemStack stack, String toolClass) {
    return ICustomShovel.super.getHarvestLevel(stack, toolClass);
  }

  @Override
  public Set<String> getToolClasses(ItemStack stack) {
    return ICustomShovel.super.getToolClasses(stack);
  }
}
