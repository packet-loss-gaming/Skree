/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.ability.combat.defensive;

import com.flowpowered.math.vector.Vector3d;
import com.skelril.nitro.entity.EntityHealthUtil;
import com.skelril.nitro.position.CuboidContainmentPredicate;
import com.skelril.nitro.probability.Probability;
import com.skelril.nitro.registry.dynamic.ability.DefensiveAbility;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.monster.Monster;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Collection;

public class PowerBurst implements DefensiveAbility {
  private Collection<Entity> getTargetEntities(Living owner) {
    CuboidContainmentPredicate predicate = new CuboidContainmentPredicate(owner.getLocation().getPosition(), 4, 4, 4);
    return owner.getNearbyEntities((entity) -> predicate.test(entity.getLocation().getPosition()));
  }

  @Override
  public void run(Living owner, Living attacker, DamageEntityEvent event) {
    notify(owner, Text.of(TextColors.YELLOW, "Your armor releases a burst of energy."));
    notify(owner, Text.of(TextColors.YELLOW, "You are healed by an ancient force."));

    final double attackDamage = event.getBaseDamage();
    EntityHealthUtil.heal(owner, attackDamage);

    getTargetEntities(owner).stream().filter(e -> e instanceof Living).forEach(e -> {
      if (e.equals(owner)) {
        return;
      }

      if (e.getType() == owner.getType()) {
        EntityHealthUtil.heal((Living) e, attackDamage);
        notify((Living) e, Text.of(TextColors.YELLOW, "You are healed by an ancient force."));
      } else if (!(owner instanceof Player) || e instanceof Monster) {
        e.setVelocity(new Vector3d(
            Probability.getRangedRandom(-1.5, 1.5),
            Probability.getRandom(2.5),
            Probability.getRangedRandom(-1.5, 1.5)
        ));
        e.offer(Keys.FIRE_TICKS, Probability.getRandom(20 * 60));
      }
    });
  }
}
