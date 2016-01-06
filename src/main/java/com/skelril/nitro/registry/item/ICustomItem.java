/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.item;

import com.google.common.collect.Lists;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface ICustomItem {
    String __getID();

    default List<String> __getMeshDefinitions() {
        return Lists.newArrayList(__getID());
    }

    default void getSubItems(Item itemIn, CreativeTabs tab, List subItems) {
        List<String> variants = __getMeshDefinitions();
        for (int i = 0; i < variants.size(); ++i) {
            subItems.add(new ItemStack(itemIn, 1, i));
        }
    }

    int __getMaxStackSize();
    CreativeTabs __getCreativeTab();
}
