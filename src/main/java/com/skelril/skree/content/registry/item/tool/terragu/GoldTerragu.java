/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.tool.terragu;

import com.skelril.nitro.registry.HarvestTier;
import com.skelril.nitro.registry.item.HarvestTiers;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;

public class GoldTerragu extends CustomTerragu {
    @Override
    public String __getType() {
        return "golden";
    }

    @Override
    public ItemStack __getRepairItemStack() {
        return new ItemStack(Items.gold_ingot);
    }

    @Override
    public double __getHitPower() {
        return ToolMaterial.GOLD.getDamageVsEntity();
    }

    @Override
    public int __getEnchantability() {
        return ToolMaterial.GOLD.getEnchantability();
    }

    @Override
    public HarvestTier __getHarvestTier() {
        return HarvestTiers.GOLD;
    }

    @Override
    public float __getSpecializedSpeed() {
        return ToolMaterial.GOLD.getEfficiencyOnProperMaterial();
    }

    @Override
    public int __getMaxUses() {
        return ToolMaterial.GOLD.getMaxUses();
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
