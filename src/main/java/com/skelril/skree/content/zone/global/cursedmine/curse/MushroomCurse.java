/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.global.cursedmine.curse;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectTypes;
import org.spongepowered.api.entity.living.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MushroomCurse implements Consumer<Player> {
    private static final PotionEffect effect = PotionEffect.of(PotionEffectTypes.NAUSEA, 1, 20);

    @Override
    public void accept(Player player) {
        List<PotionEffect> potionEffects = player.getOrElse(Keys.POTION_EFFECTS, new ArrayList<>(1));
        potionEffects.add(effect);
        player.offer(Keys.POTION_EFFECTS, potionEffects);
    }
}
