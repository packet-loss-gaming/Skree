/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.item.food;

import com.skelril.nitro.registry.dynamic.item.GameIntegrator;
import com.skelril.nitro.registry.dynamic.item.ItemLoader;
import com.skelril.nitro.registry.dynamic.item.ability.AbilityRegistry;

public class FoodLoader extends ItemLoader<LoadedFood, FoodConfig> {
  public FoodLoader(GameIntegrator integrator, AbilityRegistry abilityRegistry) {
    super(integrator, abilityRegistry);
  }

  @Override
  public LoadedFood constructFromConfig(FoodConfig config) {
    return new LoadedFood(config);
  }

  @Override
  public Class<FoodConfig> getConfigClass() {
    return FoodConfig.class;
  }
}
