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

public class DiamondTerragu extends CustomTerragu {
    @Override
    public String __getType() {
        return "diamond";
    }

    @Override
    public ItemStack __getRepairItemStack() {
        return new ItemStack(Items.diamond);
    }

    @Override
    public double __getHitPower() {
        return ToolMaterial.EMERALD.getDamageVsEntity();
    }

    @Override
    public int __getEnchantability() {
        return ToolMaterial.EMERALD.getEnchantability();
    }

    @Override
    public ItemTier __getHarvestTier() {
        return ItemTiers.DIAMOND;
    }

    @Override
    public float __getSpecializedSpeed() {
        return ToolMaterial.EMERALD.getEfficiencyOnProperMaterial();
    }

    @Override
    public int __getMaxUses() {
        return ToolMaterial.EMERALD.getMaxUses();
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
