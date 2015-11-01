/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.item;

import org.spongepowered.api.item.inventory.ItemStack;

import static net.minecraft.item.ItemStack.areItemStackTagsEqual;
import static net.minecraft.item.ItemStack.areItemsEqual;

public class ItemComparisonUtil {
    public static boolean isSimilar(ItemStack a, ItemStack b) {
        net.minecraft.item.ItemStack stackA = (net.minecraft.item.ItemStack) (Object) a;
        net.minecraft.item.ItemStack stackB = (net.minecraft.item.ItemStack) (Object) b;

        if (stackA.isItemStackDamageable()) {
            return a.getItem() == b.getItem() && areItemStackTagsEqual(stackA, stackB);
        }

        return areItemsEqual(stackA, stackB) && areItemStackTagsEqual(stackA, stackB);
    }
}
