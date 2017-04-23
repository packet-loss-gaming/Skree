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
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;

public class JurackPickaxe extends CustomPickaxe implements Craftable {
    @Override
    public String __getType() {
        return "jurack";
    }

    @Override
    public ItemStack __getRepairItemStack() {
        return new ItemStack((Item) Sponge.getRegistry().getType(ItemType.class, "skree:jurack").get());
    }

    @Override
    public double __getHitPower() {
        return ItemTiers.JURACK.getDamage() + ItemToolTypes.PICKAXE.getBaseDamage();
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
                "AAA",
                " B ",
                " B ",
                'A', new ItemStack((Item) Sponge.getRegistry().getType(ItemType.class, "skree:jurack").get()),
                'B', new ItemStack(Items.STICK)
        );
    }
}
