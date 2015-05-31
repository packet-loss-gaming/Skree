/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.consumable;

import com.skelril.nitro.registry.item.CustomItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemFood;

public class RawGodFish extends ItemFood implements CustomItem {
    public RawGodFish() {
        super(5, .5F, false);
        maxStackSize = 16;
        setCreativeTab(CreativeTabs.tabFood);
    }

    @Override
    public String getID() {
        return "godFish";
    }
}