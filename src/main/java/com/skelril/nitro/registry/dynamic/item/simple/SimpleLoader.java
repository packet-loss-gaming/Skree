/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.item.simple;

import com.skelril.nitro.registry.dynamic.ability.AbilityRegistry;
import com.skelril.nitro.registry.dynamic.item.GameIntegrator;
import com.skelril.nitro.registry.dynamic.item.ItemLoader;

public class SimpleLoader extends ItemLoader<LoadedSimpleItem, SimpleItemConfig> {
  public SimpleLoader(GameIntegrator integrator, AbilityRegistry abilityRegistry) {
    super(integrator, abilityRegistry);
  }

  @Override
  public LoadedSimpleItem constructFromConfig(SimpleItemConfig config) {
    return new LoadedSimpleItem(config);
  }

  @Override
  public Class<SimpleItemConfig> getConfigClass() {
    return SimpleItemConfig.class;
  }
}
