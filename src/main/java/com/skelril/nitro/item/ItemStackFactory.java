/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.item;

import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ItemStackFactory {

    public static ItemStack newItemStack(ItemType type) {
        return newItemStack(type, 1);
    }

    public static ItemStack newItemStack(ItemType type, DataManipulator<?, ?> data) {
        return newItemStack(type, data, 1);
    }

    public static ItemStack newItemStack(ItemType type, int quantity) {
        return ItemStack.builder().itemType(type).quantity(quantity).build();
    }

    public static ItemStack newItemStack(ItemType type, DataManipulator<?, ?> data, int quantity) {
        return ItemStack.builder().itemType(type).quantity(quantity).itemData(data).build();
    }

    public static ItemStack newItemStack(ItemStack itemStack) {
        return ItemStack.builder().fromItemStack(itemStack).build();
    }

    public static ItemStack newItemStack(ItemStack itemStack, int quantity) {
        return ItemStack.builder().fromItemStack(itemStack).quantity(quantity).build();
    }

    public static Collection<ItemStack> newItemStackCollection(ItemStack itemStack, int quantity) {
        List<ItemStack> itemStacks = new ArrayList<>();
        for (int i = quantity; i > 0;) {
            int diff = Math.min(i, itemStack.getMaxStackQuantity());
            i -= diff;
            itemStacks.add(newItemStack(itemStack, quantity));
        }
        return itemStacks;
    }
}
