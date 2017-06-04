/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.entitystats;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.World;

import java.util.*;
import java.util.function.Predicate;

public class WorldStatisticsEntityCollection implements StatisticEntityCollection {

  private Map<Vector3i, ChunkStatisticEntityCollection> chunkMapping = new HashMap<>();

  public WorldStatisticsEntityCollection(List<ChunkStatisticEntityCollection> entityCollections) {
    for (ChunkStatisticEntityCollection entityCollection : entityCollections) {
      chunkMapping.put(entityCollection.getPosition(), entityCollection);
    }
  }

  public static WorldStatisticsEntityCollection createFor(World world, Predicate<Entity> predicate) {
    List<ChunkStatisticEntityCollection> entityCollections = new ArrayList<>();
    for (Chunk chunk : world.getLoadedChunks()) {
      entityCollections.add(ChunkStatisticEntityCollection.createFor(chunk, predicate));
    }
    return new WorldStatisticsEntityCollection(entityCollections);
  }

  @Override
  public Collection<Entity> getEntities() {
    List<Entity> entities = new ArrayList<>();
    for (ChunkStatisticEntityCollection entityCollection : chunkMapping.values()) {
      entities.addAll(entityCollection.getEntities());
    }
    return entities;
  }
}
