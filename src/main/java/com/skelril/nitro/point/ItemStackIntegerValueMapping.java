/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.point;

import org.spongepowered.api.item.inventory.ItemStack;

import java.util.List;

public class ItemStackIntegerValueMapping extends ItemStackValueMapping<Integer> {
    public ItemStackIntegerValueMapping(List<PointValue<ItemStack, Integer>> values) {
        super(values, 0, 1, (a) -> a, (a) -> a, (a, b) -> a - b, (a, b) -> a * b, (a, b) -> a / b, (a, b) -> a % b);
    }
}
