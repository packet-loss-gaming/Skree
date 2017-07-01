/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.ability;

import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import javax.annotation.Nullable;

public class UsedItemApplicabilityTest implements AbilityApplicabilityTest {
  private String itemId;

  public UsedItemApplicabilityTest(String itemId) {
    this.itemId = itemId;
  }

  @Override
  public boolean test(Living sourceEntity, @Nullable ItemStackSnapshot usedStack) {
    return usedStack != null && usedStack.getType().getId().equals(itemId);
  }
}
