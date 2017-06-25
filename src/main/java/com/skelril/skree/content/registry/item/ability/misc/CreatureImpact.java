/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.ability.misc;

import com.skelril.nitro.particle.ParticleGenerator;
import com.skelril.nitro.registry.dynamic.ability.PointOfContact;
import com.skelril.skree.SkreePlugin;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CreatureImpact implements PointOfContact {
  private int count = 125;
  private String creature;

  private EntityType getEntityType() {
    return Sponge.getRegistry().getType(EntityType.class, creature).get();
  }

  private static Cause getSpawnCause() {
    return Cause.source(SpawnCause.builder().type(SpawnTypes.PLUGIN).build()).owner(SkreePlugin.container()).build();
  }

  public static void mobBarrage(Location target, EntityType type, int count) {
    final List<Entity> entities = new ArrayList<>();

    for (int i = 0; i < count; i++) {
      Entity entity = target.getExtent().createEntity(type, target.getPosition());
      entity.offer(Keys.PERSISTS, false);
      entities.add(entity);
    }

    target.getExtent().spawnEntities(entities, getSpawnCause());

    Task.builder().delay(30, TimeUnit.SECONDS).execute(() -> {
      for (Entity entity : entities) {
        if (!entity.isRemoved()) {
          entity.remove();
          ParticleGenerator.smoke(entity.getLocation(), 1);
        }
      }
    }).submit(SkreePlugin.inst());
  }

  @Override
  public void run(Living owner, Location<World> target) {
    EntityType targetType = getEntityType();

    mobBarrage(target, targetType, count);

    if (targetType == EntityTypes.BAT) {
      notify(owner, Text.of(TextColors.YELLOW, "Your bow releases a batty attack."));
    } else {
      notify(owner, Text.of(TextColors.YELLOW, "Your bow releases a " + targetType.getName().toLowerCase() + " attack."));
    }
  }
}
