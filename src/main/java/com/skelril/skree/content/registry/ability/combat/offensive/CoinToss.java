/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.ability.combat.offensive;

import com.skelril.nitro.probability.Probability;
import com.skelril.nitro.registry.dynamic.ability.SpecialAttack;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.entity.DamageEntityEvent;

import static com.skelril.nitro.entity.EntityHealthUtil.getHealth;
import static com.skelril.nitro.entity.EntityHealthUtil.getMaxHealth;

public class CoinToss implements SpecialAttack {
  public void processAttackOnEntity(Living attacker, DamageEntityEvent event) {
    int diff = (int) (getMaxHealth(attacker) - getHealth(attacker));
    double randomRelative = Probability.getRandom(Probability.getRandom(Math.pow(diff, 2)));

    event.setBaseDamage(Math.max(5, randomRelative));

    // Damage the user by 1 damage
    attacker.offer(Keys.HEALTH, getHealth(attacker) - 1);
  }

  public void processAttackOnPlayer(Living attacker, Player defender, DamageEntityEvent event) {
    event.setBaseDamage(0);

    Living target = defender;
    if (Probability.getChance(2)) {
      target = attacker;
    }

    double targetHealth = getHealth(target);
    target.offer(Keys.HEALTH, Math.max(0, targetHealth - 16));
  }

  @Override
  public void run(Living owner, Living target, DamageEntityEvent event) {
    if (target instanceof Player) {
      processAttackOnPlayer(owner, (Player) target, event);
    } else {
      processAttackOnEntity(owner, event);
    }
  }
}
