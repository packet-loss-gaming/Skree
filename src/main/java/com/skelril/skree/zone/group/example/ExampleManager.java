/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.zone.group.example;

import com.google.inject.Singleton;
import com.skelril.skree.zone.ZoneSpaceAllocator;
import com.skelril.skree.zone.group.GroupZoneManager;

@Singleton
public class ExampleManager extends GroupZoneManager<ExampleInstance> {
    @Override
    public ExampleInstance discover(ZoneSpaceAllocator allocator) {
        return new ExampleInstance();
    }

    @Override
    public String getName() {
        return "Example";
    }
}
