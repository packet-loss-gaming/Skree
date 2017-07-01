/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.ability.grouptype;

import com.skelril.nitro.probability.Probability;
import com.skelril.nitro.registry.dynamic.ability.AbilityApplicabilityTest;
import com.skelril.nitro.registry.dynamic.ability.AbilityCluster;
import com.skelril.nitro.registry.dynamic.ability.AbilityCooldownHandler;
import com.skelril.nitro.registry.dynamic.ability.PointOfContact;

import java.util.List;

public class PointOfContactCluster extends AbilityCluster {
  private List<PointOfContact> pointOfContact;

  public List<PointOfContact> getPointOfContactAbilities() {
    return pointOfContact;
  }

  public PointOfContact getNextAbilityToRun() {
    return Probability.pickOneOf(getPointOfContactAbilities());
  }

  @Override
  public ClusterListener getListenerFor(AbilityApplicabilityTest applicabilityTest, AbilityCooldownHandler coolDownManager) {
    return new PointOfContactClusterListener(this, applicabilityTest, coolDownManager);
  }
}
