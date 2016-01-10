/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.zone;

import com.skelril.skree.content.registry.item.CustomItemTypes;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapelessRecipes;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;

import java.util.Arrays;

import static com.skelril.skree.content.registry.item.zone.ZoneItemUtil.setMasterToZone;
import static com.skelril.skree.content.registry.item.zone.ZoneItemUtil.setMasterToZoneBasic;

public class ZoneMasterOrbRecipie extends ShapelessRecipes {
    private final String zone;

    public ZoneMasterOrbRecipie(String zone, Object... inputList) {
        super(setMasterToZoneBasic(new ItemStack(CustomItemTypes.ZONE_MASTER_ORB), zone), Arrays.asList(inputList));
        this.zone = zone;
    }

    private ItemStack getFull() {
        ItemStack stack = new ItemStack(CustomItemTypes.ZONE_MASTER_ORB);
        setMasterToZone(stack, zone);
        return stack;
    }

    @Override
    public ItemStack getRecipeOutput() {
        if (Sponge.getPlatform().getType() == Platform.Type.CLIENT) {
            return super.getRecipeOutput();
        }
        return getFull();
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        if (Sponge.getPlatform().getType() == Platform.Type.CLIENT) {
            return super.getCraftingResult(inv);
        }
        return getFull();
    }
}
