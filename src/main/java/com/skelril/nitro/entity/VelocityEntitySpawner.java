/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.entity;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.data.property.entity.EyeLocationProperty;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VelocityEntitySpawner {
  public static Optional<Entity> send(EntityType type, Location<World> loc, Vector3d dir, float speed) {
    Vector3d actualDir = dir.normalize();

    // Shift the entity out two movements to prevent collision with the source entity
    Vector3d finalVecLoc = loc.getPosition().add(actualDir.mul(2));
    loc = loc.setPosition(finalVecLoc);

    Entity entity = loc.getExtent().createEntity(type, loc.getPosition());
    entity.setVelocity(dir.mul(speed));
    return loc.getExtent().spawnEntity(entity) ? Optional.of(entity) : Optional.empty();
  }

  public static List<Entity> sendRadial(EntityType type, Location<World> loc) {
    return sendRadial(type, loc, 12, .5F);
  }

  public static List<Entity> sendRadial(EntityType type, Living living) {
    return sendRadial(type, living, 12, .5F);
  }

  public static List<Entity> sendRadial(EntityType type, Location<World> loc, int amt, float speed) {
    final double tau = 2 * Math.PI;

    double arc = tau / amt;
    List<Entity> resultSet = new ArrayList<>();
    for (double a = 0; a < tau; a += arc) {
      Vector3d directionVector = new Vector3d(Math.cos(a), 0, Math.sin(a));

      send(type, loc, directionVector, speed).ifPresent(resultSet::add);
    }
    return resultSet;
  }

  public static List<Entity> sendRadial(EntityType type, Living living, int amt, float speed) {
    Location<World> livingLocation = living.getLocation();
    Optional<EyeLocationProperty> optEyeLoc = living.getProperty(EyeLocationProperty.class);
    if (optEyeLoc.isPresent()) {
      Vector3d eyePosition = optEyeLoc.get().getValue();
      livingLocation = livingLocation.setPosition(eyePosition);
    }

    return sendRadial(type, livingLocation, amt, speed);
  }
}
