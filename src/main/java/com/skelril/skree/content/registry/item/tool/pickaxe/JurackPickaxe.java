/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.tool.pickaxe;

import com.skelril.nitro.registry.ItemTier;
import com.skelril.nitro.registry.item.ItemTiers;
import com.skelril.nitro.registry.item.ItemToolTypes;
import com.skelril.nitro.registry.item.pickaxe.CustomPickaxe;
import net.minecraft.item.ItemStack;

import static com.skelril.nitro.item.ItemStackFactory.newItemStack;

public class JurackPickaxe extends CustomPickaxe {
  @Override
  public String __getType() {
    return "jurack";
  }

  @Override
  public ItemStack __getRepairItemStack() {
    return (ItemStack) (Object) newItemStack("skree:jurack");
  }

  @Override
  public double __getHitPower() {
    return ItemTiers.JURACK.getDamage() + ItemToolTypes.PICKAXE.getBaseDamage();
  }

  @Override
  public int __getEnchantability() {
    return ItemTiers.JURACK.getEnchantability();
  }

  @Override
  public ItemTier __getHarvestTier() {
    return ItemTiers.JURACK;
  }

  @Override
  public float __getSpecializedSpeed() {
    return ItemTiers.JURACK.getEfficienyOnProperMaterial();
  }

  @Override
  public int __getMaxUses() {
    return ItemTiers.JURACK.getDurability();
  }
}
