/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.generic;

import com.skelril.nitro.registry.item.CustomItem;
import net.minecraft.creativetab.CreativeTabs;

public class RedShard extends CustomItem {

    @Override
    public String __getID() {
        return "red_shard";
    }

    @Override
    public int __getMaxStackSize() {
        return 64;
    }

    @Override
    public CreativeTabs __getCreativeTab() {
        return CreativeTabs.MATERIALS;
    }
}
