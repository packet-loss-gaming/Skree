/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.droptable.roller;

import com.skelril.nitro.droptable.DropTableChanceEntry;
import com.skelril.nitro.modifier.ModifierFunction;
import com.skelril.nitro.modifier.ModifierFunctions;
import com.skelril.nitro.probability.Probability;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

public class SlipperyDiceRoller implements DiceRoller {

    private final ModifierFunction modiFunc;

    public SlipperyDiceRoller() {
        this(ModifierFunctions.NOOP);
    }

    public SlipperyDiceRoller(ModifierFunction modiFunc) {
        this.modiFunc = modiFunc;
    }

    @Override
    public <T extends DropTableChanceEntry> Collection<ItemStack> pickEntry(List<T> input, int highRoll, double modifier) {
        int roll = Probability.getRandom((int) modiFunc.apply(highRoll, modifier));
        ListIterator<T> it = input.listIterator(input.size());

        T cur = null;
        while (it.hasPrevious()) {
            cur = it.previous();

            // Slip through until we hit something which our roll is <= to
            if (cur.getChance() <= roll) {
                break;
            }

            cur = null;
        }

        return cur == null ? null : cur.getItemStacks(modifier);
    }
}
