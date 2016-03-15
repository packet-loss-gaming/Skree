/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.combat;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;

import java.util.Optional;

public interface PlayerCombatParser extends CombatParser {
    default void parse(DamageEntityEvent event) {
        Entity entity = event.getTargetEntity();
        if (!(entity instanceof Living)) {
            return;
        }

        Optional<EntityDamageSource> optDamageSource = event.getCause().first(EntityDamageSource.class);
        if (optDamageSource.isPresent()) {
            Entity srcEntity;
            if (optDamageSource.isPresent() && optDamageSource.get() instanceof IndirectEntityDamageSource) {
                srcEntity = ((IndirectEntityDamageSource) optDamageSource.get()).getIndirectSource();
            } else {
                srcEntity = optDamageSource.get().getSource();
            }

            if (!(srcEntity instanceof Living)) {
                if (entity instanceof Player) {
                    processNonLivingAttack(optDamageSource.get(), (Player) entity);
                }
                return;
            }

            Living living = (Living) srcEntity;
            if (verify(living)) {
                if (entity instanceof Player && living instanceof Player) {
                    processPvP((Player) living, (Player) entity);
                } else if (entity instanceof Player) {
                    processMonsterAttack(living, (Player) entity);
                } else if (living instanceof Player) {
                    processPlayerAttack((Player) living, (Living) entity);
                }
            }
        }
    }

    default boolean verify(Living living) {
        return true;
    }

    default void processPvP(Player attacker, Player defender) { }

    default void processMonsterAttack(Living attacker, Player defender) { }

    default void processPlayerAttack(Player attacker, Living defender) { }

    default void processNonLivingAttack(DamageSource attacker, Player defender) { }
}
