/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.extractor;

import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

public class WorldFromExtent implements Extractor<World, Extent> {
    @Override
    public World from(Extent extent) {
        if (extent instanceof World) {
            return (World) extent;
        }
        return null;
    }
}
