/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.shnugglesprime;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class ShnugglesPrimeListener {

    private final ShnugglesPrimeManager manager;

    public ShnugglesPrimeListener(ShnugglesPrimeManager manager) {
        this.manager = manager;
    }

    @Listener
    public void onPlayerDeath(DestructEntityEvent.Death event) {
        Entity entity = event.getTargetEntity();
        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;
        Optional<ShnugglesPrimeInstance> optInst = manager.getApplicableZone(player);
        if (optInst.isPresent()) {
            ShnugglesPrimeInstance inst = optInst.get();
            inst.healBoss(.33F);

            Optional<ShnugglesPrimeAttack> optAttack = inst.getLastAttack();
            String deathMessage = " died while attempting to slay Shnuggles Prime";
            if (optAttack.isPresent()) {
                switch (optAttack.get()) {
                    case WRATH:
                        deathMessage = " discovered how tasty the boss's wrath is";
                        break;
                    case CORRUPTION:
                        deathMessage = " embraced the boss's corruption";
                        break;
                    case BLINDNESS:
                        deathMessage = " did not die seeing";
                        break;
                    case TANGO_TIME:
                        deathMessage = " found out the boss has two left feet";
                        break;
                    case EVERLASTING:
                        deathMessage = " needs not pester invincible overlords";
                        break;
                    case FIRE:
                        deathMessage = " died to a terrible inferno";
                        break;
                    case BASK_IN_MY_GLORY:
                        deathMessage = " basked in the glory of the boss";
                        break;
                    case DARK_POTIONS:
                        deathMessage = " was the victim of dark magic";
                        break;
                    case MINION_LEECH:
                        deathMessage = " has been consumed by the boss";
                        break;
                }
            }
            
            event.setMessage(Text.of(player.getName(), deathMessage));
        }
    }
}
