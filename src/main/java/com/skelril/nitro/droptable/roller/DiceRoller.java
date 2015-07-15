/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.droptable.roller;

import com.skelril.nitro.droptable.DropTableChanceEntry;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Collection;
import java.util.List;

public interface DiceRoller {
    <T extends DropTableChanceEntry> Collection<ItemStack> pickEntry(List<T> input, int highRoll, double modifier);
}
