/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.consumable;

import com.skelril.nitro.registry.item.CookedItem;
import com.skelril.nitro.registry.item.food.CustomFood;
import com.skelril.skree.content.registry.item.CustomItemTypes;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CookedGodFish extends CustomFood implements CookedItem {

    @Override
    public String __getID() {
        return "cooked_god_fish";
    }

    @Override
    public int __getMaxStackSize() {
        return 16;
    }

    @Override
    public double __getHealAmount() {
        return 10;
    }

    @Override
    public double __getSaturationModifier() {
        return 1F;
    }

    @Override
    public void registerIngredients() {
        GameRegistry.addSmelting(new ItemStack(CustomItemTypes.RAW_GOD_FISH), new ItemStack(this), .45F);
    }
}
