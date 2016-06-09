/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.consumable;

import com.skelril.nitro.registry.item.food.CustomFood;

public class RawGodFish extends CustomFood {

    @Override
    public String __getID() {
        return "god_fish";
    }

    @Override
    public int __getMaxStackSize() {
        return 16;
    }

    @Override
    public double __getHealAmount() {
        return 5;
    }

    @Override
    public double __getSaturationModifier() {
        return .5F;
    }
}