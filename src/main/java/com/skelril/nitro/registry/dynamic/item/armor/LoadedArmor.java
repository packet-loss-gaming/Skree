/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.item.armor;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

class LoadedArmor extends ItemArmor {
  private ArmorConfig config;

  public LoadedArmor(ArmorConfig config, EntityEquipmentSlot equipmentSlotIn) {
    super(ArmorMaterial.DIAMOND, 4, equipmentSlotIn); // 4 is allegedly used for net.minecraft.client.renderer.entity.RenderPlayer
    // to determine which model to use
    // This just bases everything off the diamond armor model

    this.config = config;

    setMaxDamage();
  }

  private void setMaxDamage() {
    int armorAdjustment = MAX_DAMAGE_ARRAY[armorType.getIndex()];
    int baseModifier = config.getMaxUsesBaseModifier();

    this.setMaxDamage(armorAdjustment * baseModifier);
  }

  @SideOnly(Side.CLIENT)
  @Override
  public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
    // Derived from net.minecraft.client.renderer.entity.layers.LayerArmorBase$getArmorResource
    String texture = config.getSetName();
    String domain = "skree";

    return String.format("%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, (slot == EntityEquipmentSlot.LEGS ? 2 : 1), "");
  }

  @Override
  public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
    ItemStack mat = config.getRepairItemStack();
    return mat != null && net.minecraftforge.oredict.OreDictionary.itemMatches(mat, repair, false);
  }

  @Override
  public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
    Multimap<String, AttributeModifier> multimap = HashMultimap.create();

    if (equipmentSlot == armorType) {
      multimap.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Armor modifier", config.getDamageReducationAmount(), 0));
      multimap.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Armor toughness", config.getToughness(), 0));
    }

    return multimap;
  }
}
