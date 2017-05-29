/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.wilderness;

import com.skelril.nitro.registry.dynamic.QuantityBoundedItemStackConfig;
import org.spongepowered.api.block.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WildernessDropConfig {
    private List<String> multipliedBlockTypes = new ArrayList<>();
    private Map<String, QuantityBoundedItemStackConfig> itemReplacementMapping = new HashMap<>();

    public boolean amplifies(BlockState state) {
        return multipliedBlockTypes.contains(state.getId());
    }

    public Map<String, QuantityBoundedItemStackConfig> getItemReplacementMapping() {
        return itemReplacementMapping;
    }
}
