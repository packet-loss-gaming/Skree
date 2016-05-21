/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.zone;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.skelril.nitro.position.CuboidContainmentPredicate;

import java.util.function.Consumer;

public class ZoneBoundingBox {
    private final Vector3i origin;
    private final Vector3i boundingBox;
    private transient CuboidContainmentPredicate predicate;

    public ZoneBoundingBox(ZoneBoundingBox boundingBox) {
        this(boundingBox.getOrigin(), boundingBox.getBoundingBox());
    }

    public ZoneBoundingBox(Vector3i origin, Vector3i boundingBox) {
        this.origin = origin;
        this.boundingBox = boundingBox;
        this.predicate = new CuboidContainmentPredicate(getMinimumPoint().toDouble(), getMaximumPoint().toDouble());
    }

    public boolean contains(Vector3d point) {
        return predicate.test(point);
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

    public Vector3d getCenter() {
        return getOrigin().toDouble().add(boundingBox.toDouble().div(2));
    }

    public void forAll(Consumer<Vector3i> predicate) {
        for (int y = 0; y < boundingBox.getY(); ++y) {
            for (int x = 0; x < boundingBox.getX(); ++x) {
                for (int z = 0; z < boundingBox.getZ(); ++z) {
                    predicate.accept(new Vector3i(x + origin.getX(), y + origin.getY(), z + origin.getZ()));
                }
            }
        }
    }
}
