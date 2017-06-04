/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.entity;

import com.skelril.nitro.time.IntegratedRunnable;
import com.skelril.nitro.time.TimeFilter;
import com.skelril.skree.service.internal.entitystats.WorldStatisticsEntityCollection;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.function.Predicate;

public abstract class EntityCleanupTask implements IntegratedRunnable {

  private final World world;
  private final Predicate<Entity> predicate;
  private final TimeFilter filter;

  private WorldStatisticsEntityCollection profile;

  public EntityCleanupTask(World world, Predicate<Entity> predicate) {
    this(world, predicate, new TimeFilter(10, 5));
  }

  public EntityCleanupTask(World world, Predicate<Entity> predicate, TimeFilter filter) {
    this.world = world;
    this.predicate = predicate;
    this.filter = filter;
  }

  public WorldStatisticsEntityCollection getLastProfile() {
    return profile;
  }

  @Override
  public boolean run(int times) {
    if (filter.matchesFilter(times)) {
      notifyCleanProgress(times);
    }
    return true;
  }

  public abstract void notifyCleanProgress(int times);

  public abstract void notifyCleanBeginning();

  public abstract void notifyCleanEnding();

  @Override
  public void end() {
    notifyCleanBeginning();

    profile = WorldStatisticsEntityCollection.createFor(world, predicate);

    Collection<? extends Entity> entities = profile.getEntities();
    entities.stream().forEach(Entity::remove);

    notifyCleanEnding();
  }
}
