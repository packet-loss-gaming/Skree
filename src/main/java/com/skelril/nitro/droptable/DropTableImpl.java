/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.droptable;

import com.google.common.collect.Lists;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DropTableImpl implements DropTable {
    private final DiceRoller roller;
    private List<DropTableEntryImpl> entries;

    public DropTableImpl(DiceRoller roller, DropTableEntryImpl... entries) {
        this.roller = roller;
        this.entries = Lists.newArrayList(entries);
    }

    @Override
    public Collection<ItemStack> getDrops(int quantity) {
        return getDrops(quantity, roller);
    }

    @Override
    public Collection<ItemStack> getDrops(int quantity, DiceRoller roller) {
        List<ItemStack> results = new ArrayList<>();
        for (int i = 0; i < quantity; ++i) {
            results.add(roller.pickEntry(entries));
        }
        return results;
    }
}
