/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.thebutchershop;


import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.animal.Animal;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.DropItemEvent;

import java.util.Optional;

import static com.skelril.nitro.item.ItemStackFactory.newItemStack;

public class TheButcherShopListener {
  private TheButcherShopManager manager;

  public TheButcherShopListener(TheButcherShopManager manager) {
    this.manager = manager;
  }

  @Listener
  public void onEntityDrop(DropItemEvent.Destruct event, @First Entity entity) {
    if (!(entity instanceof Animal)) {
      return;
    }

    Optional<TheButcherShopInstance> optInst = manager.getApplicableZone(entity);
    if (!optInst.isPresent()) {
      return;
    }

    event.getEntities().clear();

    Item item = (Item) entity.getLocation().createEntity(EntityTypes.ITEM);
    item.offer(Keys.REPRESENTED_ITEM, newItemStack("skree:unpackaged_meat").createSnapshot());

    event.getEntities().add(item);
  }
}
