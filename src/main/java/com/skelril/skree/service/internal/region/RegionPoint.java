/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.region;

import com.flowpowered.math.vector.Vector3d;

public class RegionPoint extends Vector3d implements Comparable<Vector3d> {
  public RegionPoint(Vector3d v) {
    super(v);
  }

  public RegionPoint(float x, float y, float z) {
    super(x, y, z);
  }

  public RegionPoint(double x, double y, double z) {
    super(x, y, z);
  }
}
