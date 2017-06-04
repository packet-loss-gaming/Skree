/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.ability.combat;

import com.skelril.nitro.registry.dynamic.item.ability.SpecialAttack;
import com.skelril.skree.SkreePlugin;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import static com.skelril.nitro.entity.EntityHealthUtil.getHealth;
import static com.skelril.nitro.entity.EntityHealthUtil.getMaxHealth;

public class SoulSmite implements SpecialAttack {
  @Override
  public void run(Living owner, Living target, DamageEntityEvent event) {
    final double targetMax = getMaxHealth(target);
    final double targetHP = getHealth(target) / targetMax;

    target.offer(Keys.HEALTH, (targetHP / 2) * targetMax);
    Task.builder().delayTicks(20 * (int) Math.min(20, targetMax / 5 + 1)).execute(() -> {
      if (!target.isRemoved()) {
        double newTargetMax = getMaxHealth(target);
        double newTargetHP = getHealth(target) / newTargetMax;
        if (newTargetHP < targetHP) {
          target.offer(Keys.HEALTH, targetHP * newTargetMax);
        }
      }
      notify(owner, Text.of(TextColors.YELLOW, "Your sword releases its grasp on its victim."));
    }).submit(SkreePlugin.inst());

    notify(owner, Text.of(TextColors.YELLOW, "Your sword steals its victims health for a short time."));
  }
}
