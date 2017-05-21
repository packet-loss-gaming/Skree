/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.wilderness;

import java.util.HashMap;
import java.util.Map;

public class WildernessConfig {
    private Map<String, WildernessMultipliedResult> multipliedBlocks = new HashMap<>();

    public Map<String, WildernessMultipliedResult> getMultipliedBlocks() {
        return multipliedBlocks;
    }
}
