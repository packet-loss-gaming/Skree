/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.entity;

import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class SafeTeleportHelper {
    public static Optional<Location<World>> getSafeDest(Location<World> dest) {
        while (dest.getY() > 0 && dest.getBlockType() == BlockTypes.AIR) {
            dest = dest.add(0, -1, 0);
        }
        dest.add(0, 1, 0); // Move one back up to account for air

        // If its not air, restart at the starting destination, we failed
        if (dest.getBlockType() != BlockTypes.AIR) {
            if (dest.add(0, 1, 0).getBlockType() != BlockTypes.AIR) {
                return Optional.empty();
            }
        }

        return Optional.of(dest);
    }

    public static Optional<Location<World>> getSafeDest(Entity entity, Location<World> dest) {
        Optional<Boolean> optIsFlying = entity.get(Keys.IS_FLYING);
        if (!optIsFlying.isPresent() || !optIsFlying.get()) {
            dest = getSafeDest(dest).orElse(null);
        }

        return Optional.ofNullable(dest);
    }

    public static Optional<Location<World>> teleport(Entity entity, Location<World> dest) {
        Optional<Location<World>> optDest = getSafeDest(entity, dest);

        if (optDest.isPresent()) {
            entity.setLocation(optDest.get());
        }

        return optDest;
    }
}
