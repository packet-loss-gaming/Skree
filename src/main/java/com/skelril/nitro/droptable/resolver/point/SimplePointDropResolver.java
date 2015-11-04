/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.droptable.resolver.point;

import com.skelril.nitro.point.ItemStackValueMapping;

import java.util.function.BiFunction;
import java.util.function.Function;

public class SimplePointDropResolver<PointType extends Comparable<PointType>> extends AbstractSlipperyPointResolver<PointType> implements PointDropResolver {
    private final int maxPoints;
    private final BiFunction<Integer, Double, Integer> modiFunc;

    public SimplePointDropResolver(ItemStackValueMapping<PointType> choices, Function<Integer, PointType> pointTypeFromInt, int maxPoints) {
        this(choices, pointTypeFromInt, maxPoints, (a, b) -> (int) (a * b));
    }

    public SimplePointDropResolver(ItemStackValueMapping<PointType> choices, Function<Integer, PointType> pointTypeFromInt, int maxPoints,
                                   BiFunction<Integer, Double, Integer> modiFunc) {
        super(choices, pointTypeFromInt);
        this.maxPoints = maxPoints;
        this.modiFunc = modiFunc;
    }

    @Override
    public int getMaxPoints(double modifier) {
        return modiFunc.apply(maxPoints, modifier);
    }
}
