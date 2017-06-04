/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Creature;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.cause.entity.spawn.EntitySpawnCause;
import org.spongepowered.api.event.filter.cause.Named;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.function.Function;

public class ZoneCreatureDropBlocker<T> extends ZoneApplicableListener<T> {
  public ZoneCreatureDropBlocker(Function<Location<World>, Optional<T>> applicabilityFunct) {
    super(applicabilityFunct);
  }

  @Listener
  public void onEntityDrop(DropItemEvent.Destruct event, @Named(NamedCause.SOURCE) EntitySpawnCause spawnCause) {
    Entity entity = spawnCause.getEntity();
    if (!Creature.class.isAssignableFrom(entity.getType().getEntityClass())) {
      return;
    }

    if (isApplicable(entity.getLocation())) {
      event.setCancelled(true);
    }
  }
}
