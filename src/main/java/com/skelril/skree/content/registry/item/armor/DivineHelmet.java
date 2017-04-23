/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.armor;

import com.skelril.nitro.registry.Craftable;
import com.skelril.nitro.registry.item.armor.CustomHelmet;
import com.skelril.skree.content.registry.item.CustomItemTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;

public class DivineHelmet extends CustomHelmet implements Craftable {
    @Override
    public int __getMaxUsesBaseModifier() {
        return 60;
    }

    @Override
    public String __getType() {
        return "divine";
    }

    @Override
    public ItemStack __getRepairItemStack() {
        return new ItemStack(CustomItemTypes.GUARDIAN_HELMET);
    }

    @Override
    public int __getEnchantability() {
        return 10;
    }

    @Override
    public void registerRecipes() {
        GameRegistry.addShapelessRecipe(
                new ItemStack(this),
                new ItemStack(CustomItemTypes.GUARDIAN_HELMET),
                new ItemStack((Item) Sponge.getRegistry().getType(ItemType.class, "skree:unstable_catalyst").get())
        );
    }
}
