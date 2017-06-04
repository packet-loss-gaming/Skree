/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.zone;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.world.World;

public class ZoneWorldBoundingBox extends ZoneBoundingBox {

  private final World world;

  public ZoneWorldBoundingBox(World world, Vector3i origin, Vector3i boundingBox) {
    super(origin, boundingBox);
    this.world = world;
  }

  public World getExtent() {
    return world;
  }
}
