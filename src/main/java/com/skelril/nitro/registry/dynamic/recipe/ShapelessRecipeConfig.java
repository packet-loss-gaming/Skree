/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.recipe;

import com.skelril.nitro.registry.dynamic.ItemStackConfig;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.List;

public class ShapelessRecipeConfig extends CraftingRecipeConfig {
  private List<ItemStackConfig> requiredItemStacks;

  public void registerRecipie() {
    GameRegistry.addShapelessRecipe(
        craftedItem.toNSMStack(),
        requiredItemStacks.stream().map(ItemStackConfig::toNSMStack).toArray()
    );
  }
}
