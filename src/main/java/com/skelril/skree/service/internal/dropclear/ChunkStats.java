/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.dropclear;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.world.Chunk;

public class ChunkStats extends DropClearStats {
    private final Vector3i pos;

    public ChunkStats(Chunk chunk) {
        this.pos = chunk.getPosition();
    }

    public Vector3i getPosition() {
        return pos;
    }
}