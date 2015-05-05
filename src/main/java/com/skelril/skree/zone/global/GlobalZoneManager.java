/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.zone.global;

import com.skelril.skree.zone.Zone;
import com.skelril.skree.zone.ZoneManager;
import com.skelril.skree.zone.ZoneSpaceAllocator;

import java.util.Collection;
import java.util.Collections;

public abstract class GlobalZoneManager<T extends Zone> implements ZoneManager<T> {
    protected T zone;

    public abstract T init(ZoneSpaceAllocator allocator);

    public boolean isActive() {
        return zone != null && zone.isActive();
    }

    @Override
    public T discover(ZoneSpaceAllocator allocator) {
        return !isActive() ? zone = init(allocator) : zone;
    }

    @Override
    public Collection<T> getActiveZones() {
        return isActive() ? Collections.singletonList(zone) : Collections.emptyList();
    }
}
