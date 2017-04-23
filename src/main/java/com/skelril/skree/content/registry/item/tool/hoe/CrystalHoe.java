/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.tool.hoe;

import com.skelril.nitro.registry.Craftable;
import com.skelril.nitro.registry.ItemTier;
import com.skelril.nitro.registry.item.ItemTiers;
import com.skelril.nitro.registry.item.hoe.CustomHoe;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;

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
    public ItemTier __getHarvestTier() {
        return ItemTiers.CRYSTAL;
    }

    @Override
    public void registerRecipes() {
        GameRegistry.addRecipe(
                new ItemStack(this),
                "AA ",
                " B ",
                " B ",
                'A', new ItemStack((Item) Sponge.getRegistry().getType(ItemType.class, "skree:sea_crystal").get()),
                'B', new ItemStack(Items.STICK)
        );
    }
}
