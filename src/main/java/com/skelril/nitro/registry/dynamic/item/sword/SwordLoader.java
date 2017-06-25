/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.item.sword;

import com.skelril.nitro.registry.dynamic.ability.AbilityRegistry;
import com.skelril.nitro.registry.dynamic.item.GameIntegrator;
import com.skelril.nitro.registry.dynamic.item.ItemLoader;

public class SwordLoader extends ItemLoader<LoadedSword, SwordConfig> {
  public SwordLoader(GameIntegrator integrator, AbilityRegistry registry) {
    super(integrator, registry);
  }

  @Override
  public LoadedSword constructFromConfig(SwordConfig config) {
    return new LoadedSword(config);
  }

  @Override
  public Class<SwordConfig> getConfigClass() {
    return SwordConfig.class;
  }
}
