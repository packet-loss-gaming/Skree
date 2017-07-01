/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.ability.grouptype;

import com.skelril.nitro.registry.dynamic.ability.AbilityCooldownHandler;
import com.skelril.nitro.registry.dynamic.ability.SpecialAttack;
import org.spongepowered.api.entity.living.Living;

import java.util.List;
import java.util.function.Predicate;

public class RangedSpecialAttackCluster extends SpecialAttackCluster {
  private List<SpecialAttack> rangedAttacks;

  @Override
  public List<SpecialAttack> getSpecialAttacks() {
    return rangedAttacks;
  }

  @Override
  public ClusterListener getListenerFor(Predicate<Living> applicabilityTest, AbilityCooldownHandler cooldownHandler) {
    return new RangedSpecialAttackClusterListener(this, applicabilityTest, cooldownHandler);
  }

}
