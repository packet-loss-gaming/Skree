/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.item;

import com.flowpowered.math.vector.Vector3d;
import com.skelril.nitro.probability.Probability;
import com.skelril.nitro.time.IntegratedRunnable;
import org.spongepowered.api.event.cause.entity.spawn.SpawnType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;

public class ItemFountain implements IntegratedRunnable {

  private final ItemDropper dropper;

  private final Function<Integer, Integer> amplifier;
  private final Collection<ItemStack> options;
  private final SpawnType spawnType;

  public ItemFountain(Location<World> location, Function<Integer, Integer> amplifier, Collection<ItemStack> options, SpawnType spawnType) {
    this.dropper = new ItemDropper(location);

    this.amplifier = amplifier;
    this.options = options;
    this.spawnType = spawnType;
  }

  public World getExtent() {
    return dropper.getExtent();
  }

  public Vector3d getPos() {
    return dropper.getPos();
  }

  @Override
  public boolean run(int times) {
    ItemStack stack = Probability.pickOneOf(options);
    for (int i = 0; i < amplifier.apply(i) + 1; i++) {
      dropper.dropStacks(Collections.singletonList(stack), spawnType);
    }
    return true;
  }

  @Override
  public void end() {

  }
}
