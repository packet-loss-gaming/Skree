/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone;

import com.skelril.nitro.combat.PlayerCombatParser;
import com.skelril.skree.service.PvPService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.CollideEntityEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.function.Function;

public class ZonePvPListener<T> extends ZoneApplicableListener<T> {
    public ZonePvPListener(Function<Location<World>, Optional<T>> applicabilityFunct) {
        super(applicabilityFunct);
    }

    private PlayerCombatParser createFor(Cancellable event) {
        return new PlayerCombatParser() {
            @Override
            public void processPvP(Player attacker, Player defender) {
                Optional<PvPService> optService = Sponge.getServiceManager().provide(PvPService.class);
                if (optService.isPresent()) {
                    PvPService service = optService.get();
                    if (service.getPvPState(attacker).allowByDefault() && service.getPvPState(defender).allowByDefault()) {
                        return;
                    }
                }

                attacker.sendMessage(Text.of(TextColors.RED, "PvP is opt-in only in this area!"));

                event.setCancelled(true);
            }
        };
    }

    @Listener
    public void onPlayerCombat(DamageEntityEvent event) {
        if (!isApplicable(event.getTargetEntity())) {
            return;
        }

        createFor(event).parse(event);
    }

    @Listener
    public void onPlayerCombat(CollideEntityEvent.Impact event) {
        Optional<Projectile> optProjectile = event.getCause().first(Projectile.class);
        if (!optProjectile.isPresent()) {
            return;
        }

        if (!isApplicable(optProjectile.get())) {
            return;
        }

        createFor(event).parse(event);
    }
}
