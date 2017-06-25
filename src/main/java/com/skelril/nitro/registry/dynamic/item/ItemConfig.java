/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.item;

import com.google.common.collect.Lists;
import com.skelril.nitro.registry.dynamic.ability.AbilityGroup;
import com.skelril.nitro.registry.dynamic.ability.HeldItemApplicabilityTest;
import org.spongepowered.api.entity.living.Living;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class ItemConfig {
  private String id;
  private List<AbilityGroup> abilityGroups = new ArrayList<>();

  private int enchantability;

  public String getID() {
    return id;
  }

  public List<AbilityGroup> getAbilityGroups() {
    return abilityGroups;
  }

  public List<String> getMeshDefinitions() {
    return Lists.newArrayList(getID());
  }

  public int getEnchantability() {
    return enchantability;
  }

  public Predicate<Living> getApplicabilityTest() {
    return new HeldItemApplicabilityTest(id);
  }
}
