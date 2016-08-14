/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.mobdensity;

import com.google.common.collect.Lists;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.animal.Animal;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.CollideEntityEvent;

import java.util.List;
import java.util.stream.Collectors;

public class MobDensityListener {
    @Listener
    public void onEntityCollide(CollideEntityEvent event) {
        PotionEffect witherEffect = PotionEffect.builder()
                .potionType(PotionEffectTypes.WITHER)
                .amplifier(0)
                .duration(20 * 3)
                .particles(true)
                .build();

        List<Entity> entities = event.getEntities().stream().filter(e -> e instanceof Animal).collect(Collectors.toList());
        if (entities.size() > 5) {
            entities.forEach(e -> {
                e.offer(Keys.POTION_EFFECTS, Lists.newArrayList(witherEffect));
            });
        }
    }
}
