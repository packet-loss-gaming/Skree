/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.ability.combat;

import com.skelril.nitro.particle.ParticleGenerator;
import com.skelril.nitro.position.CuboidContainmentPredicate;
import com.skelril.nitro.probability.Probability;
import com.skelril.nitro.registry.dynamic.item.ability.SpecialAttack;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Collection;

public class DoomBlade implements SpecialAttack {
    private Collection<Entity> getTargetEntities(Living target) {
        CuboidContainmentPredicate predicate = new CuboidContainmentPredicate(target.getLocation().getPosition(), 3, 2, 3);
        return target.getNearbyEntities((entity) -> predicate.test(entity.getLocation().getPosition()));
    }

    private double getDamage(Entity target) {
        double damage = Probability.getRangedRandom(150, 350);
        if (target instanceof Player) {
            damage = (1.0 / 3.0) * damage;
        }
        return damage;
    }

    @Override
    public void run(Living owner, Living target, DamageEntityEvent event) {
        notify(owner, Text.of(TextColors.YELLOW, "Your weapon releases a huge burst of energy."));

        double dmgTotal = 0;
        for (Entity e : getTargetEntities(target)) {
            if (!e.isRemoved() && e instanceof Living) {
                if (e.equals(owner)) continue;

                double damage = getDamage(e);
                if (!e.damage(damage, damageSource(owner))) {
                    continue;
                }

                ParticleGenerator.mobSpawnerFlames(e.getLocation(), 20);

                dmgTotal += damage;
            }
        }

        notify(owner, Text.of(TextColors.YELLOW, "Your sword dishes out an incredible ", (int) Math.ceil(dmgTotal), " damage!"));
    }
}
