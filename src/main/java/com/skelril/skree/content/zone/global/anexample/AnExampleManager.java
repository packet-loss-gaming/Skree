/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.global.anexample;

import com.skelril.skree.service.internal.zone.ZoneRegion;
import com.skelril.skree.service.internal.zone.ZoneSpaceAllocator;
import com.skelril.skree.service.internal.zone.global.GlobalZoneManager;

import java.util.function.Consumer;

public class AnExampleManager extends GlobalZoneManager<AnExampleInstance> {

    private String name;

    public AnExampleManager(String name) {
        this.name = name;
    }

    @Override
    public void init(ZoneSpaceAllocator allocator, Consumer<AnExampleInstance> callback) {
        allocator.regionFor(getSystemName(), clause -> {
            ZoneRegion region = clause.getKey();

            AnExampleInstance instance = new AnExampleInstance(region);
            instance.init();

            callback.accept(instance);
        });
    }

    @Override
    public String getName() {
        return name;
    }
}
