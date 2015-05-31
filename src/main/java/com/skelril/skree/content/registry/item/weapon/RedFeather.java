/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.weapon;

import com.skelril.nitro.registry.item.CustomItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class RedFeather extends Item implements CustomItem {
    public RedFeather() {
        maxStackSize = 1;
        setCreativeTab(CreativeTabs.tabCombat);
    }

    @Override
    public String getID() {
        return "redFeather";
    }
}
