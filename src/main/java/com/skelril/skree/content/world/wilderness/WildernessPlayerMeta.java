/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.wilderness;

class WildernessPlayerMeta {
  private int level = -1;
  private long attacks;
  private long hits;
  private long lastReset;
  private long lastChange;

  public WildernessPlayerMeta() {
    reset();
  }

  public void attack() {
    ++attacks;
    lastChange = System.currentTimeMillis();
  }

  public void hit() {
    ++hits;
    lastChange = System.currentTimeMillis();
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
    if (level != -1) {
      lastChange = System.currentTimeMillis();
    }
  }

  public long getHits() {
    return hits;
  }

  public long getAttacks() {
    return attacks;
  }

  public long getFactors() {
    return attacks + hits;
  }

  public double getRatio() {
    return ((double) attacks) / hits;
  }

  public void reset() {
    attacks = 1;
    hits = 1;
    lastChange = lastReset = System.currentTimeMillis();
  }

  public long getLastReset() {
    return lastReset;
  }

  public long getLastChange() {
    return lastChange;
  }
}
