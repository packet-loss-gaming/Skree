/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.droptable.roller;

import com.google.common.collect.ImmutableList;
import com.skelril.nitro.droptable.DropTableEntry;
import com.skelril.nitro.modifier.ModifierFunction;
import com.skelril.nitro.modifier.ModifierFunctions;
import com.skelril.nitro.probability.Probability;

import java.util.Collection;
import java.util.Collections;
import java.util.ListIterator;

public class SlipperySingleHitDiceRoller implements DiceRoller {

    private final ModifierFunction modiFunc;

    public SlipperySingleHitDiceRoller() {
        this(ModifierFunctions.NOOP);
    }

    public SlipperySingleHitDiceRoller(ModifierFunction modiFunc) {
        this.modiFunc = modiFunc;
    }

    @Override
    public <T extends DropTableEntry> Collection<T> getHits(ImmutableList<T> input, double modifier) {
        ListIterator<T> it = input.listIterator(input.size());

        T cur = null;
        while (it.hasPrevious()) {
            cur = it.previous();

            // Slip through until something which is >= the chance is hit, unless a modifier is applied
            // this is equivalent to a 1 / chance probability
            if (cur.getChance() >= Probability.getRandom((int) modiFunc.apply(cur.getChance(), modifier))) {
                break;
            }

            cur = null;
        }

        return cur == null ? Collections.emptySet() : Collections.singleton(cur);
    }
}
