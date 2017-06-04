/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.armor;

import com.skelril.nitro.registry.Craftable;
import com.skelril.nitro.registry.item.armor.CustomLeggings;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

import static com.skelril.nitro.item.ItemStackFactory.newItemStack;

public class CrystalLeggings extends CustomLeggings implements Craftable {
  @Override
  public int __getMaxUsesBaseModifier() {
    return 60;
  }

  @Override
  public String __getType() {
    return "crystal";
  }

  @Override
  public ItemStack __getRepairItemStack() {
    return (ItemStack) (Object) newItemStack("skree:sea_crystal");
  }

  @Override
  public int __getEnchantability() {
    return 10;
  }

  @Override
  public void registerRecipes() {
    GameRegistry.addRecipe(
        new ItemStack(this),
        "AAA",
        "A A",
        "A A",
        'A', newItemStack("skree:sea_crystal")
    );
  }
}
