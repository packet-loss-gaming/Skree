/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.droptable.resolver;

import org.spongepowered.api.item.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;

public class SimpleDropResolver implements DropResolver {
    private Collection<ItemStack> queue = getBaseCollection();
    private Collection<ItemStack> itemStacks;

    public SimpleDropResolver(Collection<ItemStack> itemStacks) {
        this.itemStacks = itemStacks;
    }

    public Collection<ItemStack> getBaseCollection() {
        return new ArrayList<>();
    }

    @Override
    public void enqueue(double modifier) {
        queue.addAll(itemStacks);
    }

    @Override
    public Collection<ItemStack> flush() {
        Collection<ItemStack> itemStacks = queue;
        queue = getBaseCollection();
        return itemStacks;
    }
}
