/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.seteffect;

import com.skelril.nitro.registry.dynamic.ability.AbilityRegistry;
import com.skelril.nitro.registry.dynamic.ability.EquippedArmorApplicabilityTest;
import com.skelril.nitro.registry.dynamic.item.GameIntegrator;
import org.spongepowered.api.entity.living.Living;

import java.util.function.Predicate;

public class ArmorSetEffectLoader extends SetEffectLoader<ArmorSetEffectConfig> {
  public ArmorSetEffectLoader(GameIntegrator integrator, AbilityRegistry abilityRegistry) {
    super(integrator, abilityRegistry);
  }

  @Override
  public Predicate<Living> constructPredicateFromConfig(ArmorSetEffectConfig configObject) {
    return new EquippedArmorApplicabilityTest(
        configObject.getMatchingHelmets(),
        configObject.getMatchingChestplates(),
        configObject.getMatchingLeggings(),
        configObject.getMatchingBoots()
    );
  }

  @Override
  public Class<ArmorSetEffectConfig> getConfigClass() {
    return ArmorSetEffectConfig.class;
  }
}
