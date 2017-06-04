/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world;

import com.flowpowered.math.vector.Vector3i;
import com.skelril.skree.SkreePlugin;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.ImmutableBiomeVolume;
import org.spongepowered.api.world.extent.MutableBlockVolume;
import org.spongepowered.api.world.gen.GenerationPopulator;

public class BarrierBlockTerrainGenerator implements GenerationPopulator {
  @Override
  public void populate(World world, MutableBlockVolume buffer, ImmutableBiomeVolume biomes) {
    Vector3i min = buffer.getBlockMin();
    Vector3i max = buffer.getBlockMax();
    for (int y = min.getY(); y <= max.getY(); ++y) {
      for (int x = min.getX(); x <= max.getX(); ++x) {
        for (int z = min.getZ(); z <= max.getZ(); ++z) {
          buffer.setBlockType(x, y, z, BlockTypes.BARRIER, Cause.source(SkreePlugin.container()).build());
        }
      }
    }
  }
}
