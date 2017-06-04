/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.point;

import org.spongepowered.api.item.inventory.ItemStack;

import java.math.BigInteger;
import java.util.List;

public class ItemStackBigIntegerValueMapping extends ItemStackValueMapping<BigInteger> {
  public ItemStackBigIntegerValueMapping(List<PointValue<ItemStack, BigInteger>> pointValues) {
    super(pointValues, BigInteger.ZERO, BigInteger.ONE, BigInteger::valueOf, BigInteger::intValue, BigInteger::subtract, BigInteger::multiply, BigInteger::divide, BigInteger::remainder);
  }
}
