/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.dropclear;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CheckProfile {
    private final Collection<? extends Entity> entities;
    private final Collection<? extends DropClearStats> stats;

    private CheckProfile(Collection<? extends Entity> entities, Collection<? extends DropClearStats> stats) {
        this.entities = entities;
        this.stats = stats;
    }

    public static CheckProfile createFor(Extent extent, Collection<EntityType> checkedEntities) {
        Set<Entity> entities = new HashSet<>();
        Set<ChunkStats> stats = new HashSet<>();

        ExtentStats es = new ExtentStats();
        for (Entity e : extent.getEntities()) {
            checkedEntities.stream().filter(eType -> eType == e.getType()).forEach(
                    eType -> {
                        es.increase(eType, 1);
                        entities.add(e);
                    }
            );
        }

        return new CheckProfile(entities, Collections.singletonList(es));
    }

    public static CheckProfile createFor(World world, Collection<EntityType> checkedEntities) {
        Set<Entity> entities = new HashSet<>();
        Set<ChunkStats> stats = new HashSet<>();
        for (Chunk chunk : world.getLoadedChunks()) {
            ChunkStats cs = new ChunkStats(chunk);
            for (Entity e : chunk.getEntities()) {
                checkedEntities.stream().filter(eType -> eType == e.getType()).forEach(
                        eType -> {
                            cs.increase(eType, 1);
                            entities.add(e);
                        }
                );
            }
            if (cs.total() < 1) continue;
            stats.add(cs);
        }
        return new CheckProfile(entities, stats);
    }

    public Collection<? extends Entity> getEntities() {
        return entities;
    }

    public Collection<? extends DropClearStats> getStats() {
        return stats;
    }
}