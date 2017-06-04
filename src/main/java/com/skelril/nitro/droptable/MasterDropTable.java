/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.droptable;

import com.skelril.nitro.droptable.roller.DiceRoller;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MasterDropTable implements DropTable {
  private final DiceRoller roller;
  private final List<DropTable> subTables;

  public MasterDropTable(DiceRoller roller, List<DropTable> subTables) {
    this.roller = roller;
    this.subTables = subTables;
  }

  @Override
  public Collection<ItemStack> getDrops(int quantity) {
    return getDrops(quantity, 1, roller);
  }

  @Override
  public Collection<ItemStack> getDrops(int quantity, double modifier) {
    return getDrops(quantity, modifier, roller);
  }

  @Override
  public Collection<ItemStack> getDrops(int quantity, DiceRoller roller) {
    return getDrops(quantity, 1, roller);
  }

  @Override
  public Collection<ItemStack> getDrops(int quantity, double modifier, DiceRoller roller) {
    List<ItemStack> itemStacks = new ArrayList<>();
    for (DropTable table : subTables) {
      itemStacks.addAll(table.getDrops(quantity, modifier, roller));
    }
    return itemStacks;
  }
}
