/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public abstract class CustomItem extends Item implements ICustomItem {
    protected CustomItem() {
        this.maxStackSize = __getMaxStackSize();
        this.setCreativeTab(__getCreativeTab());

        if (this instanceof DegradableItem) {
            this.setMaxDamage(((DegradableItem) this).__getMaxUses());
        }

        if (__getMeshDefinitions().size() > 1) {
            this.setMaxDamage(0);
            this.setHasSubtypes(true);
        }
    }

    @SideOnly(Side.CLIENT)
    public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        ICustomItem.super.getSubItems(itemIn, tab, subItems);
    }
}
