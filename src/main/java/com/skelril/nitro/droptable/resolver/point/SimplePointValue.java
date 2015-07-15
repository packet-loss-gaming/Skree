/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.droptable.resolver.point;

import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Collection;

public class SimplePointValue implements PointValue {

    private final Collection<ItemStack> itemStacks;
    private final int points;

    public SimplePointValue(Collection<ItemStack> itemStacks, int points) {
        this.itemStacks = itemStacks;
        this.points = points;
    }

    @Override
    public Collection<ItemStack> getItemStacks() {
        return itemStacks;
    }

    @Override
    public int getPoints() {
        return points;
    }
}
