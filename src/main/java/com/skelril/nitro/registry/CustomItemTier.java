/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry;

public class CustomItemTier extends ItemTier {
  private final int harvesetLevel;
  private final int durability;
  private final float properMaterialEfficieny;
  private final float damage;
  private final int enchantability;

  public CustomItemTier(String tierName, int harestLevel, int durability, float properMaterialEfficieny, float damage, int enchantability) {
    super(tierName);
    this.harvesetLevel = harestLevel;
    this.durability = durability;
    this.properMaterialEfficieny = properMaterialEfficieny;
    this.damage = damage;
    this.enchantability = enchantability;
  }

  @Override
  public int getHarvestLevel() {
    return harvesetLevel;
  }

  @Override
  public int getDurability() {
    return durability;
  }

  @Override
  public float getEfficienyOnProperMaterial() {
    return properMaterialEfficieny;
  }

  @Override
  public float getDamage() {
    return damage;
  }

  @Override
  public int getEnchantability() {
    return enchantability;
  }
}
