/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.generic;

import com.skelril.nitro.registry.Craftable;
import com.skelril.nitro.registry.item.CustomItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class SeaCrystal extends CustomItem implements Craftable {

    @Override
    public String __getID() {
        return "sea_crystal";
    }

    @Override
    public int __getMaxStackSize() {
        return 64;
    }

    @Override
    public CreativeTabs __getCreativeTab() {
        return CreativeTabs.MATERIALS;
    }

    @Override
    public void registerRecipes() {
        GameRegistry.addRecipe(
                new ItemStack(this),
                "ABA",
                "BAB",
                "ABA",
                'A', new ItemStack(Items.DIAMOND),
                'B', new ItemStack(Items.PRISMARINE_CRYSTALS)
        );
    }
}
