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
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;

public class DiamondTerragu extends CustomTerragu implements Craftable {
    @Override
    public String __getType() {
        return "diamond";
    }

    @Override
    public ItemStack __getRepairItemStack() {
        return null;
    }
    
    @Override
    public double __getHitPower() {
        return ItemTiers.DIAMOND.getDamage();
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
        return ItemTiers.DIAMOND.getDurability() * 10;
    }

    @Listener
    public void process(InteractBlockEvent.Primary event) {
        super.process(event);
    }

    @Listener
    public void process(InteractBlockEvent.Secondary event) {
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
                new ItemStack(Items.diamond_pickaxe),
                new ItemStack(Items.diamond_axe),
                new ItemStack(Items.diamond_shovel),
                new ItemStack(CustomItemTypes.UNSTABLE_CATALYST)
        );
    }
}
