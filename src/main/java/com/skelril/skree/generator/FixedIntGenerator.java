/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.generator;

public class FixedIntGenerator implements Generator<Integer> {

    private final int res;

    public FixedIntGenerator(int res) {
        this.res = res;
    }

    @Override
    public Integer get() {
        return res;
    }
}
