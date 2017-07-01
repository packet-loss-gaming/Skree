/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.ability.combat.defensive;

import com.skelril.nitro.registry.dynamic.ability.DefensiveAbility;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;

public class DamageReflection implements DefensiveAbility {
  private double modifier = .2;
  private double minimum = 1;

  private DamageSource damageSource(Living owner) {
    return EntityDamageSource.builder().entity(owner).type(DamageTypes.ATTACK).build();
  }

  @Override
  public void run(Living owner, Living attacker, DamageEntityEvent event) {
    double reflectionAmount = event.getBaseDamage() * modifier;
    event.setBaseDamage(event.getBaseDamage() - reflectionAmount);
    attacker.damage(Math.max(minimum, reflectionAmount), damageSource(owner));
  }
}
