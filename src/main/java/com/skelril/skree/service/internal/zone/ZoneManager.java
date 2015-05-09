/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.zone;

import java.util.Collection;

public interface ZoneManager<T extends Zone> {

    T discover(ZoneSpaceAllocator allocator);
    Collection<T> getActiveZones();

    String getName();
}
