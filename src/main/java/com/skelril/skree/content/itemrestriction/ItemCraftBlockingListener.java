/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.itemrestriction;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.type.Exclude;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Collection;
import java.util.Optional;

public class ItemCraftBlockingListener {
  private Collection<String> blockedItems;

  public ItemCraftBlockingListener(Collection<String> blockedItems) {
    this.blockedItems = blockedItems;
  }

  @Listener
  @Exclude(value = ChangeInventoryEvent.Held.class)
  public void onItemCraft(ChangeInventoryEvent event) {
    event.getTransactions().forEach((t) -> {
      if (blockedItems.contains(t.getFinal().getType().getId())) {
        Optional<Player> optPlayer = event.getCause().first(Player.class);
        if (optPlayer.isPresent()) {
          Player player = optPlayer.get();
          player.sendMessage(Text.of(TextColors.RED, "This item has been disabled."));
        }

        event.setCancelled(true);
      }
    });
  }
}
