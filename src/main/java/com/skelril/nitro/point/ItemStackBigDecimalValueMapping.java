/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.point;

import org.spongepowered.api.item.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.List;

public class ItemStackBigDecimalValueMapping extends ItemStackValueMapping<BigDecimal> {
    public ItemStackBigDecimalValueMapping(List<PointValue<ItemStack, BigDecimal>> pointValues) {
        super(pointValues, BigDecimal.ZERO, BigDecimal.ONE, BigDecimal::new, BigDecimal::intValue, BigDecimal::subtract, BigDecimal::multiply, BigDecimal::divide, BigDecimal::remainder);
    }
}
