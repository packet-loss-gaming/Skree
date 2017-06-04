/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.market;

import java.math.BigDecimal;

public class ItemDescriptor {
  private final String name;
  private final BigDecimal currentValue;
  private final int stock;

  public ItemDescriptor(String name, BigDecimal currentValue, int stock) {
    this.name = name;
    this.currentValue = currentValue;
    this.stock = stock;
  }

  public String getName() {
    return name;
  }

  public BigDecimal getCurrentValue() {
    return currentValue;
  }

  public int getStock() {
    return stock;
  }
}
