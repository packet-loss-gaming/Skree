/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.global.theforge;

import org.spongepowered.api.item.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TheForgeConfig {
  private List<String> sourceItemWhitelist = new ArrayList<>();

  public boolean isCompatibleWith(ItemStack itemStack) {
    return sourceItemWhitelist.contains(itemStack.getItem().getId());
  }
}
