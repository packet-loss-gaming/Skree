/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.item;

import com.google.common.collect.Multimap;
import com.skelril.nitro.registry.ItemTier;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

public interface ICustomTool extends ICustomItem, DegradableItem {
  // Skelril Methods

  // General

  @Override
  default String __getId() {
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
    return CreativeTabs.TOOLS;
  }

  // Repair
  ItemStack __getRepairItemStack();

  // Combat Data
  default int __getDamageForUseOnEntity() {
    return 2;
  }

  double __getHitPower();

  double __getAttackSpeed();

  // Enchantability
  int __getEnchantability();

  // Block Modification Data

  default int __getDamageForUseOnBlock() {
    return 1;
  }

  ItemTier __getHarvestTier();

  float __getSpecializedSpeed();

  default float __getGeneralizedSpeed() {
    return 1.0F;
  }

  Collection<Block> __getEffectiveBlocks();

  // Native compatibility methods

  boolean __superGetIsRepairable(ItemStack toRepair, ItemStack repair);

  Multimap<String, AttributeModifier> __superGetItemAttributeModifiers(EntityEquipmentSlot equipmentSlot);

  int __superGetHarvestLevel(ItemStack stack, String toolClass, @Nullable EntityPlayer player, @Nullable IBlockState blockState);

  Set<String> __superGetToolClasses(ItemStack stack);

  // Modified Native ItemTool methods

  // TODO Use an AT
  UUID ATTACK_DAMAGE_MODIFIER = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
  UUID ATTACK_SPEED_MODIFIER = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");

  default float getStrVsBlock(ItemStack stack, IBlockState state) {
    return __getEffectiveBlocks().contains(state.getBlock()) ? __getSpecializedSpeed() : __getGeneralizedSpeed();
  }

  /**
   * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
   * the damage on the stack.
   *
   * @param target   The Entity being hit
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
    if ((double) state.getBlockHardness(worldIn, pos) != 0.0D) {
      stack.damageItem(1, entityLiving);
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
   * @param repair   The ItemStack that should repair this Item (leather for leather armor, etc.)
   */
  default boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
    ItemStack mat = __getRepairItemStack();
    if (mat != null && net.minecraftforge.oredict.OreDictionary.itemMatches(mat, repair, false)) {
      return true;
    }
    return __superGetIsRepairable(toRepair, repair);
  }

  /**
   * Gets a map of item attribute modifiers, used by ItemSword to increase hit damage.
   */
  @SuppressWarnings("unchecked")
  default Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
    Multimap<String, AttributeModifier> multimap = __superGetItemAttributeModifiers(equipmentSlot);

    if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
      multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", __getHitPower(), 0));
      multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", __getAttackSpeed(), 0));
    }

    return multimap;
  }

  // Modified Forge ItemTool methods

  default int getHarvestLevel(ItemStack stack, String toolClass, @Nullable EntityPlayer player, @Nullable IBlockState blockState) {
    int level = __superGetHarvestLevel(stack, toolClass, player, blockState);
    if (level == -1 && toolClass != null && toolClass.equals(__getToolClass())) {
      return __getHarvestTier().getHarvestLevel();
    } else {
      return level;
    }
  }

  default Set<String> getToolClasses(ItemStack stack) {
    return __getToolClass() != null ? com.google.common.collect.ImmutableSet.of(__getToolClass()) : __superGetToolClasses(
        stack
    );
  }
}
