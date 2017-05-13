/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.item.food;

import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.potion.PotionEffect;

class LoadedFood extends ItemFood {
    private FoodConfig config;

    protected LoadedFood(FoodConfig config) {
        super(config.getHungerRestored(), config.getSaturationModifier(), false);
        this.config = config;

        setMaxStackSize();
        setFoodPoisoningChance();
    }

    // Config Loading

    private void setMaxStackSize() {
        this.setMaxStackSize(config.getMaxStackSize());
    }

    private void setFoodPoisoningChance() {
        float probability = config.getProbabilityOfFoodPoisoning();
        if (probability > 0) {
            this.setPotionEffect(new PotionEffect(MobEffects.HUNGER, 600, 0), probability);
        }
    }
}
