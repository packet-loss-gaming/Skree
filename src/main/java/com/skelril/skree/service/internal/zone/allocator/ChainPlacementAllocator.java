/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.zone.allocator;

import com.flowpowered.math.vector.Vector2i;
import com.sk89q.worldedit.world.World;
import com.skelril.nitro.Clause;
import com.skelril.skree.service.internal.zone.ZoneRegion;

import java.io.File;

public class ChainPlacementAllocator extends WESchematicAllocator {

    private final World world;

    private Vector2i lastEnd = new Vector2i(0, 0);

    public ChainPlacementAllocator(File baseDir, World world) {
        super(baseDir);
        this.world = world;
    }

    @Override
    public float getLoad() {
        return 0;
    }

    @Override
    public Clause<ZoneRegion, ZoneRegion.State> regionFor(String managerName) {
        ZoneRegion region = pasteAt(world, lastEnd.toVector3(), managerName);

        // Update last end
        lastEnd = region.getMaximumPoint().toVector2();

        return new Clause<>(region, ZoneRegion.State.NEW);
    }
}
