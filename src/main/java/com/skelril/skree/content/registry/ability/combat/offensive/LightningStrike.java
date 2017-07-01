/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.ability.combat.offensive;

import com.skelril.nitro.registry.dynamic.ability.PointOfContact;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.weather.Lightning;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class LightningStrike implements PointOfContact {
  @Override
  public void run(Living owner, Location<World> target) {
    Lightning lightning = (Lightning) target.createEntity(EntityTypes.LIGHTNING);
    target.spawnEntity(lightning, Cause.source(SpawnCause.builder().type(SpawnTypes.PLUGIN).build()).build());
  }
}
