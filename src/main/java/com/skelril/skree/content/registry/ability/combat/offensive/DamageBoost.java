/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.ability.combat.offensive;

import com.skelril.nitro.registry.dynamic.ability.SpecialAttack;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.entity.DamageEntityEvent;

public class DamageBoost implements SpecialAttack {
  private double modifier = 1.25;

  @Override
  public void run(Living owner, Living target, DamageEntityEvent event) {
    event.setBaseDamage(event.getBaseDamage() * modifier);
  }
}
