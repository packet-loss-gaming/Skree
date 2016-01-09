/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.weapon.bow;

import com.skelril.nitro.registry.item.bow.CustomBow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.Random;

public class NetherBow extends CustomBow {
    @Override
    public int __getMaxUses() {
        return 1536;
    }

    @Override
    public String __getType() {
        return "nether";
    }

    @Override
    public ItemStack __getRepairItemStack() {
        return new ItemStack(Items.nether_star);
    }

    @Override
    public int __getEnchantability() {
        return 1;
    }

    @Override
    public Random __getItemRand() {
        return itemRand;
    }
}
