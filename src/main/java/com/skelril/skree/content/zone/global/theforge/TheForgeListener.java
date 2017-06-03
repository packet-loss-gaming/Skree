/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.global.theforge;

import com.skelril.nitro.combat.PlayerCombatParser;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.CollideEntityEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class TheForgeListener {
    private final TheForgeManager manager;

    public TheForgeListener(TheForgeManager manager) {
        this.manager = manager;
    }

    // Poor man's flight check
    private boolean isFlying(Player player) {
        BlockType blockBelow = player.getLocation().add(0, -1, 0).getBlockType();
        BlockType blockBelowBelow = player.getLocation().add(0, -2, 0).getBlockType();
        return blockBelow == BlockTypes.AIR && blockBelowBelow == BlockTypes.AIR;
    }

    @Listener
    public void onPlayerCombat(CollideEntityEvent.Impact event) {
        Optional<Projectile> optProjectile = event.getCause().first(Projectile.class);
        if (!optProjectile.isPresent()) {
            return;
        }

        Optional<TheForgeInstance> optInst = manager.getApplicableZone(optProjectile.get());
        if (!optInst.isPresent()) {
            return;
        }

        new PlayerCombatParser() {
            @Override
            public void processMonsterAttack(Living attacker, Player defender) {
                if (!(event instanceof DamageEntityEvent)) {
                    return;
                }

                DamageEntityEvent dEvent = (DamageEntityEvent) event;
                if (isFlying(defender)) {
                    dEvent.setBaseDamage(dEvent.getBaseDamage() * 3);
                }
            }
        }.parse(event);
    }

    @Listener
    public void onPlayerDeath(DestructEntityEvent.Death event) {
        Entity entity = event.getTargetEntity();
        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;
        Optional<TheForgeInstance> optInst = manager.getApplicableZone(player);
        if (optInst.isPresent()) {
            if (event.getMessage().toPlain().contains("died")) {
                event.setMessage(Text.of(player.getName(), " was incinerated at The Forge"));
            }
        }
    }
}
