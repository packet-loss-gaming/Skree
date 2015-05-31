/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.droptable;

import org.spongepowered.api.item.inventory.ItemStack;

public class DropTableEntryImpl implements DropTableEntry {
    private final ItemStack stack;
    private final int chance;

    public DropTableEntryImpl(ItemStack stack, int chance) {
        this.stack = stack;
        this.chance = chance;
    }

    @Override
    public ItemStack getItemStack() {
        return stack;
    }

    @Override
    public int getChance() {
        return chance;
    }
}
