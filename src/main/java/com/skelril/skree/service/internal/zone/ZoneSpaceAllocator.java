/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.zone;

import com.skelril.nitro.Clause;
import com.skelril.skree.service.internal.zone.ZoneRegion.State;

import java.util.function.Consumer;

public interface ZoneSpaceAllocator {
    float getLoad();

    void regionFor(String managerName, Consumer<Clause<ZoneRegion, State>> callBack);
}
