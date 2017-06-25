/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.ability.grouptype;

import com.skelril.nitro.probability.Probability;
import com.skelril.nitro.registry.dynamic.ability.AbilityCluster;
import com.skelril.nitro.registry.dynamic.ability.AbilityCooldownHandler;
import com.skelril.nitro.registry.dynamic.ability.DefensiveAbility;
import org.spongepowered.api.entity.living.Living;

import java.util.List;
import java.util.function.Predicate;

public class DefensiveCluster extends AbilityCluster {
  private List<DefensiveAbility> defensive;

  public List<DefensiveAbility> getDefensiveAbilities() {
    return defensive;
  }

  public DefensiveAbility getNextAbilityToUse() {
    return Probability.pickOneOf(getDefensiveAbilities());
  }

  @Override
  public ClusterListener getListenerFor(Predicate<Living> applicabilityTest, AbilityCooldownHandler cooldownHandler) {
    return new DefensiveClusterListener(this, applicabilityTest, cooldownHandler);
  }
}
