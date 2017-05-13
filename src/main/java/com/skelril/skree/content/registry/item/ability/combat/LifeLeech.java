/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.ability.combat;

import com.skelril.nitro.registry.dynamic.item.ability.SpecialAttack;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import static com.skelril.nitro.entity.EntityHealthUtil.getHealth;
import static com.skelril.nitro.entity.EntityHealthUtil.getMaxHealth;

public class LifeLeech implements SpecialAttack {
    @Override
    public void run(Living owner, Living target, DamageEntityEvent event) {
        final double ownerMax = getMaxHealth(owner);
        final double targetMax = getMaxHealth(target);

        final double ownerHP = getHealth(owner) / ownerMax;
        final double targetHP = getHealth(target) / targetMax;

        if (ownerHP > targetHP) {
            owner.offer(Keys.HEALTH, Math.min(ownerMax, ownerMax * (ownerHP + .1)));

            notify(owner, Text.of(TextColors.YELLOW, "Your weapon heals you."));
        } else {
            target.offer(Keys.HEALTH, targetMax * ownerHP);
            owner.offer(Keys.HEALTH, Math.min(ownerMax, ownerMax * targetHP * 1.1));

            notify(owner, Text.of(TextColors.YELLOW, "You leech the health of your foe."));
        }
    }
}
