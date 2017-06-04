/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry;

import net.minecraft.item.Item;

public class VanillaItemTier extends ItemTier {

  private final Item.ToolMaterial material;

  public VanillaItemTier(String tierName, Item.ToolMaterial material) {
    super(tierName);
    this.material = material;
  }

  @Override
  public int getHarvestLevel() {
    return material.getHarvestLevel();
  }

  @Override
  public int getDurability() {
    return material.getMaxUses();
  }

  @Override
  public float getEfficienyOnProperMaterial() {
    return material.getEfficiencyOnProperMaterial();
  }

  @Override
  public float getDamage() {
    return material.getDamageVsEntity();
  }

  @Override
  public int getEnchantability() {
    return material.getEnchantability();
  }
}
