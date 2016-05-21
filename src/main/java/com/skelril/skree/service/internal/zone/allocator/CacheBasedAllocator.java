/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.zone.allocator;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;
import com.skelril.nitro.Clause;
import com.skelril.skree.service.internal.zone.*;
import com.skelril.skree.service.internal.zone.decorator.Decorator;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class CacheBasedAllocator implements ZoneSpaceAllocator {

    private final WorldResolver worldResolver;
    private Decorator decorator;
    private ZonePool pool;

    private Vector2i lastEnd;

    public CacheBasedAllocator(Decorator decorator, WorldResolver worldResolver) {
        this.decorator = decorator;
        this.worldResolver = worldResolver;

        try {
            pool = new ZonePool();
            pool.load();
        } catch (IOException|IllegalStateException e) {
            pool = new ZonePool();
        }

        lastEnd = pool.getLastMarkedPoint();
    }

    @Override
    public WorldResolver getWorldResolver() {
        return worldResolver;
    }

    @Override
    public float getLoad() {
        return 0;
    }

    @Override
    public <T> void regionFor(String managerName, Function<Clause<ZoneRegion, ZoneRegion.State>, T> initMapper, Consumer<T> callBack) {
        Optional<ZoneBoundingBox> optBoundingBox = pool.getIfAvailable(managerName);

        Vector3i origin = new Vector3i(lastEnd.getX(), 0, lastEnd.getY());
        if (optBoundingBox.isPresent()) {
            origin = optBoundingBox.get().getOrigin();
        }

        ZoneWorldBoundingBox incompleteRegion = decorator.pasteAt(
                worldResolver,
                origin,
                managerName,
                box -> initMapper.apply(new Clause<>(new ZoneRegion(this, box), ZoneRegion.State.NEW_LOADING)),
                callBack::accept
        );

        if (!optBoundingBox.isPresent()) {
            pool.claimNew(managerName, incompleteRegion);

            Vector3i lastMax = incompleteRegion.getMaximumPoint();
            lastEnd = new Vector2i(lastMax.getX() + 1, lastMax.getZ() + 1);
        }
    }

    @Override
    public void release(String managerName, ZoneWorldBoundingBox region) {
        pool.freeToPool(managerName, region);
    }
}
