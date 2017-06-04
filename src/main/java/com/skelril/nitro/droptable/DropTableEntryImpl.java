/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.droptable;

import com.skelril.nitro.droptable.resolver.DropResolver;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Collection;

public class DropTableEntryImpl implements DropTableEntry {
  private final DropResolver resolver;
  private final int chance;

  public DropTableEntryImpl(DropResolver resolver) {
    this(resolver, 0);
  }

  public DropTableEntryImpl(DropResolver resolver, int chance) {
    this.resolver = resolver;
    this.chance = chance;
  }

  @Override
  public void enque(double modifier) {
    resolver.enqueue(modifier);
  }

  @Override
  public Collection<ItemStack> flush() {
    return resolver.flush();
  }

  @Override
  public int getChance() {
    return chance;
  }
}
