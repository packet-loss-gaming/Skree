/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.ability;

import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.ArmorEquipable;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;
import java.util.function.Predicate;

public class HeldItemApplicabilityTest implements Predicate<Living> {
  private String itemId;

  public HeldItemApplicabilityTest(String itemId) {
    this.itemId = itemId;
  }

  @Override
  public boolean test(Living sourceEntity) {
    if (!(sourceEntity instanceof ArmorEquipable)) {
      return false;
    }

    Optional<ItemStack> optHeldItem = ((ArmorEquipable) sourceEntity).getItemInHand(HandTypes.MAIN_HAND);
    return optHeldItem.isPresent() && optHeldItem.get().getItem().getId().equals(itemId);
  }
}
