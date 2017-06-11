/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.highscore;

import java.text.DecimalFormat;

public class ScoreType {
  private int id;
  private boolean incremental;
  private Order order;

  protected ScoreType(int id, boolean incremental, Order order) {
    this.id = id;
    this.incremental = incremental;
    this.order = order;
  }

  public int getId() {
    return id;
  }

  public boolean isIncremental() {
    return incremental;
  }

  public Order getOrder() {
    return order;
  }

  public String format(int score) {
    DecimalFormat df = new DecimalFormat("#,###");
    return df.format(score);
  }

  public enum Order {
    ASC, DESC
  }
}
