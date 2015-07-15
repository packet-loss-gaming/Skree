/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.modifier;

public class ModifierFunctions {
    public static final ModifierFunction NOOP = new ModifierFunction() {
        @Override
        public int apply(int data, int modifierVal) {
            return data;
        }

        @Override
        public double apply(double data, double modifierVal) {
            return data;
        }
    };

    public static final ModifierFunction MULTI = new ModifierFunction() {
        @Override
        public int apply(int data, int modifierVal) {
            return data * modifierVal;
        }

        @Override
        public double apply(double data, double modifierVal) {
            return data * modifierVal;
        }
    };

    public static final ModifierFunction ADD = new ModifierFunction() {

        @Override
        public int apply(int data, int modifierVal) {
            return data + modifierVal;
        }

        @Override
        public double apply(double data, double modifierVal) {
            return data + modifierVal;
        }
    };
}
