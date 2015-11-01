/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.droptable.resolver.point;

import com.skelril.nitro.modifier.ModifierFunction;
import com.skelril.nitro.modifier.ModifierFunctions;
import com.skelril.nitro.point.ValueMapping;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.function.Function;

public class SimplePointDropResolver<PointType extends Comparable<PointType>> extends AbstractSlipperyPointResolver<PointType> implements PointDropResolver {
    private final int maxPoints;
    private final ModifierFunction modiFunc;

    public SimplePointDropResolver(ValueMapping<ItemStack, PointType> choices, Function<Integer, PointType> pointTypeFromInt, int maxPoints) {
        this(choices, pointTypeFromInt, maxPoints, ModifierFunctions.MULTI);
    }

    public SimplePointDropResolver(ValueMapping<ItemStack, PointType> choices, Function<Integer, PointType> pointTypeFromInt, int maxPoints, ModifierFunction modiFunc) {
        super(choices, pointTypeFromInt);
        this.maxPoints = maxPoints;
        this.modiFunc = modiFunc;
    }

    @Override
    public int getMaxPoints(double modifier) {
        return (int) this.modiFunc.apply(maxPoints, modifier);
    }
}
