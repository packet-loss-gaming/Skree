/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.time;

public class TimeFilter {

    private final int min;
    private final int divisible;

    public TimeFilter(int min, int divisible) {
        this.min = min;
        this.divisible = divisible;
    }

    public boolean matchesFilter(int entry) {
        return entry > 0 && entry % divisible == 0 || entry <= min && entry > 0;
    }
}
