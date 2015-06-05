/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service;

import org.spongepowered.api.world.extent.Extent;

public interface DropClearService {
    default boolean cleanup(Extent extent) {
        return cleanup(extent, 10);
    }
    boolean cleanup(Extent extent, int seconds);

    boolean checkedCleanup(Extent extent);

    boolean isActiveFor(Extent extent);

    void forceCleanup(Extent extent);
}
