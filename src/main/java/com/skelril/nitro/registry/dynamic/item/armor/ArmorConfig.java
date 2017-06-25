/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.item.armor;

import com.skelril.nitro.registry.dynamic.item.RepairableItemConfig;

public abstract class ArmorConfig extends RepairableItemConfig {
  private String setName;
  private int maxUsesBaseModifier;

  public String getSetName() {
    return setName;
  }

  public int getMaxUsesBaseModifier() {
    return maxUsesBaseModifier;
  }

  public abstract double getDamageReducationAmount();

  public abstract double getToughness();
}
