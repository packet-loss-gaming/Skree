/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.item;

import com.skelril.nitro.point.ItemStackValueMapping;
import org.spongepowered.api.item.inventory.ItemStack;

import java.math.BigInteger;
import java.util.*;

public class ItemCompactor {
    private List<ItemStackValueMapping<BigInteger>> valueMappings;

    public ItemCompactor(List<ItemStackValueMapping<BigInteger>> valueMappings) {
        this.valueMappings = valueMappings;
    }

    public Optional<ItemStack[]> execute(ItemStack[] input) {
        ItemStack[] output = Arrays.copyOf(input, input.length);
        int[] countBuckets = new int[valueMappings.size()];
        BigInteger[] pointBuckets = new BigInteger[valueMappings.size()];
        for (int i = 0; i < pointBuckets.length; ++i) {
            pointBuckets[i] = BigInteger.ZERO;
        }

        for (int i = 0; i < input.length; ++i) {
            Collection<ItemStack> stack = Collections.singleton(input[i]);
            for (int k = 0; k < valueMappings.size(); ++k) {
                Optional<BigInteger> value = valueMappings.get(k).getValue(stack);
                if (value.isPresent()) {
                    output[i] = null;
                    countBuckets[k] += input[i].getQuantity();
                    pointBuckets[k] = pointBuckets[k].add(value.get());
                    break;
                }
            }
        }

        Queue<ItemStack> collectedStacks = new ArrayDeque<>();
        boolean canMinimize = false;
        for (int k = 0; k < valueMappings.size(); ++k) {
            Collection<ItemStack> stacks = valueMappings.get(k).satisfy(pointBuckets[k]);

            int minimumSum = 0;
            for (ItemStack stack : stacks) {
                minimumSum += stack.getQuantity();
            }

            if (minimumSum < countBuckets[k]) {
                canMinimize = true;
            }
            collectedStacks.addAll(stacks);
        }

        if (canMinimize) {
            for (int i = 0; i < output.length; ++i) {
                if (collectedStacks.isEmpty()) {
                    break;
                }

                if (output[i] == null) {
                    output[i] = collectedStacks.poll();
                }
            }
            // Cover a very rare case where items may be lost
            // TODO do this better
            return collectedStacks.isEmpty() ? Optional.of(output) : Optional.empty();
        }
        return Optional.empty();
    }
}
