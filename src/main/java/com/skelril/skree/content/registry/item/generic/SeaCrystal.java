/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.generic;

import com.skelril.nitro.registry.item.CraftableItem;
import com.skelril.nitro.registry.item.CustomItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class SeaCrystal extends Item implements CustomItem, CraftableItem {

    public SeaCrystal() {
        maxStackSize = 64;
        setCreativeTab(CreativeTabs.tabMaterials);
    }

    @Override
    public String getID() {
        return "seaCrystal";
    }

    @Override
    public void registerRecipes() {
        GameRegistry.addRecipe(
                new ItemStack(this),
                "ABA",
                "BAB",
                "ABA",
                'A', new ItemStack(Items.diamond),
                'B', new ItemStack(Items.prismarine_crystals)
        );
    }
}
