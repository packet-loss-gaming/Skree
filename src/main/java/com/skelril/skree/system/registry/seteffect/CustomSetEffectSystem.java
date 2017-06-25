/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.registry.seteffect;

import com.skelril.nitro.registry.dynamic.LoaderRegistry;
import com.skelril.nitro.registry.dynamic.ability.AbilityRegistry;
import com.skelril.nitro.registry.dynamic.item.GameIntegrator;
import com.skelril.nitro.registry.dynamic.seteffect.ArmorSetEffectLoader;
import com.skelril.skree.system.registry.AbstractCustomRegistrySystem;

public class CustomSetEffectSystem extends AbstractCustomRegistrySystem {
  private final GameIntegrator gameIntegrator;
  private final AbilityRegistry abilityRegistry;

  public CustomSetEffectSystem(GameIntegrator gameIntegrator, AbilityRegistry abilityRegistry) {
    super("/registry/seteffects/");
    this.gameIntegrator = gameIntegrator;
    this.abilityRegistry = abilityRegistry;
  }

  @Override
  public void init() {
    LoaderRegistry dynamicRecipeRegistry = new LoaderRegistry();
    loadFromResources(getResource -> {
      dynamicRecipeRegistry.registerLoader(new ArmorSetEffectLoader(gameIntegrator, abilityRegistry), getResource.apply("armor"));
      dynamicRecipeRegistry.loadAll();
    });
  }
}
