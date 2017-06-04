/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.ability.combat;

import com.skelril.nitro.entity.EntityHealthUtil;
import com.skelril.nitro.particle.ParticleGenerator;
import com.skelril.nitro.registry.dynamic.item.ability.SpecialAttack;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class HealingLight implements SpecialAttack {
  @Override
  public void run(Living owner, Living target, DamageEntityEvent event) {
    EntityHealthUtil.heal(owner, 5);

    ParticleGenerator.mobSpawnerFlames(target.getLocation(), 4);

    target.damage(20, damageSource(owner));

    notify(owner, Text.of(TextColors.YELLOW, "Your weapon glows dimly."));
  }
}
