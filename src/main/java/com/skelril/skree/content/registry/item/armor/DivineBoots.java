/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.armor;

import com.skelril.nitro.registry.Craftable;
import com.skelril.nitro.registry.item.armor.CustomBoots;
import com.skelril.skree.content.registry.item.CustomItemTypes;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

import static com.skelril.nitro.item.ItemStackFactory.newItemStack;

public class DivineBoots extends CustomBoots implements Craftable {
  @Override
  public int __getMaxUsesBaseModifier() {
    return 60;
  }

  @Override
  public String __getType() {
    return "divine";
  }

  @Override
  public ItemStack __getRepairItemStack() {
    return new ItemStack(CustomItemTypes.GUARDIAN_BOOTS);
  }

  @Override
  public int __getEnchantability() {
    return 10;
  }

  @Override
  public void registerRecipes() {
    GameRegistry.addShapelessRecipe(
        new ItemStack(this),
        new ItemStack(CustomItemTypes.GUARDIAN_BOOTS),
        newItemStack("skree:unstable_catalyst")
    );
  }
}