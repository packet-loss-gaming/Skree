/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.item;

import com.google.common.collect.Lists;
import net.minecraft.creativetab.CreativeTabs;

import java.util.List;

public interface ICustomItem {
    String __getID();

    default List<String> __getMeshDefinitions() {
        return Lists.newArrayList(__getID());
    }
    int __getMaxStackSize();
    CreativeTabs __getCreativeTab();
}
