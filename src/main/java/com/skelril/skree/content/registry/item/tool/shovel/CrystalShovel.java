/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.tool.shovel;

import com.skelril.nitro.registry.HarvestTier;
import com.skelril.nitro.registry.item.CraftableItem;
import com.skelril.nitro.registry.item.HarvestTiers;
import com.skelril.nitro.registry.item.shovel.CustomShovel;
import com.skelril.skree.content.registry.item.CustomItemTypes;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CrystalShovel extends CustomShovel implements CraftableItem {
    @Override
    public String __getType() {
        return "crystal";
    }

    @Override
    public ItemStack __getRepairItemStack() {
        return new ItemStack(CustomItemTypes.SEA_CRYSTAL);
    }

    @Override
    public double __getHitPower() {
        return 5;
    }

    @Override
    public int __getEnchantability() {
        return ToolMaterial.EMERALD.getEnchantability();
    }

    @Override
    public HarvestTier __getHarvestTier() {
        return HarvestTiers.CRYSTAL;
    }

    @Override
    public float __getSpecializedSpeed() {
        return 10.0F;
    }

    @Override
    public int __getMaxUses() {
        return ToolMaterial.EMERALD.getMaxUses();
    }

    @Override
    public void registerRecipes() {
        GameRegistry.addRecipe(
                new ItemStack(this),
                " A ",
                " B ",
                " B ",
                'A', new ItemStack(CustomItemTypes.SEA_CRYSTAL),
                'B', new ItemStack(Items.stick)
        );
    }
}
