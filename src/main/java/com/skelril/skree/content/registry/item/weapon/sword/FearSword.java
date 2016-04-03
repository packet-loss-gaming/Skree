/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.weapon.sword;

import com.skelril.nitro.registry.Craftable;
import com.skelril.nitro.registry.item.ItemTiers;
import com.skelril.nitro.registry.item.ItemToolTypes;
import com.skelril.nitro.registry.item.sword.CustomSword;
import com.skelril.skree.content.registry.item.CustomItemTypes;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class FearSword extends CustomSword implements Craftable {

    @Override
    public String __getType() {
        return "fear";
    }

    @Override
    public ItemStack __getRepairItemStack() {
        return new ItemStack(CustomItemTypes.DEMONIC_SWORD);
    }

    @Override
    public int __getMaxUses() {
        return ItemTiers.CRYSTAL.getDurability();
    }

    @Override
    public double __getHitPower() {
        return ItemTiers.CRYSTAL.getDamage() + ItemToolTypes.SWORD.getBaseDamage();
    }

    @Override
    public int __getEnchantability() {
        return ItemTiers.CRYSTAL.getEnchantability();
    }

    @Override
    public void registerRecipes() {
        GameRegistry.addShapelessRecipe(
                new ItemStack(this),
                new ItemStack(CustomItemTypes.DEMONIC_SWORD),
                new ItemStack(CustomItemTypes.UNSTABLE_CATALYST)
        );
    }
}