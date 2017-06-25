/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.item.armor;

import com.skelril.nitro.registry.dynamic.ability.AbilityRegistry;
import com.skelril.nitro.registry.dynamic.item.GameIntegrator;
import com.skelril.nitro.registry.dynamic.item.ItemLoader;

public class ChestplateLoader extends ItemLoader<LoadedChestplate, ChestplateConfig> {
  public ChestplateLoader(GameIntegrator integrator, AbilityRegistry registry) {
    super(integrator, registry);
  }

  @Override
  public LoadedChestplate constructFromConfig(ChestplateConfig config) {
    return new LoadedChestplate(config);
  }

  @Override
  public Class<ChestplateConfig> getConfigClass() {
    return ChestplateConfig.class;
  }
}