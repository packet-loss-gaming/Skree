/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.admin;

import com.skelril.nitro.registry.item.CustomItem;
import net.minecraft.creativetab.CreativeTabs;

public class NavigationWand extends CustomItem {

    @Override
    public String __getID() {
        return "navigation_wand";
    }

    @Override
    public int __getMaxStackSize() {
        return 1;
    }

    @Override
    public CreativeTabs __getCreativeTab() {
        return CreativeTabs.tabTools;
    }
}

