/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.droptable.resolver.point;

import org.apache.commons.lang3.Validate;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

public abstract class AbstractSlipperyPointResolver implements PointDropResolver {
    protected final List<PointValue> choices;

    protected AbstractSlipperyPointResolver(List<PointValue> choices) {
        this.choices = choices;
        this.choices.sort((a, b) -> a.getPoints() - b.getPoints());
        Validate.isTrue(!this.choices.isEmpty() && this.choices.get(0).getPoints() > 0);
    }

    @Override
    public Collection<ItemStack> getItemStacks(double modifier) {
        List<ItemStack> results = new ArrayList<>();

        ListIterator<PointValue> it = choices.listIterator(choices.size());

        int points = getMaxPoints(modifier);
        while (it.hasPrevious()) {
            PointValue cur = it.previous();

            int amt = points / cur.getPoints();
            points = points % cur.getPoints();

            for (int i = 0; i < amt; ++i) {
                results.addAll(cur.getItemStacks());
            }
        }
        return results;
    }
}
