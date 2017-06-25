/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.ability.combat.offensive;

import com.skelril.nitro.entity.EntityHealthUtil;
import com.skelril.nitro.registry.dynamic.ability.SpecialAttack;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class EvilFocus implements SpecialAttack {
  @Override
  public void run(Living owner, Living target, DamageEntityEvent event) {
    Optional<PotionEffectData> optPotionEffectData = target.getOrCreate(PotionEffectData.class);
    if (!optPotionEffectData.isPresent()) {
      return;
    }

    PotionEffectData potionEffectData = optPotionEffectData.get();

    int duration = (int) (EntityHealthUtil.getHealth(target) * 10);
    potionEffectData.addElement(PotionEffect.of(PotionEffectTypes.SLOWNESS, 9, duration));
    if (target instanceof Player) {
      potionEffectData.addElement(PotionEffect.of(PotionEffectTypes.BLINDNESS, 0, 20 * 4));
    }

    target.offer(potionEffectData);

    target.getWorld().playSound(SoundTypes.ENTITY_GHAST_SCREAM, target.getLocation().getPosition(), 1, .02F);

    notify(owner, Text.of(TextColors.YELLOW, "Your weapon traps your foe in their own sins."));
  }
}
