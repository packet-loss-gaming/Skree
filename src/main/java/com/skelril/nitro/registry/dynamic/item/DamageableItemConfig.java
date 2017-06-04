/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.item;

import com.skelril.nitro.registry.dynamic.ItemStackConfig;
import net.minecraft.item.ItemStack;

public class DamageableItemConfig extends ItemConfig {
  private int maxUses;
  private ItemStackConfig repairItemStack;

  public int getMaxUses() {
    return maxUses;
  }

  public ItemStackConfig getRepairItemStackConfig() {
    return repairItemStack;
  }

  public ItemStack getRepairItemStack() {
    ItemStackConfig repairItemStack = getRepairItemStackConfig();
    return repairItemStack != null ? repairItemStack.toNSMStack() : null;
  }
}
