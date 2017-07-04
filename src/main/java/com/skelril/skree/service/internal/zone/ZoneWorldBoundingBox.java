/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.zone;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.world.Chunk;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;

public class ZoneWorldBoundingBox extends ZoneBoundingBox {

  private final World world;

  public ZoneWorldBoundingBox(World world, Vector3i origin, Vector3i boundingBox) {
    super(origin, boundingBox);
    this.world = world;
  }

  public World getExtent() {
    return world;
  }

  public List<Chunk> getChunks() {
    List<Chunk> chunks = new ArrayList<>();

    for (int x = 0; x < getBoundingBox().getX(); x += 16) {
      for (int z = 0; z < getBoundingBox().getZ(); z += 16) {
        getExtent().getChunkAtBlock(getOrigin().getX() + x, 0, getOrigin().getZ() + z).ifPresent(chunks::add);
      }
    }

    return chunks;
  }
}
