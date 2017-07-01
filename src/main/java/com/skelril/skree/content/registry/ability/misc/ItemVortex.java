/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.ability.misc;

import com.skelril.nitro.particle.ParticleGenerator;
import com.skelril.nitro.position.CuboidContainmentPredicate;
import com.skelril.nitro.registry.dynamic.ability.PointOfContact;
import com.skelril.nitro.time.IntegratedRunnable;
import com.skelril.nitro.time.TimedRunnable;
import com.skelril.skree.SkreePlugin;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class ItemVortex implements PointOfContact {
  private Collection<Entity> getTargetEntities(Location<World> originalLocation) {
    CuboidContainmentPredicate predicate = new CuboidContainmentPredicate(originalLocation.getPosition(), 4, 4, 4);
    return originalLocation.getExtent().getEntities((entity) -> predicate.test(entity.getLocation().getPosition()));
  }

  @Override
  public void run(Living owner, Location<World> target) {
    IntegratedRunnable vortex = new IntegratedRunnable() {
      @Override
      public boolean run(int times) {
        ParticleGenerator.enderTeleport(target, 1);

        getTargetEntities(target).stream().filter(e -> e instanceof Item).forEach(e -> {
          e.setLocation(owner.getLocation());
        });

        return true;
      }

      @Override
      public void end() {

      }
    };
    TimedRunnable<IntegratedRunnable> runnable = new TimedRunnable<>(vortex, 3);

    Task task = Task.builder().execute(runnable).interval(500, TimeUnit.MILLISECONDS).submit(SkreePlugin.inst());
    runnable.setTask(task);
  }
}
