/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.item;

public class ToolConfig extends DamageableItemConfig {
  private double hitPower;
  private double attackSpeed;

  public double getHitPower() {
    return hitPower;
  }

  public double getAttackSpeed() {
    return attackSpeed;
  }
}
