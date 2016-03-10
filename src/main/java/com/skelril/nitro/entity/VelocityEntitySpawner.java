/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.entity;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VelocityEntitySpawner {
    public static Optional<Entity> send(EntityType type, Location<World> loc, Vector3d dir, float speed, Cause cause) {
        Vector3d actualDir = dir.normalize();
        Vector3d finalVecLoc = loc.getPosition().add(actualDir.mul(2));
        loc.setPosition(finalVecLoc);
        Optional<Entity> optEnt = loc.getExtent().createEntity(type, loc.getPosition());
        if (optEnt.isPresent()) {
            Entity entity = optEnt.get();
            entity.setVelocity(dir.mul(speed));
            return loc.getExtent().spawnEntity(entity, cause) ? Optional.of(entity) : Optional.empty();
        }
        return Optional.empty();
    }

    public static List<Entity> sendRadial(EntityType type, Location<World> loc, Cause cause) {
        final int amt = 12;
        final double tau = 2 * Math.PI;

        double arc = tau / amt;
        List<Entity> resultSet = new ArrayList<>();
        for (double a = 0; a < tau; a += arc) {
            Optional<Entity> optEnt = send(type, loc, new Vector3d(Math.cos(a), 0, Math.sin(a)), .5F, cause);
            if (optEnt.isPresent()) {
                resultSet.add(optEnt.get());
            }
        }
        return resultSet;
    }
}
