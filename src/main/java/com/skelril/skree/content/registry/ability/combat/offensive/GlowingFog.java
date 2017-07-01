/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.ability.combat.offensive;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.skelril.nitro.particle.ParticleGenerator;
import com.skelril.nitro.position.CuboidContainmentPredicate;
import com.skelril.nitro.probability.Probability;
import com.skelril.nitro.registry.dynamic.ability.SpecialAttack;
import com.skelril.nitro.time.IntegratedRunnable;
import com.skelril.nitro.time.TimedRunnable;
import com.skelril.skree.SkreePlugin;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class GlowingFog implements SpecialAttack {
  private Collection<Entity> getTargetEntities(Location<World> originalLocation) {
    CuboidContainmentPredicate predicate = new CuboidContainmentPredicate(originalLocation.getPosition(), 4, 4, 4);
    return originalLocation.getExtent().getEntities((entity) -> predicate.test(entity.getLocation().getPosition()));
  }

  @Override
  public void run(Living owner, Living target, DamageEntityEvent event) {
    Location<World> originalLocation = target.getLocation();

    IntegratedRunnable bomb = new IntegratedRunnable() {
      @Override
      public boolean run(int times) {
        Vector3d max = originalLocation.getPosition().add(1, 1, 1);
        Vector3d min = originalLocation.getPosition().sub(1, 0, 1);

        for (int x = min.getFloorX(); x <= max.getFloorX(); ++x) {
          for (int z = min.getFloorZ(); z <= max.getFloorZ(); ++z) {
            for (int y = min.getFloorY(); y < max.getFloorY(); ++y) {
              Location<World> loc = target.getLocation().setBlockPosition(new Vector3i(x, y, z));

              ParticleGenerator.mobSpawnerFlames(loc, 1);
            }
          }
        }

        getTargetEntities(originalLocation).stream().filter(e -> e instanceof Living).forEach((e -> {
          if (!e.isRemoved() && e instanceof Living) {
            if (e.equals(owner)) {
              return;
            }

            e.damage(5, damageSource(owner));
          }
        }));

        return true;
      }

      @Override
      public void end() {

      }
    };

    TimedRunnable<IntegratedRunnable> timedRunnable = new TimedRunnable<>(bomb, (Probability.getRandom(15) * 3) + 7);

    Task task = Task.builder().execute(timedRunnable).interval(500, TimeUnit.MILLISECONDS).submit(SkreePlugin.inst());
    timedRunnable.setTask(task);

    notify(owner, Text.of(TextColors.YELLOW, "Your bow unleashes a powerful glowing fog."));
  }
}
