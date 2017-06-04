/*
 * This Source Code Form is subject to the terms of the Mozilla default
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.item.armor;

import com.google.common.collect.Multimap;
import com.skelril.nitro.registry.item.DegradableItem;
import com.skelril.nitro.registry.item.ICustomItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public interface ICustomArmor extends ICustomItem, DegradableItem {
  UUID[] ARMOR_MODIFIERS = new UUID[] {
      UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"),
      UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"),
      UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"),
      UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")
  };

  int[] MAX_DAMAGE_ARRAY = new int[] {13, 15, 16, 11};

  // Skelril Methods

  // General

  @Override
  default int __getMaxStackSize() {
    return 1;
  }

  @Override
  default int __getMaxUses(ItemStack stack) {
    return __getMaxUses(EntityLiving.getSlotForItemStack(stack));
  }

  default int __getMaxUses(EntityEquipmentSlot slot) {
    return MAX_DAMAGE_ARRAY[slot.getIndex()] * __getMaxUsesBaseModifier();
  }

  @Override
  @Deprecated
  default int __getMaxUses() {
    throw new UnsupportedOperationException();
  }

  EntityEquipmentSlot __getSlotType();

  int __getMaxUsesBaseModifier();

  String __getType();

  @Override
  default CreativeTabs __getCreativeTab() {
    return CreativeTabs.COMBAT;
  }

  // Repair
  ItemStack __getRepairItemStack();

  // Combat Data
  int __getDamageReductionAmount();

  int __getToughness();

  // Enchantability
  int __getEnchantability();

  @Override
  default String __getId() {
    return __getType() + "_" + __getArmorCategory();
  }

  String __getArmorCategory();

  // Native compatibility methods

  ItemArmor.ArmorMaterial __superGetArmorMaterial();

  boolean __superGetIsRepairable(ItemStack toRepair, ItemStack repair);

  Multimap<String, AttributeModifier> __superGetItemAttributeModifiers(EntityEquipmentSlot equipmentSlot);

  // Modified Native ItemArmor methods

  default String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
    // Derived from net.minecraft.client.renderer.entity.layers.LayerArmorBase$getArmorResource
    String texture = __getType();
    String domain = "skree";

    return String.format("%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, (slot == EntityEquipmentSlot.LEGS ? 2 : 1), "");
  }

  default int getColorFromItemStack(ItemStack stack, int renderPass) {
    if (renderPass > 0) {
      return 16777215;
    } else {
      int j = this.getColor(stack);

      if (j < 0) {
        j = 16777215;
      }

      return j;
    }
  }


  /**
   * Return the enchantability factor of the item, most of the time is based on material.
   */
  default int getItemEnchantability() {
    return __getEnchantability();
  }

  /**
   * Return the armor material for this armor item.
   */
  default ItemArmor.ArmorMaterial getArmorMaterial() {
    return __superGetArmorMaterial();
  }

  /**
   * Return whether the specified armor ItemStack has a color.
   */
  default boolean hasColor(ItemStack stack) {
    return false;
  }

  /**
   * Return the color for the specified armor ItemStack.
   */
  default int getColor(ItemStack stack) {
    return -1;
  }

  /**
   * Remove the color from the specified armor ItemStack.
   */
  default void removeColor(ItemStack stack) {

  }

  /**
   * Sets the color of the specified armor ItemStack
   */
  default void setColor(ItemStack stack, int color) {
    throw new UnsupportedOperationException("Can\'t dye non-leather!");
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

  default Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
    Multimap<String, AttributeModifier> multimap = __superGetItemAttributeModifiers(equipmentSlot);

    if (equipmentSlot == __getSlotType()) {
      multimap.put(SharedMonsterAttributes.ARMOR.getAttributeUnlocalizedName(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Armor modifier", (double) __getDamageReductionAmount(), 0));
      multimap.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getAttributeUnlocalizedName(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Armor toughness", (double) __getToughness(), 0));
    }

    return multimap;
  }
}
