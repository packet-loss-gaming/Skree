/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.consumable;

import com.skelril.nitro.registry.item.CookedItem;
import com.skelril.nitro.registry.item.CustomItem;
import com.skelril.skree.content.registry.item.CustomItemTypes;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CookedGodFish extends ItemFood implements CustomItem, CookedItem {
    public CookedGodFish() {
        super(10, 1F, false);
        maxStackSize = 16;
        setCreativeTab(CreativeTabs.tabFood);
    }

    @Override
    public String getID() {
        return "cookedGodFish";
    }

    @Override
    public void registerIngredients() {
        GameRegistry.addSmelting(new ItemStack(CustomItemTypes.RAW_GOD_FISH), new ItemStack(this), .45F);
    }
}
