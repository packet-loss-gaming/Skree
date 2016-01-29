/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.minigame;

import com.skelril.nitro.registry.item.CustomItem;
import net.minecraft.creativetab.CreativeTabs;

import java.util.ArrayList;
import java.util.List;

public class GoldRushKey extends CustomItem {

    @Override
    public String __getID() {
        return "gold_rush_key";
    }

    @Override
    public List<String> __getMeshDefinitions() {
        List<String> baseList = new ArrayList<>();
        baseList.add("gold_rush_red_key");
        baseList.add("gold_rush_blue_key");
        return baseList;
    }

    @Override
    public int __getMaxStackSize() {
        return 1;
    }

    @Override
    public CreativeTabs __getCreativeTab() {
        return null;
    }
}
