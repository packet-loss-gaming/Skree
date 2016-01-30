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

import java.nio.file.Path;
import java.util.function.Consumer;

public class ChainPlacementAllocator extends WESchematicAllocator {

    private final WorldResolver worldResolver;

    private Vector2i lastEnd = new Vector2i(0, 0);

    public ChainPlacementAllocator(Path baseDir, WorldResolver worldResolver) {
        super(baseDir);
        this.worldResolver = worldResolver;
    }

    @Override
    public float getLoad() {
        return 0;
    }

    @Override
    public void regionFor(String managerName, Consumer<Clause<ZoneRegion, ZoneRegion.State>> callBack) {
        pasteAt(worldResolver, new Vector3i(lastEnd.getX(), 0, lastEnd.getY()), managerName, region -> {
            // Update last end
            Vector3i lastMax = region.getMaximumPoint();
            lastEnd = new Vector2i(lastMax.getX() + 1, lastMax.getZ() + 1);

            callBack.accept(new Clause<>(region, ZoneRegion.State.NEW));
        });
    }
}
