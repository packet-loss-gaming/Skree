/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.item;

import com.google.common.collect.Lists;

import java.util.List;

public abstract class ItemConfig {
    private String id;

    private int enchantability;

    public String getID() {
        return id;
    }

    public List<String> getMeshDefinitions() {
        return Lists.newArrayList(getID());
    }

    public int getEnchantability() {
        return enchantability;
    }
}
