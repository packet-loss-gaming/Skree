/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.tool.terragu;

import com.skelril.nitro.registry.ItemTier;
import com.skelril.nitro.registry.item.ItemTiers;
import net.minecraft.item.ItemStack;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;

public class WoodTerragu extends CustomTerragu {
  @Override
  public String __getType() {
    return "wooden";
  }

  @Override
  public ItemStack __getRepairItemStack() {
    return null;
  }

  @Override
  public double __getHitPower() {
    return ItemTiers.WOOD.getDamage();
  }

  @Override
  public int __getEnchantability() {
    return ItemTiers.WOOD.getEnchantability();
  }

  @Override
  public ItemTier __getHarvestTier() {
    return ItemTiers.WOOD;
  }

  @Override
  public float __getSpecializedSpeed() {
    return ItemTiers.WOOD.getEfficienyOnProperMaterial();
  }

  @Override
  public int __getMaxUses() {
    return ItemTiers.WOOD.getDurability() * 10;
  }

  @Listener
  public void process(InteractBlockEvent.Primary.MainHand event) {
    super.process(event);
  }

  @Listener
  public void process(InteractBlockEvent.Secondary.MainHand event) {
    super.process(event);
  }

  @Listener
  public void process(ChangeBlockEvent.Break event) {
    super.process(event);
  }
}
