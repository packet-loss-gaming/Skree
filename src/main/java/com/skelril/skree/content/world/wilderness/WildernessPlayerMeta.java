/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.wilderness;

class WildernessPlayerMeta {
    private int level = -1;
    private long attacks = 1;
    private long hits = 1;
    private long lastReset = System.currentTimeMillis();

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

    public long factors() {
        return attacks + hits;
    }

    public double ratio() {
        return ((double) attacks) / hits;
    }

    public void reset() {
        attacks = 0;
        hits = 0;
        lastReset = System.currentTimeMillis();
    }

    public long getLastReset() {
        return lastReset;
    }
}
