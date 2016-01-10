/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.tool.pickaxe;

import com.skelril.nitro.registry.Craftable;
import com.skelril.nitro.registry.ItemTier;
import com.skelril.nitro.registry.item.ItemTiers;
import com.skelril.nitro.registry.item.ItemToolTypes;
import com.skelril.nitro.registry.item.pickaxe.CustomPickaxe;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class HardenedDiamondPickaxe extends CustomPickaxe implements Craftable {
    @Override
    public String __getType() {
        return "hardened_diamond";
    }

    @Override
    public ItemStack __getRepairItemStack() {
        return null;
    }

    @Override
    public double __getHitPower() {
        return ItemTiers.DIAMOND.getDamage() + ItemToolTypes.PICKAXE.getBaseDamage();
    }

    @Override
    public int __getEnchantability() {
        return ItemTiers.DIAMOND.getEnchantability();
    }

    @Override
    public ItemTier __getHarvestTier() {
        return ItemTiers.DIAMOND;
    }

    @Override
    public float __getSpecializedSpeed() {
        return ItemTiers.DIAMOND.getEfficienyOnProperMaterial();
    }

    @Override
    public int __getMaxUses() {
        return ItemTiers.DIAMOND.getDurability();
    }

    @Override
    public void registerRecipes() {
        GameRegistry.addRecipe(
                new ItemStack(this),
                "AAA",
                " B ",
                "   ",
                'A', new ItemStack(Blocks.obsidian),
                'B', new ItemStack(Items.diamond_pickaxe)
        );
    }
}
