/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.seteffect;

import com.google.gson.GsonBuilder;
import com.skelril.nitro.registry.dynamic.Loader;
import com.skelril.nitro.registry.dynamic.ability.AbilityEnabledGsonBuilder;
import com.skelril.nitro.registry.dynamic.ability.AbilityRegistry;
import com.skelril.nitro.registry.dynamic.item.GameIntegrator;
import org.spongepowered.api.entity.living.Living;

import java.util.function.Predicate;

public abstract class SetEffectLoader<T extends SetEffectConfig> implements Loader<T> {
  private GameIntegrator integrator;
  private AbilityRegistry abilityRegistry;

  public SetEffectLoader(GameIntegrator integrator, AbilityRegistry abilityRegistry) {
    this.integrator = integrator;
    this.abilityRegistry = abilityRegistry;
  }

  public abstract Predicate<Living> constructPredicateFromConfig(T configObject);

  @Override
  public void load(T configObject) {
    integrator.registerAbilities(constructPredicateFromConfig(configObject), configObject.getAbilityGroups());
  }

  @Override
  public GsonBuilder getGsonBuilder() {
    GsonBuilder parentBuilder = Loader.super.getGsonBuilder();

    return AbilityEnabledGsonBuilder.getGsonBuilder(parentBuilder, abilityRegistry);
  }
}
