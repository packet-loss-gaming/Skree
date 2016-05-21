/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.zone;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.world.World;

import java.lang.ref.WeakReference;
import java.util.Optional;

public class ZoneRegion extends ZoneWorldBoundingBox {
    private final WeakReference<ZoneSpaceAllocator> weakAllocator;

    public ZoneRegion(ZoneSpaceAllocator allocator, ZoneWorldBoundingBox boundingBox) {
        this(allocator, boundingBox.getExtent(), boundingBox.getOrigin(), boundingBox.getBoundingBox());
    }

    public ZoneRegion(ZoneSpaceAllocator allocator, World world, Vector3i origin, Vector3i boundingBox) {
        super(world, origin, boundingBox);
        this.weakAllocator = new WeakReference<>(allocator);
    }

    public Optional<ZoneSpaceAllocator> getAllocator() {
        return Optional.ofNullable(weakAllocator.get());
    }

    public enum State {
        NEW,
        NEW_LOADING,
        RELOADED
    }
}
