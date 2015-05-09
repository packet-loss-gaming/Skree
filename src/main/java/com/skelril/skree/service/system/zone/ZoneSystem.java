/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.system.zone;

import com.skelril.skree.service.internal.zone.ZoneServiceImpl;
import com.skelril.skree.service.internal.zone.allocator.RandomPlacementAllocator;

public class ZoneSystem {

    private ZoneServiceImpl zoner = new ZoneServiceImpl(new RandomPlacementAllocator());

    public ZoneSystem() {

    }
}
