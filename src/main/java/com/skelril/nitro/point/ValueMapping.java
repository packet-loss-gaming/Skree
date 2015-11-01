/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.point;


import com.google.common.collect.Multimap;
import org.apache.commons.lang3.Validate;

import java.util.*;
import java.util.function.BiFunction;

public abstract class ValueMapping<KeyType, PointType extends Comparable<PointType>> {
    List<PointValue<KeyType, PointType>> values;
    PointType zeroValue, oneValue;
    BiFunction<PointType, PointType, PointType> pointTypeDiv, pointTypeMod;

    Multimap<KeyType, PointValue<KeyType, PointType>> valueMap;

    public ValueMapping(List<PointValue<KeyType, PointType>> values, PointType zeroValue, PointType oneValue,
                        BiFunction<PointType, PointType, PointType> pointTypeDiv,
                        BiFunction<PointType, PointType, PointType> pointTypeMod) {
        this.values = values;
        this.zeroValue = zeroValue;
        this.oneValue = oneValue;
        this.pointTypeDiv = pointTypeDiv;
        this.pointTypeMod = pointTypeMod;

        Collections.sort(this.values);
        Validate.isTrue(!this.values.isEmpty() && this.values.get(0).getPoints().compareTo(zeroValue) > 0);

        for (PointValue<KeyType, PointType> pointVal : this.values) {
            for (KeyType satisfier : pointVal.getSatisfiers()) {
                valueMap.put(satisfier, pointVal);
            }
        }
    }

    public Collection<KeyType> getBestSatisifers(PointType value) {
        ListIterator<PointValue<KeyType, PointType>> satisfier = values.listIterator(values.size());
        while (satisfier.hasPrevious()) {
            PointValue<KeyType, PointType> curVal = satisfier.previous();
            PointType type = curVal.getPoints();
            if (type.compareTo(value) <= 0) {
                return curVal.getSatisfiers();
            }
        }
        return Collections.emptyList();
    }

    protected abstract Collection<KeyType> collect(Collection<KeyType> satisfiers, PointType amt);

    public Collection<KeyType> satisfy(PointType value) {
        List<KeyType> results = new ArrayList<>();
        ListIterator<PointValue<KeyType, PointType>> it = values.listIterator(values.size());

        while (it.hasPrevious()) {
            PointValue<KeyType, PointType> cur = it.previous();

            PointType amt = pointTypeDiv.apply(value, cur.getPoints());
            value = pointTypeMod.apply(value, cur.getPoints());

            results.addAll(collect(cur.getSatisfiers(), amt));
        }
        return results;
    }

    protected abstract Optional<PointType> matches(Collection<KeyType> a, Collection<KeyType> b, PointType matchPoints);

    public Optional<PointType> getValue(Collection<KeyType> key) {
        Validate.isTrue(!key.isEmpty());
        Collection<PointValue<KeyType, PointType>> possibleMatches = valueMap.get(key.iterator().next());
        for (PointValue<KeyType, PointType> possibleMatch : possibleMatches) {
            Optional<PointType> optPoints = matches(key, possibleMatch.getSatisfiers(), possibleMatch.getPoints());
            if (optPoints.isPresent()) {
                return optPoints;
            }
        }
        return Optional.empty();
    }
}
