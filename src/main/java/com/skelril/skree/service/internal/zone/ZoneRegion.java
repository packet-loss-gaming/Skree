/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.zone;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.world.World;

public class ZoneRegion {

    private final World world;
    private final Vector3i origin;
    private final Vector3i boundingBox;

    public ZoneRegion(World world, Vector3i origin, Vector3i boundingBox) {
        this.world = world;
        this.origin = origin;
        this.boundingBox = boundingBox;
    }

    public World getExtent() {
        return world;
    }

    public Vector3i getOrigin() {
        return origin;
    }

    public Vector3i getMinimumPoint() {
        return getOrigin();
    }

    public Vector3i getMaximumPoint() {
        return getOrigin().add(boundingBox);
    }

    public Vector3i getBoundingBox() {
        return boundingBox;
    }

    public enum State {
        NEW,
        RELOADED
    }
}
