/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.recipe;

import com.skelril.nitro.registry.dynamic.ItemStackConfig;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ShapedRecipeConfig extends CraftingRecipeConfig {
  private List<String> craftingRows;
  private Map<Character, ItemStackConfig> mappedItemStacks;

  public void registerRecipie() {
    List<Object> argList = new ArrayList<>();
    argList.addAll(craftingRows);
    mappedItemStacks.forEach((character, config) -> {
      argList.add(character);
      argList.add(config.toNSMStack());
    });

    GameRegistry.addRecipe(
        craftedItem.toNSMStack(),
        argList.toArray(new Object[argList.size()])
    );
  }
}
