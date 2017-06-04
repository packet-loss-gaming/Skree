/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.ability.combat;

import com.skelril.nitro.entity.EntityHealthUtil;
import com.skelril.nitro.registry.dynamic.item.ability.SpecialAttack;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class Agility implements SpecialAttack {
  @Override
  public void run(Living owner, Living target, DamageEntityEvent event) {
    int duration = (int) Math.min(20 * 60 * 5, EntityHealthUtil.getHealth(owner) * 18);

    Optional<PotionEffectData> optOwnerPotionEffectData = owner.getOrCreate(PotionEffectData.class);
    if (optOwnerPotionEffectData.isPresent()) {
      PotionEffectData ownerPotionEffectData = optOwnerPotionEffectData.get();
      ownerPotionEffectData.addElement(PotionEffect.of(PotionEffectTypes.SPEED, 2, duration));
      owner.offer(ownerPotionEffectData);
    }

    Optional<PotionEffectData> optTargetPotionEffectData = target.getOrCreate(PotionEffectData.class);
    if (optTargetPotionEffectData.isPresent()) {
      PotionEffectData targetPotionEffectData = optTargetPotionEffectData.get();
      targetPotionEffectData.addElement(PotionEffect.of(PotionEffectTypes.SLOWNESS, 2, duration));
      target.offer(targetPotionEffectData);
    }

    if (optOwnerPotionEffectData.isPresent() || optTargetPotionEffectData.isPresent()) {
      notify(owner, Text.of(TextColors.YELLOW, "You gain a agile advantage over your opponent."));
    }
  }
}
