/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.ability;

import com.skelril.nitro.registry.dynamic.ability.grouptype.ClusterListener;
import org.spongepowered.api.entity.living.Living;

import java.util.function.Predicate;

public abstract class AbilityCluster {
  public abstract ClusterListener getListenerFor(Predicate<Living> applicabilityTest, AbilityCooldownHandler coolDownManager);
}
