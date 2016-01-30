/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.zone;

import com.skelril.skree.service.ZoneService;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;

public interface ZoneManager<T extends Zone> {
    void discover(ZoneSpaceAllocator allocator, Consumer<Optional<Zone>> callback);
    Collection<T> getActiveZones();

    default Optional<Integer> getMaxGroupSize() {
        return Optional.empty();
    }

    String getName();
    default String getSystemName() {
        return ZoneService.mangleManagerName(getName());
    }
}
