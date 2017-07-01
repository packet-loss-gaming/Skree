/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.ability.combat.offensive;

import com.skelril.nitro.entity.EntityHealthUtil;
import com.skelril.nitro.registry.dynamic.ability.SpecialAttack;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class Famine implements SpecialAttack {
  @Override
  public void run(Living owner, Living target, DamageEntityEvent event) {
    if (target.get(Keys.FOOD_LEVEL).isPresent() && target.get(Keys.SATURATION).isPresent()) {
      target.offer(Keys.FOOD_LEVEL, (int) (target.get(Keys.FOOD_LEVEL).get() * .85));
      target.offer(Keys.FOOD_LEVEL, (int) (target.get(Keys.FOOD_LEVEL).get() * .85));
      target.offer(Keys.SATURATION, 0D);
    } else {
      EntityHealthUtil.setMaxHealth(target, EntityHealthUtil.getMaxHealth(target) * .9);
    }

    notify(owner, Text.of(TextColors.YELLOW, "You drain the stamina of your foe."));
  }
}