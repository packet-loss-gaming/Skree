/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry;

public abstract class ItemTier {

  private String tierName;

  public ItemTier(String tierName) {
    this.tierName = tierName;
  }

  public String getTierName() {
    return tierName;
  }

  public abstract int getHarvestLevel();

  public abstract int getDurability();

  public abstract float getEfficienyOnProperMaterial();

  public abstract float getDamage();

  public abstract int getEnchantability();
}
