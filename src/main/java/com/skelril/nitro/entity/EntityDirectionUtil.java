/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.entity;

import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.entity.living.Living;

public class EntityDirectionUtil {
  public static Vector3d getFacingVector(Living living) {
    Vector3d rot = living.getHeadRotation();

    double xRot = (rot.getY() + 90) % 360;
    double yRot = rot.getX() * -1;

    double h = Math.cos(Math.toRadians(yRot));

    return new Vector3d(
        h * Math.cos(Math.toRadians(xRot)),
        Math.sin(Math.toRadians(yRot)),
        h * Math.sin(Math.toRadians(xRot))
    );
  }
}
