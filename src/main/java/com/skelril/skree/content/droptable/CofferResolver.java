/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.droptable;

import com.skelril.nitro.droptable.resolver.point.SimplePointDropResolver;
import com.skelril.skree.content.registry.item.currency.CofferValueMap;

import java.math.BigInteger;
import java.util.function.BiFunction;

public class CofferResolver extends SimplePointDropResolver<BigInteger> {
  public CofferResolver(int maxCoffers) {
    this(maxCoffers, (a, b) -> (int) (a * b));
  }

  public CofferResolver(int maxCoffers, BiFunction<Integer, Double, Integer> modifierFunction) {
    super(CofferValueMap.inst(), BigInteger::valueOf, maxCoffers, modifierFunction);
  }
}
