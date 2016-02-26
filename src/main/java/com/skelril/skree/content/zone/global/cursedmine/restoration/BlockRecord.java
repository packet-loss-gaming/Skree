/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.global.cursedmine.restoration;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class BlockRecord implements Comparable<BlockRecord> {
    // Block Information
    private final BlockSnapshot snapshot;

    // Time Information
    private final long time;

    public BlockRecord(BlockSnapshot snapshot) {
        this.snapshot = snapshot;
        this.time = System.currentTimeMillis();
    }

    public long getTime() {
        return time;
    }

    public Location<World> getLocation() {
        return snapshot.getLocation().get();
    }

    public void revert() {
        snapshot.restore(true, true);
    }

    // Oldest to newest
    @Override
    public int compareTo(BlockRecord record) {

        if (record == null) return -1;

        if (this.getTime() == record.getTime()) return 0;
        if (this.getTime() > record.getTime()) return 1;
        return -1;
    }
}
