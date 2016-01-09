/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.tool.axe;

import com.skelril.nitro.registry.Craftable;
import com.skelril.nitro.registry.ItemTier;
import com.skelril.nitro.registry.item.ItemTiers;
import com.skelril.nitro.registry.item.ItemToolTypes;
import com.skelril.nitro.registry.item.axe.CustomAxe;
import com.skelril.skree.content.registry.item.CustomItemTypes;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class JurackAxe extends CustomAxe implements Craftable {
    @Override
    public String __getType() {
        return "jurack";
    }

    @Override
    public ItemStack __getRepairItemStack() {
        return new ItemStack(CustomItemTypes.JURACK_GEM);
    }

    @Override
    public double __getHitPower() {
        return ItemTiers.JURACK.getDamage() + ItemToolTypes.AXE.getBaseDamage();
    }

    @Override
    public int __getEnchantability() {
        return ItemTiers.JURACK.getEnchantability();
    }

    @Override
    public ItemTier __getHarvestTier() {
        return ItemTiers.JURACK;
    }

    @Override
    public float __getSpecializedSpeed() {
        return ItemTiers.JURACK.getEfficienyOnProperMaterial();
    }

    @Override
    public int __getMaxUses() {
        return ItemTiers.JURACK.getDurability();
    }

    @Override
    public void registerRecipes() {
        GameRegistry.addRecipe(
                new ItemStack(this),
                "AA ",
                "AB ",
                " B ",
                'A', new ItemStack(CustomItemTypes.JURACK_GEM),
                'B', new ItemStack(Items.stick)
        );
    }
}