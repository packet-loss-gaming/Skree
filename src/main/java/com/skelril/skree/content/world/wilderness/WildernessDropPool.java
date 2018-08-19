/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.wilderness;

import com.flowpowered.math.vector.Vector3d;
import com.skelril.nitro.item.ItemDropper;
import com.skelril.nitro.time.IntegratedRunnable;
import com.skelril.nitro.time.TimedRunnable;
import com.skelril.skree.SkreePlugin;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.function.Supplier;

public class WildernessDropPool implements IntegratedRunnable {

  private final ItemDropper dropper;
  private Supplier<Collection<ItemStackSnapshot>> itemStackProvider;
  private final int times;

  public WildernessDropPool(Location<World> location, Supplier<Collection<ItemStackSnapshot>> itemStackProvider, int times) {
    this.dropper = new ItemDropper(location);
    this.itemStackProvider = itemStackProvider;
    this.times = times;
  }

  public World getExtent() {
    return dropper.getExtent();
  }

  public Vector3d getPos() {
    return dropper.getPos();
  }

  @Override
  public boolean run(int timesL) {
    dropper.dropStackSnapshots(itemStackProvider.get());

    float volumeLevel = Math.min(1, (((float) timesL / times) * .6F) + ((float) 1 / times));

    IntegratedRunnable music = new IntegratedRunnable() {
      @Override
      public boolean run(int musicPitchT) {
        getExtent().playSound(
            SoundTypes.BLOCK_NOTE_HARP,
            getPos(),
            volumeLevel,
            (musicPitchT / 3) + 1
        );
        return true;
      }

      @Override
      public void end() {
      }
    };

    TimedRunnable<IntegratedRunnable> runnable = new TimedRunnable<>(music, 3);
    Task task = Task.builder().execute(runnable).intervalTicks(3).submit(SkreePlugin.inst());
    runnable.setTask(task);

    return true;
  }

  @Override
  public void end() {
    getExtent().playSound(
        SoundTypes.BLOCK_NOTE_HARP,
        getPos(),
        1,
        0
    );
  }
}
