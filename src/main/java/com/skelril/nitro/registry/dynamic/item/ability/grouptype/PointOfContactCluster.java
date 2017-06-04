/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.item.ability.grouptype;

import com.skelril.nitro.probability.Probability;
import com.skelril.nitro.registry.dynamic.item.ability.AbilityCluster;
import com.skelril.nitro.registry.dynamic.item.ability.AbilityCooldownHandler;
import com.skelril.nitro.registry.dynamic.item.ability.PointOfContact;

import java.util.List;

public class PointOfContactCluster extends AbilityCluster {
  private List<PointOfContact> pointOfContact;

  public List<PointOfContact> getPointOfContactAbilties() {
    return pointOfContact;
  }

  public PointOfContact getNextAbilityToRun() {
    return Probability.pickOneOf(getPointOfContactAbilties());
  }

  @Override
  public ClusterListener getListenerFor(String itemID, AbilityCooldownHandler coolDownManager) {
    return new PointOfContactClusterListener(this, itemID, coolDownManager);
  }
}
