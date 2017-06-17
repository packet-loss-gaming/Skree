/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.dropclear;

import com.skelril.nitro.entity.EntityCleanupTask;
import com.skelril.nitro.predicate.EntityTypePredicate;
import com.skelril.nitro.time.TimedRunnable;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.DropClearService;
import com.skelril.skree.service.internal.entitystats.WorldStatisticsEntityCollection;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class DropClearServiceImpl implements DropClearService {

  private int autoAmt;
  private float panicMod;

  private Map<World, TimedRunnable<EntityCleanupTask>> timers = new HashMap<>();
  private Map<World, Long> entityCount = new WeakHashMap<>();

  public DropClearServiceImpl(int autoAmt, float panicMod) {
    this.autoAmt = autoAmt;
    this.panicMod = panicMod;
  }

  @Override
  public boolean cleanup(World extent, int seconds) {
    return dropClear(extent, seconds, true);
  }

  @Override
  public boolean checkedCleanup(World world) {
    WorldStatisticsEntityCollection profile = WorldStatisticsEntityCollection.createFor(world, CHECK_PREDICATE);
    long discoveredEntityCount = profile.getEntities().size();
    entityCount.put(world, discoveredEntityCount);
    if (discoveredEntityCount >= autoAmt) {
      if (discoveredEntityCount >= autoAmt * panicMod) {
        forceCleanup(world);
      } else if (!isActiveFor(world)) {
        cleanup(world);
      }
      return true;
    }
    return false;
  }

  @Override
  public boolean isActiveFor(World extent) {
    return getActiveTask(extent) != null;
  }

  private TimedRunnable<EntityCleanupTask> getActiveTask(World world) {
    TimedRunnable<EntityCleanupTask> runnable = timers.get(world);

    // Check for old task, and overwrite if allowed
    if (runnable != null && !runnable.isComplete()) {
      return runnable;
    }
    return null;
  }

  @Override
  public void forceCleanup(World world) {
    dropClear(world, 0, true);
  }

  private static final Predicate<Entity> CHECK_PREDICATE;

  static {
    HashSet<EntityType> checkedEntities = new HashSet<>();
    checkedEntities.add(EntityTypes.ITEM);
    checkedEntities.add(EntityTypes.TIPPED_ARROW);
    checkedEntities.add(EntityTypes.SPECTRAL_ARROW);
    checkedEntities.add(EntityTypes.FIREBALL);
    checkedEntities.add(EntityTypes.SMALL_FIREBALL);
    checkedEntities.add(EntityTypes.FIREWORK);
    checkedEntities.add(EntityTypes.EXPERIENCE_ORB);
    checkedEntities.add(EntityTypes.SPLASH_POTION);

    CHECK_PREDICATE = new EntityTypePredicate(checkedEntities);
  }

  @Listener(order = Order.POST)
  public void onItemSpawn(SpawnEntityEvent event) {
    event.getEntities().stream().filter(DropClearServiceImpl.CHECK_PREDICATE).forEach((e) -> {
      World targetWorld = e.getWorld();
      entityCount.merge(targetWorld, 1L, (a, b) -> a + b);
    });

    entityCount.forEach((world, count) -> {
      if (count > autoAmt) {
        checkedCleanup(world);
      }
    });
  }

  private EntityCleanupTask pickDropClear(World world) {
    return new EntityCleanupTask(world, CHECK_PREDICATE) {
      @Override
      public void notifyCleanProgress(int times) {
        MessageChannel.TO_ALL.send(
            Text.of(
                TextColors.RED,
                "Clearing drops of " + world.getName() + " in " + times + " seconds!"
            )
        );
      }

      @Override
      public void notifyCleanBeginning() {
        MessageChannel.TO_ALL.send(
            Text.of(
                TextColors.RED,
                "Clearing drops of " + world.getName() + "!"
            )
        );
      }

      @Override
      public void notifyCleanEnding() {
        MessageChannel.TO_ALL.send(
            Text.of(
                TextColors.GREEN,
                getLastProfile().getEntities().size() + " drops cleared!"
            )
        );
      }
    };
  }

  private boolean dropClear(World world, int seconds, boolean overwrite) {
    TimedRunnable<EntityCleanupTask> runnable = getActiveTask(world);

    // Check for old task, and overwrite if allowed
    if (runnable != null) {
      if (overwrite) {
        runnable.setTimes(seconds);
        return true;
      }
      return false;
    }

    EntityCleanupTask cleanupTask = pickDropClear(world);

    // Setup new task
    runnable = new TimedRunnable<EntityCleanupTask>(cleanupTask, seconds) {
      @Override
      public void cancel(boolean withEnd) {
        super.cancel(withEnd);
        if (withEnd) {
          entityCount.put(world, 0L);

          // TODO send to the yet to be made, Entity Stats service
          // lastClear.put(world, getBaseTask().getLastProfile());
        }
        timers.remove(world);
      }
    };

    // Offset this by one to prevent the drop clear from triggering twice
    Task task = Task.builder().execute(runnable).delayTicks(1).interval(
        1,
        TimeUnit.SECONDS
    ).submit(SkreePlugin.inst());

    runnable.setTask(task);
    timers.put(world, runnable);
    return true;
  }
}
