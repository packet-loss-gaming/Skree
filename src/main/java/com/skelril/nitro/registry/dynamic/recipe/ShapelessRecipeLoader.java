/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.recipe;

import com.skelril.nitro.registry.dynamic.Loader;

public class ShapelessRecipeLoader implements Loader<ShapelessRecipeConfig> {
  @Override
  public void load(ShapelessRecipeConfig config) {
    config.registerRecipie();
  }

  @Override
  public Class<ShapelessRecipeConfig> getConfigClass() {
    return ShapelessRecipeConfig.class;
  }
}
