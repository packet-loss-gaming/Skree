/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.ability.combat;

import com.skelril.nitro.probability.Probability;
import com.skelril.nitro.registry.dynamic.item.ability.SpecialAttack;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class Decimate implements SpecialAttack {
    @Override
    public void run(Living owner, Living target, DamageEntityEvent event) {
        double damage = Probability.getRandom(target instanceof Player ? 3 : 10) * 50;
        if (target.damage(damage, damageSource(owner))) {
            notify(owner, Text.of(TextColors.YELLOW, "Your sword tears through the flesh of its victim."));
        }
    }
}
