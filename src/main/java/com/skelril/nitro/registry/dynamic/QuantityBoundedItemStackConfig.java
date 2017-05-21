/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic;

import com.skelril.nitro.probability.Probability;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;

public class QuantityBoundedItemStackConfig extends ItemStackConfig {
    private Range quantity = new Range();

    @Override
    public ItemStack toNSMStack() {
        ItemType spongeType = Sponge.getRegistry().getType(ItemType.class, id).get();
        return new ItemStack((Item) spongeType, Probability.getRangedRandom(quantity.getMin(), quantity.getMax()), data);
    }

    private static class Range {
        private int min = 1;
        private int max = 1;

        public int getMin() {
            return min;
        }

        public int getMax() {
            return max;
        }
    }
}
