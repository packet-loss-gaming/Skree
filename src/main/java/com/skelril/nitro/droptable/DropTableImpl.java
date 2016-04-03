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
import java.util.stream.Collectors;

public class DropTableImpl implements DropTable {
    private final DiceRoller roller;
    private ImmutableList<DropTableEntry> possible;

    public DropTableImpl(DiceRoller roller, List<DropTableEntry> possible) {
        this.roller = roller;

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

        for (int i = 0; i < quantity; ++i) {
            Collection<DropTableEntry> hits = roller.getHits(possible, modifier);
            for (DropTableEntry entry : hits) {
                entry.enque(modifier);
            }
        }

        for (DropTableEntry entry : possible) {
            results.addAll(entry.flush());
        }

        return results.stream().map(ItemStack::copy).collect(Collectors.toList());
    }
}
