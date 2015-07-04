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
    @Override
    public int __getMaxUses() {
        return ToolMaterial.EMERALD.getMaxUses();
    }

    @Override
    public ItemStack __getRepairItemStack() {
        return new ItemStack(CustomItemTypes.SEA_CRYSTAL);
    }

    @Override
    public double __getHitPower() {
        return 8;
    }

    @Override
    public int __getEnchantability() {
        return ToolMaterial.EMERALD.getEnchantability();
    }

    @Override
    public String __getType() {
        return "crystal";
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
