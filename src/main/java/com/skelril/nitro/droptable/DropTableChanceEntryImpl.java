/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.droptable;

import com.skelril.nitro.droptable.resolver.DropResolver;

public class DropTableChanceEntryImpl extends DropTableEntryImpl implements DropTableChanceEntry {
    private final int chance;

    public DropTableChanceEntryImpl(DropResolver resolver, int chance) {
        super(resolver);
        this.chance = chance;
    }

    @Override
    public int getChance() {
        return chance;
    }
}
