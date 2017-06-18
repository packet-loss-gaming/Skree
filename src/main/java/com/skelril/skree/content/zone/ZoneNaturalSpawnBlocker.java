/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone;

import com.google.common.collect.Lists;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.ExperienceOrb;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.explosive.PrimedTNT;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.spawn.SpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnType;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ZoneNaturalSpawnBlocker<T> extends ZoneApplicableListener<T> {
  public ZoneNaturalSpawnBlocker(Function<Location<World>, Optional<T>> applicabilityFunct) {
    super(applicabilityFunct);
  }

  private static List<Class<? extends Entity>> LEGAL_ENTITY_CLASSES = Lists.newArrayList(
      ExperienceOrb.class, PrimedTNT.class, Projectile.class, Item.class
  );

  private static boolean isLegalSpawn(Entity entity) {
    boolean legalSpawn = false;

    for (Class<? extends Entity> clazz : LEGAL_ENTITY_CLASSES) {
      if (clazz.isInstance(entity)) {
        legalSpawn = true;
      }
    }

    return legalSpawn;
  }

  @Listener
  public void onEntitySpawn(SpawnEntityEvent event, @First SpawnCause spawnCause) {
    for (Entity entity : event.getEntities()) {
      if (isApplicable(entity)) {
        SpawnType spawnType = spawnCause.getType();

        if (spawnType != SpawnTypes.PLUGIN && !isLegalSpawn(entity)) {
          event.setCancelled(true);
        }

        break;
      }
    }
  }
}
