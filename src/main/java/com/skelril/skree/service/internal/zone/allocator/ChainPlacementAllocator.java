/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.zone.allocator;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;
import com.skelril.nitro.Clause;
import com.skelril.skree.service.internal.zone.WorldResolver;
import com.skelril.skree.service.internal.zone.ZoneRegion;
import com.skelril.skree.service.internal.zone.ZoneSpaceAllocator;
import com.skelril.skree.service.internal.zone.decorator.Decorator;

import java.util.function.Consumer;
import java.util.function.Function;

public class ChainPlacementAllocator implements ZoneSpaceAllocator {

    private final WorldResolver worldResolver;
    private Decorator decorator;

    private Vector2i lastEnd = new Vector2i(0, 0);

    public ChainPlacementAllocator(Decorator decorator, WorldResolver worldResolver) {
        this.decorator = decorator;
        this.worldResolver = worldResolver;
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
        ZoneRegion incompleteRegion = decorator.pasteAt(
                worldResolver,
                new Vector3i(lastEnd.getX(), 0, lastEnd.getY()),
                managerName,
                initMapper,
                callBack::accept
        );

        Vector3i lastMax = incompleteRegion.getMaximumPoint();
        lastEnd = new Vector2i(lastMax.getX() + 1, lastMax.getZ() + 1);
    }
}
