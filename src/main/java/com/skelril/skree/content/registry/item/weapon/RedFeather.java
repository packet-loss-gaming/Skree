/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.weapon;

import com.skelril.nitro.registry.item.CustomItem;
import com.skelril.nitro.registry.item.ICustomItem;
import net.minecraft.creativetab.CreativeTabs;

public class RedFeather extends CustomItem implements ICustomItem {

    @Override
    public String __getID() {
        return "red_feather";
    }

    @Override
    public int __getMaxStackSize() {
        return 1;
    }

    @Override
    public CreativeTabs __getCreativeTab() {
        return CreativeTabs.tabCombat;
    }
}
