/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.item;

public class ItemToolType {

  private int baseDamage;

  public ItemToolType(int baseDamage) {
    this.baseDamage = baseDamage;
  }

  public int getBaseDamage() {
    return baseDamage;
  }
}
