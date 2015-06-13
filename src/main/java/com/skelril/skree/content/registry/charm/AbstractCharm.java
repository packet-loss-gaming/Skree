/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.charm;

public abstract class AbstractCharm implements Charm {
    private final int ID;
    private final String name;
    private final int maxLevel;

    public AbstractCharm(int ID, String name, int maxLevel) {
        this.ID = ID;
        this.name = name;
        this.maxLevel = maxLevel;
    }

    @Override
    public int getID() {
        return ID;
    }

    @Override
    public String getName() {
        return "skree." + name;
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
    }
}
