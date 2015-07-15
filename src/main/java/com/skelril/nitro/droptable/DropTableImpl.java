/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.droptable;

import com.google.common.collect.ImmutableList;
import com.skelril.nitro.droptable.roller.DiceRoller;
import org.apache.commons.lang3.Validate;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DropTableImpl implements DropTable {
    private final DiceRoller roller;
    private ImmutableList<DropTableEntry> guaranteed;
    private ImmutableList<DropTableChanceEntry> possible;

    public DropTableImpl(DiceRoller roller, List<DropTableEntry> guaranteed, List<DropTableChanceEntry> possible) {
        this.roller = roller;
        this.guaranteed = ImmutableList.copyOf(guaranteed);

        // First sort possible, then apply
        possible.sort((a, b) -> a.getChance() - b.getChance());
        Validate.isTrue(!possible.isEmpty() && possible.get(0).getChance() > 0);

        this.possible = ImmutableList.copyOf(possible);
    }

    @Override
    public Collection<ItemStack> getDrops(int quantity) {
        return getDrops(quantity, 1, roller);
    }

    @Override
    public Collection<ItemStack> getDrops(int quantity, double modifier) {
        return getDrops(quantity, modifier, roller);
    }

    @Override
    public Collection<ItemStack> getDrops(int quantity, DiceRoller roller) {
        return getDrops(quantity, 1, roller);
    }

    @Override
    public Collection<ItemStack> getDrops(int quantity, double modifier, DiceRoller roller) {
        List<ItemStack> results = new ArrayList<>();
        guaranteed.forEach(e -> results.addAll(e.getItemStacks(modifier)));

        DropTableChanceEntry last = possible.get(possible.size() - 1);

        for (int i = 0; i < quantity; ++i) {
            results.addAll(roller.pickEntry(possible, last.getChance(), modifier));
        }
        return results;
    }
}
