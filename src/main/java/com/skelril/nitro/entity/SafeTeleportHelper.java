/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.entity;

import com.google.common.collect.Sets;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.property.block.PassableProperty;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.Set;

public class SafeTeleportHelper {
    private final static Set<BlockType> BLACK_LISTED_BLOCK = Sets.newHashSet(
            BlockTypes.LAVA, BlockTypes.FLOWING_LAVA
    );

    private static boolean isSafePassableBlock(BlockType blockType) {
        boolean isPassable = blockType.getProperty(PassableProperty.class).orElse(new PassableProperty(false)).getValue();

        return isPassable && !isBlacklistedBlock(blockType);
    }

    private static boolean isBlacklistedBlock(BlockType blockType) {
        return BLACK_LISTED_BLOCK.contains(blockType);
    }

    public static Optional<Location<World>> getSafeDest(Location<World> dest) {
        // Move down through the air, if we hit a non-air block stop
        int blocksMoved;
        for (blocksMoved = 0; dest.getY() > 0 && dest.getBlockType() == BlockTypes.AIR; ++blocksMoved) {
            dest = dest.add(0, -1, 0);
        }

        // Move one back up to account for air if the player was moved
        if (blocksMoved > 0) {
            dest = dest.add(0, 1, 0);
        }

        // Check the blocks where the player model would be located
        // If both blocks are not safe passable block types, we failed
        if (!isSafePassableBlock(dest.getBlockType()) || !isSafePassableBlock(dest.add(0, 1, 0).getBlockType())) {
            return Optional.empty();
        }

        // Check the block immediately below the player, if it's blacklisted, we failed
        if (isBlacklistedBlock(dest.add(0, -1, 0).getBlockType())) {
            return Optional.empty();
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
