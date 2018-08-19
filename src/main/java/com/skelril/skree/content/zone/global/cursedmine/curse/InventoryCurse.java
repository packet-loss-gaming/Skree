/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.global.cursedmine.curse;

import com.google.common.collect.Lists;
import com.skelril.nitro.item.ItemDropper;
import com.skelril.nitro.probability.Probability;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;
import java.util.function.Consumer;

import static com.skelril.nitro.item.ItemComparisonUtil.isSimilar;
import static com.skelril.nitro.item.ItemStackFactory.newItemStack;

public class InventoryCurse implements Consumer<Player> {

  private ItemType targetType;
  private int targetAmt;

  public InventoryCurse(ItemType targetType, int targetAmt) {
    this.targetType = targetType;
    this.targetAmt = targetAmt;
  }

  @Override
  public void accept(Player player) {
    ItemStack stack = newItemStack(targetType, Probability.getRandom(targetAmt));
    Optional<ItemStack> optHeld = player.getItemInHand(HandTypes.MAIN_HAND);
    if (optHeld.isPresent() && !isSimilar(optHeld.get(), stack)) {
      new ItemDropper(player.getLocation()).dropStacks(Lists.newArrayList(optHeld.get()));
    }
    player.setItemInHand(HandTypes.MAIN_HAND, stack);
  }
}
