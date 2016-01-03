/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.entitystats;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.world.Chunk;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ChunkStatisticEntityCollection implements StatisticEntityCollection {
    private Vector3i chunkPosition;
    private Collection<Entity> entities;

    public ChunkStatisticEntityCollection(Vector3i chunkPosition, Collection<Entity> entities) {
        this.chunkPosition = chunkPosition;
        this.entities = entities;
    }

    public static ChunkStatisticEntityCollection createFor(Chunk chunk, Predicate<Entity> predicate) {
        return new ChunkStatisticEntityCollection(
                chunk.getPosition(),
                chunk.getEntities().stream().filter(predicate).collect(Collectors.toList())
        );
    }

    public Vector3i getPosition() {
        return chunkPosition;
    }

    @Override
    public Collection<Entity> getEntities() {
        return entities;
    }
}
