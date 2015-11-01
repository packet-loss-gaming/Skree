/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.point;

import java.util.Collection;

public class SimplePointValue<KeyType, PointType extends Comparable<PointType>> implements PointValue<KeyType, PointType> {
    private final Collection<KeyType> itemStacks;
    private final PointType points;

    public SimplePointValue(Collection<KeyType> itemStacks, PointType points) {
        this.itemStacks = itemStacks;
        this.points = points;
    }

    @Override
    public Collection<KeyType> getSatisfiers() {
        return itemStacks;
    }

    @Override
    public PointType getPoints() {
        return points;
    }

    @Override
    public int compareTo(PointValue<KeyType, PointType> pointValue) {
        return this.points.compareTo(pointValue.getPoints());
    }
}
