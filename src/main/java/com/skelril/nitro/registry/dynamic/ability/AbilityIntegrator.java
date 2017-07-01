/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.ability;

import com.skelril.nitro.registry.dynamic.ability.grouptype.ClusterListener;
import org.spongepowered.api.Sponge;

import java.util.List;

public class AbilityIntegrator {
  private final AbilityCooldownManager cooldownManager = new AbilityCooldownManager();

  private void processAbilityGroup(Object mod, AbilityApplicabilityTest applicabilityTest, AbilityGroup abilityGroup) {
    AbilityCooldownHandler cooldownHandler = new AbilityCooldownHandler(abilityGroup.getCoolDown(), cooldownManager);
    abilityGroup.getClusters().forEach((cluster) -> {
      ClusterListener listener = cluster.getListenerFor(applicabilityTest, cooldownHandler);
      Sponge.getEventManager().registerListeners(mod, listener);
    });
  }

  public void processAbilityGroups(Object mod, AbilityApplicabilityTest applicabilityTest, List<AbilityGroup> abilityGroups) {
    abilityGroups.forEach(abilityGroup -> {
      processAbilityGroup(mod, applicabilityTest, abilityGroup);
    });
  }
}
