/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.point;

import com.skelril.nitro.Clause;
import com.skelril.nitro.item.ItemStackFactory;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ItemStackValueMapping<PointType extends Comparable<PointType>> extends ValueMapping<ItemStack, PointType> {
    private Function<Integer, PointType> pointTypeFromInt;
    private Function<PointType, Integer> pointTypeToInt;

    private BiFunction<PointType, PointType, PointType> pointTypeSub, pointTypeMulti;


    public ItemStackValueMapping(List<PointValue<ItemStack, PointType>> pointValues, PointType zeroValue, PointType oneValue,
                                 Function<Integer, PointType> pointTypeFromInt,
                                 Function<PointType, Integer> pointTypeToInt,
                                 BiFunction<PointType, PointType, PointType> pointTypeSub,
                                 BiFunction<PointType, PointType, PointType> pointTypeMulti,
                                 BiFunction<PointType, PointType, PointType> pointTypeDiv,
                                 BiFunction<PointType, PointType, PointType> pointTypeMod) {
        super(pointValues, zeroValue, oneValue, pointTypeDiv, pointTypeMod);
        this.pointTypeFromInt = pointTypeFromInt;
        this.pointTypeToInt = pointTypeToInt;
        this.pointTypeSub = pointTypeSub;
        this.pointTypeMulti = pointTypeMulti;
    }

    private PointType min(PointType a, PointType b) {
        return a.compareTo(b) > 0 ? b : a;
    }

    @Override
    protected Collection<ItemStack> collect(Collection<ItemStack> satisfiers, PointType amt) {
        List<ItemStack> itemStacks = new ArrayList<>();
        for (ItemStack satisfier : satisfiers) {
            PointType baseQuantity = pointTypeFromInt.apply(satisfier.getQuantity());
            PointType maxQuantity = pointTypeFromInt.apply(satisfier.getMaxStackQuantity());

            PointType total = pointTypeMulti.apply(baseQuantity, amt);
            while (total.compareTo(zeroValue) > 0) {
                PointType increment = min(total, maxQuantity);
                total = pointTypeSub.apply(total, increment);
                itemStacks.add(ItemStackFactory.newItemStack(satisfier, pointTypeToInt.apply(increment)));
            }
        }
        return itemStacks;
    }

    private List<Clause<ItemStack, ItemStack>> getComparisonList(Collection<ItemStack> stacks) {
        return stacks.stream().map(stack -> {
            ItemStack stackCopy = stack.copy();
            stackCopy.setQuantity(1);
            return new Clause<>(stackCopy, stack);
        }).collect(Collectors.toList());
    }

    @Override
    protected Optional<PointType> matches(Collection<ItemStack> a, Collection<ItemStack> b, PointType matchPoints) {
        if (a.size() != b.size()) {
            return Optional.empty();
        }

        int factor = Integer.MAX_VALUE;

        List<Clause<ItemStack, ItemStack>> comparisonListA = getComparisonList(a);
        List<Clause<ItemStack, ItemStack>> comparisonListB = getComparisonList(b);

        for (Clause<ItemStack, ItemStack> aStack : comparisonListA) {
            boolean matchFound = false;
            for (Clause<ItemStack, ItemStack> bStack : comparisonListB) {
                if (aStack.getKey().equals(bStack.getKey())) {
                    matchFound = true;
                    factor = Math.min(factor, bStack.getValue().getQuantity() / aStack.getValue().getQuantity());
                    break;
                }
            }

            if (!matchFound) {
                return Optional.empty();
            }
        }

        return Optional.of(pointTypeMulti.apply(pointTypeFromInt.apply(factor), matchPoints));
    }
}
