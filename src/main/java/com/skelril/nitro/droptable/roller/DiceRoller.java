/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.droptable.roller;

import com.google.common.collect.ImmutableList;
import com.skelril.nitro.droptable.DropTableEntry;

import java.util.Collection;

public interface DiceRoller {
  <T extends DropTableEntry> Collection<T> getHits(ImmutableList<T> input, double modifier);
}
