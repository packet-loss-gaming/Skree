/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.probability;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public class Probability {
    private static Random r = new Random(System.currentTimeMillis());

    /**
     * Obtains a random element from the provided {@link java.util.List}
     */
    public static <T> T pickOneOf(List<T> list) {
        return list.get(getRandom(list.size()) - 1);
    }

    /**
     * Obtains a random element from the provided {@link java.util.Collection}
     */
    public static <T> T pickOneOf(Collection<T> collection) {
        return pickOneOf(Lists.newArrayList(collection));
    }

    /**
     * Obtains a random element from the provided array
     */
    public static <T> T pickOneOf(T[] arr) {
        return arr[getRandom(arr.length) - 1];
    }

    public static int getRandom(int highestValue) {
        return highestValue == 0 ? 1 : highestValue < 0 ? (r.nextInt(highestValue * -1) + 1) * -1 : r.nextInt(highestValue) + 1;
    }

    public static int getRangedRandom(int lowestValue, int highestValue) {
        if (lowestValue == highestValue) return lowestValue;
        return lowestValue + getRandom((highestValue + 1) - lowestValue) - 1;
    }

    public static double getRandom(double highestValue) {
        if (highestValue <= 1 && highestValue > 0) {
            return r.nextDouble();
        }
        if (highestValue < 0) {
            return (r.nextDouble() * (highestValue * -1)) * -1;
        }
        return (r.nextDouble() * (highestValue - 1)) + 1;
    }

    public static double getRangedRandom(double lowestValue, double highestValue) {
        if (lowestValue == highestValue) return lowestValue;
        return lowestValue + getRandom((highestValue + 1) - lowestValue) - 1;
    }

    public static boolean getChance(Number number) {
        return getChance(number.intValue());
    }

    public static boolean getChance(int outOf) {
        return getChance(1, outOf);
    }

    public static boolean getChance(int chance, int outOf) {
        return getRandom(outOf) <= chance;
    }
}
