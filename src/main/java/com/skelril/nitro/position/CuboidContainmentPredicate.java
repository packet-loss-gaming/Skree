/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.position;

import com.flowpowered.math.vector.Vector3d;

import java.util.function.Predicate;

public class CuboidContainmentPredicate implements Predicate<Vector3d> {

  private final Vector3d min;
  private final Vector3d max;

  public CuboidContainmentPredicate(Vector3d origin, double x, double y, double z) {
    this(
        new Vector3d(origin.getX() - x, origin.getY() - y, origin.getZ() - z),
        new Vector3d(origin.getX() + x + 1, origin.getY() + y + 1, origin.getZ() + z + 1)
    );
  }

  public CuboidContainmentPredicate(Vector3d min, Vector3d max) {
    this.min = min;
    this.max = max;
  }

  @Override
  public boolean test(Vector3d point) {
    return min.getX() <= point.getX() && point.getX() < max.getX()
        && min.getY() <= point.getY() && point.getY() < max.getY()
        && min.getZ() <= point.getZ() && point.getZ() < max.getZ();
  }
}
