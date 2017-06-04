/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.registry.recipe;

import com.skelril.nitro.registry.dynamic.LoaderRegistry;
import com.skelril.nitro.registry.dynamic.recipe.CookedRecipieLoader;
import com.skelril.nitro.registry.dynamic.recipe.ShapedRecipeLoader;
import com.skelril.nitro.registry.dynamic.recipe.ShapelessRecipeLoader;
import com.skelril.skree.system.registry.AbstractCustomRegistrySystem;

public class CustomRecipeSystem extends AbstractCustomRegistrySystem {
  public CustomRecipeSystem() {
    super("/registry/recipes/");
  }

  @Override
  public void associate() {
    LoaderRegistry dynamicRecipeRegistry = new LoaderRegistry();
    loadFromResources(getResource -> {
      dynamicRecipeRegistry.registerLoader(new CookedRecipieLoader(), getResource.apply("cooked"));
      dynamicRecipeRegistry.registerLoader(new ShapedRecipeLoader(), getResource.apply("shaped"));
      dynamicRecipeRegistry.registerLoader(new ShapelessRecipeLoader(), getResource.apply("shapeless"));
      dynamicRecipeRegistry.loadAll();
    });
  }
}
