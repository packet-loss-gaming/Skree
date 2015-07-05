/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.admin;

import com.skelril.nitro.registry.item.sword.CustomSword;
import net.minecraft.item.ItemStack;

public class SwordOGreatPain extends CustomSword {
    @Override
    public int __getMaxUses() {
        return 14;
    }

    @Override
    public ItemStack __getRepairItemStack() {
        return null;
    }

    @Override
    public double __getHitPower() {
        return 100000000;
    }

    @Override
    public int __getEnchantability() {
        return ToolMaterial.WOOD.getEnchantability();
    }

    @Override
    public String __getType() {
        return "oGreatPain";
    }
}