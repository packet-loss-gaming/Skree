/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.global.anexample;

import com.skelril.nitro.Clause;
import com.skelril.skree.service.internal.zone.ZoneRegion;
import com.skelril.skree.service.internal.zone.ZoneSpaceAllocator;
import com.skelril.skree.service.internal.zone.global.GlobalZoneManager;

public class AnExampleManager extends GlobalZoneManager<AnExampleInstance> {

    private String name;

    public AnExampleManager(String name) {
        this.name = name;
    }

    @Override
    public AnExampleInstance init(ZoneSpaceAllocator allocator) {
        Clause<ZoneRegion, ZoneRegion.State> result = allocator.regionFor(getName());
        ZoneRegion region = result.getKey();

        AnExampleInstance instance = new AnExampleInstance(region);
        instance.init();

        return instance;
    }

    @Override
    public String getName() {
        return name;
    }
}
