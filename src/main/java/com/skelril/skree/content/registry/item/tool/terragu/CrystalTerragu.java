/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.tool.terragu;

import com.skelril.nitro.registry.Craftable;
import com.skelril.nitro.registry.ItemTier;
import com.skelril.nitro.registry.item.ItemTiers;
import com.skelril.skree.content.registry.item.CustomItemTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.item.ItemType;

public class CrystalTerragu extends CustomTerragu implements Craftable {
    @Override
    public String __getType() {
        return "crystal";
    }

    @Override
    public ItemStack __getRepairItemStack() {
        return null;
    }

    @Override
    public double __getHitPower() {
        return ItemTiers.CRYSTAL.getDamage();
    }

    @Override
    public int __getEnchantability() {
        return ItemTiers.CRYSTAL.getEnchantability();
    }

    @Override
    public ItemTier __getHarvestTier() {
        return ItemTiers.CRYSTAL;
    }

    @Override
    public float __getSpecializedSpeed() {
        return ItemTiers.CRYSTAL.getEfficienyOnProperMaterial();
    }

    @Override
    public int __getMaxUses() {
        return ItemTiers.CRYSTAL.getDurability() * 10;
    }

    @Listener
    public void process(InteractBlockEvent.Primary.MainHand event) {
        super.process(event);
    }

    @Listener
    public void process(InteractBlockEvent.Secondary.MainHand event) {
        super.process(event);
    }

    @Listener
    public void process(ChangeBlockEvent.Break event) {
        super.process(event);
    }

    @Override
    public void registerRecipes() {
        GameRegistry.addShapelessRecipe(
                new ItemStack(this),
                new ItemStack(CustomItemTypes.CRYSTAL_PICKAXE),
                new ItemStack(CustomItemTypes.CRYSTAL_AXE),
                new ItemStack(CustomItemTypes.CRYSTAL_SHOVEL),
                new ItemStack((Item) Sponge.getRegistry().getType(ItemType.class, "skree:unstable_catalyst").get())
        );
    }
}
