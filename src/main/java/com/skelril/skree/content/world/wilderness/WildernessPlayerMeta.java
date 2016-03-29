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

    public WildernessPlayerMeta() {
        reset();
    }

    public void attack() {
        ++attacks;
    }

    public void hit() {
        ++hits;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
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
        lastReset = System.currentTimeMillis();
    }

    public long getLastReset() {
        return lastReset;
    }
}
