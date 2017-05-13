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

public class Confuse implements SpecialAttack {
    @Override
    public void run(Living owner, Living target, DamageEntityEvent event) {
        Optional<PotionEffectData> optPotionEffectData = target.getOrCreate(PotionEffectData.class);
        if (!optPotionEffectData.isPresent()) {
            return;
        }

        PotionEffectData potionEffectData = optPotionEffectData.get();

        int duration = (int) Math.min(1200, EntityHealthUtil.getHealth(owner) * 18);
        potionEffectData.addElement(PotionEffect.of(PotionEffectTypes.NAUSEA, 1, duration));

        target.offer(potionEffectData);

        notify(owner, Text.of(TextColors.YELLOW, "Your sword confuses its victim."));

    }
}
