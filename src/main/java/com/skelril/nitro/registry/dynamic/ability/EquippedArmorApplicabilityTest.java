/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.ability;

import org.spongepowered.api.entity.ArmorEquipable;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Optional;

public class EquippedArmorApplicabilityTest implements AbilityApplicabilityTest {
  private final Collection<String> matchingHelmets;
  private final Collection<String> matchingChestplates;
  private final Collection<String> matchingLeggings;
  private final Collection<String> matchingBoots;

  public EquippedArmorApplicabilityTest(Collection<String> matchingHelmets, Collection<String> matchingChestplates,
                                        Collection<String> matchingLeggings, Collection<String> matchingBoots) {
    this.matchingHelmets = matchingHelmets;
    this.matchingChestplates = matchingChestplates;
    this.matchingLeggings = matchingLeggings;
    this.matchingBoots = matchingBoots;
  }

  @Override
  public boolean test(Living sourceEntity, @Nullable ItemStackSnapshot usedStack) {
    if (!(sourceEntity instanceof Player)) {
      return false;
    }

    Optional<ItemStack> optHelmet = ((ArmorEquipable) sourceEntity).getHelmet();
    Optional<ItemStack> optChestplate = ((ArmorEquipable) sourceEntity).getChestplate();
    Optional<ItemStack> optLeggings = ((ArmorEquipable) sourceEntity).getLeggings();
    Optional<ItemStack> optBoots = ((ArmorEquipable) sourceEntity).getBoots();

    if (!optHelmet.isPresent()) {
      return false;
    }

    if (!optChestplate.isPresent()) {
      return false;
    }

    if (!optLeggings.isPresent()) {
      return false;
    }

    if (!optBoots.isPresent()) {
      return false;
    }

    String helmetType = optHelmet.get().getItem().getId();
    String chestplateType = optChestplate.get().getItem().getId();
    String leggingsType = optLeggings.get().getItem().getId();
    String bootsType = optBoots.get().getItem().getId();

    return matchingHelmets.contains(helmetType) && matchingChestplates.contains(chestplateType)
        && matchingLeggings.contains(leggingsType) && matchingBoots.contains(bootsType);
  }
}
