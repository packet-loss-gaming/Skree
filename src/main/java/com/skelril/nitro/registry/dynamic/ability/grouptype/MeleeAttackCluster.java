/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.ability.grouptype;

import com.skelril.nitro.registry.dynamic.ability.AbilityApplicabilityTest;
import com.skelril.nitro.registry.dynamic.ability.AbilityCooldownHandler;
import com.skelril.nitro.registry.dynamic.ability.SpecialAttack;

import java.util.List;

public class MeleeAttackCluster extends SpecialAttackCluster {
  private List<SpecialAttack> meleeAttacks;

  @Override
  public List<SpecialAttack> getSpecialAttacks() {
    return meleeAttacks;
  }

  @Override
  public ClusterListener getListenerFor(AbilityApplicabilityTest applicabilityTest, AbilityCooldownHandler cooldownHandler) {
    return new MeleeAttackClusterListener(this, applicabilityTest, cooldownHandler);
  }
}
