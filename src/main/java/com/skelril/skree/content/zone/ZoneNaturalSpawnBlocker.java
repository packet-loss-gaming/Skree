/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.spawn.SpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnType;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.function.Function;

public class ZoneNaturalSpawnBlocker<T> extends ZoneApplicableListener<T> {
  public ZoneNaturalSpawnBlocker(Function<Location<World>, Optional<T>> applicabilityFunct) {
    super(applicabilityFunct);
  }

  @Listener
  public void onEntitySpawn(SpawnEntityEvent event, @First SpawnCause spawnCause) {
    for (Entity entity : event.getEntities()) {
      if (isApplicable(entity)) {
        SpawnType spawnType = spawnCause.getType();
        if (spawnType == SpawnTypes.CUSTOM || spawnType == SpawnTypes.WORLD_SPAWNER) {
          /* SpongeCommon/679 */
          if (entity.getType() != EntityTypes.EXPERIENCE_ORB && entity.getType() != EntityTypes.PRIMED_TNT && !(entity instanceof Projectile)) {
            event.setCancelled(true);
          }
        }

        /* SpongeCommon/584 */
        if (spawnType == SpawnTypes.DROPPED_ITEM && entity.getType() == EntityTypes.ZOMBIE) {
          event.setCancelled(true);
        }
        break;
      }
    }
  }
}
