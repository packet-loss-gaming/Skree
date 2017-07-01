/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.ability.combat.offensive;

import com.flowpowered.math.vector.Vector3d;
import com.skelril.nitro.position.CuboidContainmentPredicate;
import com.skelril.nitro.probability.Probability;
import com.skelril.nitro.registry.dynamic.ability.SpecialAttack;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Collection;

public class FearStrike implements SpecialAttack {
  private Collection<Entity> getTargetEntities(Living target) {
    CuboidContainmentPredicate predicate = new CuboidContainmentPredicate(target.getLocation().getPosition(), 4, 2, 4);
    return target.getNearbyEntities((entity) -> predicate.test(entity.getLocation().getPosition()));
  }

  @Override
  public void run(Living owner, Living target, DamageEntityEvent event) {
    for (Entity e : getTargetEntities(target)) {
      if (!e.isRemoved() && e instanceof Living) {
        if (e.equals(owner)) {
          continue;
        }

        if (!e.damage(10, damageSource(owner))) {
          continue;
        }

        Vector3d velocity = owner.getHeadRotation().mul(2);
        velocity = new Vector3d(velocity.getX(), Math.max(velocity.getY(), Math.random() * 2 + 1.27), velocity.getZ());
        e.setVelocity(velocity);
        e.offer(Keys.FIRE_TICKS, 20 * (Probability.getRandom(40) + 20));
      }
    }

    notify(owner, Text.of(TextColors.YELLOW, "You fire a terrifyingly powerful shot."));
  }
}
