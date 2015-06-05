/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.dropclear;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.extent.Extent;

import java.util.*;

public class CheckProfile {
    private final Collection<? extends Entity> entities;
    private final Map<Vector3i, ? extends DropClearStats> stats;

    private CheckProfile(Collection<? extends Entity> entities, Map<Vector3i, ? extends DropClearStats> stats) {
        this.entities = entities;
        this.stats = stats;
    }

    public static CheckProfile createFor(Extent extent, Collection<EntityType> checkedEntities) {
        Set<Entity> entities = new HashSet<>();
        Map<Vector3i, ChunkStats> stats = new HashMap<>();
        for (Entity entity : extent.getEntities()) {
            EntityType eType = entity.getType();
            if (checkedEntities.contains(eType)) {
                Vector3i chunkPos = getChunkPos(entity.getLocation());
                ChunkStats chunkStats = stats.merge(
                        chunkPos, new ChunkStats(chunkPos), ChunkStats::merge
                );
                chunkStats.increase(eType, 1);
                entities.add(entity);
            }
        }
        return new CheckProfile(entities, stats);
    }

    private static Vector3i getChunkPos(Location loc) {
        return new Vector3i(loc.getX() / 16, 0, loc.getZ() / 16);
    }

    public Collection<? extends Entity> getEntities() {
        return entities;
    }

    public Map<Vector3i, ? extends DropClearStats> getStats() {
        return stats;
    }
}