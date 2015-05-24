/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.admin;

import com.skelril.nitro.registry.item.CustomItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class HackBook extends Item implements CustomItem {

    public HackBook() {
        maxStackSize = 1;
        setCreativeTab(CreativeTabs.tabMisc);
    }

    @Override
    public String getID() {
        return "hackBook";
    }
}
