/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.item.food;

import com.skelril.nitro.registry.dynamic.item.ItemConfig;

public class FoodConfig extends ItemConfig {
    private int maxStackSize;
    private int hungerRestored;
    private float saturationModifier;
    private float probabilityOfFoodPoisoning;

    public int getMaxStackSize() {
        return maxStackSize;
    }

    public int getHungerRestored() {
        return hungerRestored;
    }

    public float getSaturationModifier() {
        return saturationModifier;
    }

    public float getProbabilityOfFoodPoisoning() {
        return probabilityOfFoodPoisoning;
    }
}
