/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.tool.terragu;

import com.skelril.nitro.registry.ItemTier;
import com.skelril.nitro.registry.item.ItemTiers;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;

public class IronTerragu extends CustomTerragu {
    @Override
    public String __getType() {
        return "iron";
    }

    @Override
    public ItemStack __getRepairItemStack() {
        return new ItemStack(Items.iron_ingot);
    }
    
    @Override
    public double __getHitPower() {
        return ItemTiers.IRON.getDamage();
    }

    @Override
    public int __getEnchantability() {
        return ItemTiers.IRON.getEnchantability();
    }

    @Override
    public ItemTier __getHarvestTier() {
        return ItemTiers.IRON;
    }

    @Override
    public float __getSpecializedSpeed() {
        return ItemTiers.IRON.getEfficienyOnProperMaterial();
    }

    @Override
    public int __getMaxUses() {
        return ItemTiers.IRON.getDurability();
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
}
