/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.item;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.stream.Collectors;

public class ItemDropper {

  private final Location<World> location;

  public ItemDropper(Location<World> location) {
    this.location = location;
  }

  public World getExtent() {
    return location.getExtent();
  }

  public Vector3d getPos() {
    return location.getPosition();
  }

  public void dropItem(ItemStackSnapshot snapshot) {
    Item item = (Item) getExtent().createEntity(EntityTypes.ITEM, getPos());
    item.offer(Keys.REPRESENTED_ITEM, snapshot);
    getExtent().spawnEntity(item);
  }

  public void dropStackSnapshots(Collection<ItemStackSnapshot> stacks) {
    for (ItemStackSnapshot stack : stacks) {
      dropItem(stack);
    }
  }

  public void dropStacks(Collection<ItemStack> stacks) {
    dropStackSnapshots(stacks.stream().map(ItemStack::createSnapshot).collect(Collectors.toList()));
  }
}
