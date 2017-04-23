/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.armor;

import com.skelril.nitro.registry.Craftable;
import com.skelril.nitro.registry.item.armor.CustomBoots;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;

public class TormentorBoots extends CustomBoots implements Craftable {
    @Override
    public int __getMaxUsesBaseModifier() {
        return 60;
    }

    @Override
    public String __getType() {
        return "tormentor";
    }

    @Override
    public ItemStack __getRepairItemStack() {
        return new ItemStack((Item) Sponge.getRegistry().getType(ItemType.class, "skree:demonic_ingot").get());
    }

    @Override
    public int __getEnchantability() {
        return 10;
    }

    @Override
    public void registerRecipes() {
        GameRegistry.addRecipe(
                new ItemStack(this),
                "   ",
                "A A",
                "A A",
                'A', new ItemStack((Item) Sponge.getRegistry().getType(ItemType.class, "skree:demonic_ingot").get())
        );
    }
}