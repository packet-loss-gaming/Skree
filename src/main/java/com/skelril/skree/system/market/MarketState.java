/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.market;

public class MarketState {
  private long lastUpdate;

  public long getLastUpdate() {
    return lastUpdate;
  }

  public void setLastUpdate(long lastUpdate) {
    this.lastUpdate = lastUpdate;
  }
}
