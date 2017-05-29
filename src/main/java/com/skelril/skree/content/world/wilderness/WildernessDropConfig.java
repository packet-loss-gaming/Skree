/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.wilderness;

import com.skelril.nitro.registry.dynamic.QuantityBoundedItemStackConfig;

import java.util.*;

public class WildernessDropConfig {
    private List<String> multipliedBlockTypes = new ArrayList<>();
    private Map<String, QuantityBoundedItemStackConfig> itemReplacementMapping = new HashMap<>();

    public Collection<String> getMultipliedBlockTypes() {
        return multipliedBlockTypes;
    }

    public Map<String, QuantityBoundedItemStackConfig> getItemReplacementMapping() {
        return itemReplacementMapping;
    }
}
