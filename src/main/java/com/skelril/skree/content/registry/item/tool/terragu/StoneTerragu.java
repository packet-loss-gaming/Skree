/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.tool.terragu;

import com.skelril.nitro.registry.Craftable;
import com.skelril.nitro.registry.ItemTier;
import com.skelril.nitro.registry.item.ItemTiers;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;

import static com.skelril.nitro.item.ItemStackFactory.newItemStack;

public class StoneTerragu extends CustomTerragu implements Craftable {
  @Override
  public String __getType() {
    return "stone";
  }

  @Override
  public ItemStack __getRepairItemStack() {
    return null;
  }

  @Override
  public double __getHitPower() {
    return ItemTiers.STONE.getDamage();
  }

  @Override
  public int __getEnchantability() {
    return ItemTiers.STONE.getEnchantability();
  }

  @Override
  public ItemTier __getHarvestTier() {
    return ItemTiers.STONE;
  }

  @Override
  public float __getSpecializedSpeed() {
    return ItemTiers.STONE.getEfficienyOnProperMaterial();
  }

  @Override
  public int __getMaxUses() {
    return ItemTiers.STONE.getDurability() * 10;
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

  @Override
  public void registerRecipes() {
    GameRegistry.addShapelessRecipe(
        new ItemStack(this),
        new ItemStack(Items.STONE_PICKAXE),
        new ItemStack(Items.STONE_AXE),
        new ItemStack(Items.STONE_SHOVEL),
        newItemStack("skree:unstable_catalyst")
    );
  }
}
