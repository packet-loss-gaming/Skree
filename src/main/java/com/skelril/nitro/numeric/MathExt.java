/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.numeric;

public class MathExt {
    public static double bound(double input, double min, double max) {
        return Math.min(max, Math.max(min, input));
    }

    public static int bound(int input, int min, int max) {
        return Math.min(max, Math.max(min, input));
    }
}
