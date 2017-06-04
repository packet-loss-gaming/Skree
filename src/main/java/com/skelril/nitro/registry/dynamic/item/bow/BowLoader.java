/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.item.bow;

import com.skelril.nitro.registry.dynamic.item.GameIntegrator;
import com.skelril.nitro.registry.dynamic.item.ItemLoader;
import com.skelril.nitro.registry.dynamic.item.ability.AbilityRegistry;

public class BowLoader extends ItemLoader<LoadedBow, BowConfig> {
  public BowLoader(GameIntegrator integrator, AbilityRegistry registry) {
    super(integrator, registry);
  }

  @Override
  public LoadedBow constructFromConfig(BowConfig config) {
    return new LoadedBow(config);
  }

  @Override
  public Class<BowConfig> getConfigClass() {
    return BowConfig.class;
  }
}
