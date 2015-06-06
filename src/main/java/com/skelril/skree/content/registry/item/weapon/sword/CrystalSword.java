/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.weapon.sword;

import com.skelril.nitro.registry.item.CraftableItem;
import com.skelril.nitro.registry.item.sword.CustomSword;
import com.skelril.skree.content.registry.item.CustomItemTypes;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CrystalSword extends CustomSword implements CraftableItem {
    public CrystalSword() {
        super(ToolMaterial.EMERALD);
    }

    @Override
    public String getType() {
        return "crystal";
    }

    @Override
    public double getDamage() {
        return 8;
    }

    @Override
    public void registerRecipes() {
        GameRegistry.addRecipe(
                new ItemStack(this),
                " A ",
                " A ",
                " B ",
                'A', new ItemStack(CustomItemTypes.SEA_CRYSTAL),
                'B', new ItemStack(Items.stick)
        );
    }
}
