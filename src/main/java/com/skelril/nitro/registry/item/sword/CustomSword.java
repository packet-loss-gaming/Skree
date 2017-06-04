/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.item.sword;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class CustomSword extends ItemSword implements ICustomSword {
  protected CustomSword() {
    super(ToolMaterial.DIAMOND);
    this.maxStackSize = __getMaxStackSize();
    this.setCreativeTab(__getCreativeTab());

    this.setMaxDamage(__getMaxUses());
  }

  // Native compatibility methods

  @Override
  public boolean __superGetIsRepairable(ItemStack toRepair, ItemStack repair) {
    return false; // Use functionality defined in Item
  }

  @Override
  public Multimap<String, AttributeModifier> __superGetItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
    return HashMultimap.create(); // Use functionality defined in Item
  }

  // Modified Native ItemTool methods
  @Override
  public float getStrVsBlock(ItemStack stack, IBlockState state) {
    return ICustomSword.super.getStrVsBlock(stack, state);
  }

  @Override
  public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
    return ICustomSword.super.hitEntity(stack, target, attacker);
  }

  @Override
  public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
    return ICustomSword.super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean isFull3D() {
    return ICustomSword.super.isFull3D();
  }

  @Override
  public EnumAction getItemUseAction(ItemStack stack) {
    return ICustomSword.super.getItemUseAction(stack);
  }

  @Override
  public int getMaxItemUseDuration(ItemStack stack) {
    return ICustomSword.super.getMaxItemUseDuration(stack);
  }

  @Override
  public boolean canHarvestBlock(IBlockState state) {
    return ICustomSword.super.canHarvestBlock(state);
  }

  @Override
  public int getItemEnchantability() {
    return ICustomSword.super.getItemEnchantability();
  }

  @Override
  public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
    return ICustomSword.super.getIsRepairable(toRepair, repair);
  }

  @Override
  public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
    return ICustomSword.super.getItemAttributeModifiers(equipmentSlot);
  }
}
