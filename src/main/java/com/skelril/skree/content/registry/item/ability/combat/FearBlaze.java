/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.ability.combat;

import com.skelril.nitro.entity.EntityHealthUtil;
import com.skelril.nitro.registry.dynamic.item.ability.SpecialAttack;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.PotionEffectData;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class FearBlaze implements SpecialAttack {
    @Override
    public void run(Living owner, Living target, DamageEntityEvent event) {
        int duration = (int) (EntityHealthUtil.getHealth(owner) * 20);

        Optional<PotionEffectData> optPotionEffectData = target.getOrCreate(PotionEffectData.class);
        if (optPotionEffectData.isPresent()) {
            PotionEffectData potionEffectData = optPotionEffectData.get();

            potionEffectData.addElement(PotionEffect.of(PotionEffectTypes.BLINDNESS, 1, duration));

            target.offer(potionEffectData);
        }

        target.offer(Keys.FIRE_TICKS, duration);

        notify(owner, Text.of(TextColors.YELLOW, "Your sword releases a deadly blaze."));
    }
}
