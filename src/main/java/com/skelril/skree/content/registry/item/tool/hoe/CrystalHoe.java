/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.tool.hoe;

import com.skelril.nitro.registry.Craftable;
import com.skelril.nitro.registry.item.ItemTiers;
import com.skelril.nitro.registry.item.hoe.CustomHoe;
import com.skelril.skree.content.registry.item.CustomItemTypes;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CrystalHoe extends CustomHoe implements Craftable {
    @Override
    public String __getType() {
        return "crystal";
    }

    @Override
    public int __getMaxUses() {
        return ItemTiers.CRYSTAL.getDurability();
    }

    @Override
    public void registerRecipes() {
        GameRegistry.addRecipe(
                new ItemStack(this),
                "AA ",
                " B ",
                " B ",
                'A', new ItemStack(CustomItemTypes.SEA_CRYSTAL),
                'B', new ItemStack(Items.stick)
        );
    }
}
