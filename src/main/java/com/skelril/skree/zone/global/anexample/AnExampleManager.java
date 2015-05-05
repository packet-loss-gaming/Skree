/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.zone.global.anexample;

import com.skelril.skree.zone.ZoneSpaceAllocator;
import com.skelril.skree.zone.global.GlobalZoneManager;

public class AnExampleManager extends GlobalZoneManager<AnExampleInstance> {
    @Override
    public AnExampleInstance init(ZoneSpaceAllocator allocator) {
        return new AnExampleInstance();
    }

    @Override
    public String getName() {
        return "An Example";
    }
}
