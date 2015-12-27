/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.shnugglesprime;

import net.minecraft.entity.monster.EntityZombie;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.monster.Giant;
import org.spongepowered.api.entity.living.monster.Zombie;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.text.Texts;

import java.util.Optional;

public class ShnugglesPrimeListener {

    private final ShnugglesPrimeManager manager;

    public ShnugglesPrimeListener(ShnugglesPrimeManager manager) {
        this.manager = manager;
    }

    @Listener
    public void onEntitySpawn(SpawnEntityEvent event) {
        event.getEntities().removeAll(event.filterEntities(e -> {
            Optional<ShnugglesPrimeInstance> optInst = manager.getApplicableZone(e);
            if (optInst.isPresent()) {
                if  (e instanceof Giant) {
                    return true;
                }
                return e instanceof Zombie && ((EntityZombie) e).isChild();
            }
            return true;
        }));
    }

    @Listener
    public void onBlockBreak(ChangeBlockEvent event) {
        Optional<Player> player = event.getCause().first(Player.class);
        if (player.isPresent() && manager.getApplicableZone(player.get()).isPresent()) {
            event.setCancelled(true);
        }
    }

    @Listener
    public void onEntityDrop(DropItemEvent.Destruct event) {
        Optional<Zombie> zombie = event.getCause().first(Zombie.class);
        if (zombie.isPresent() && manager.getApplicableZone(zombie.get()).isPresent()) {
            event.setCancelled(true);
        }
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
            // TODO inventory protection & removal of drops here
            String deathMessage;
            switch (inst.getLastAttack().orElseGet(null)) {
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
                case DOOM:
                    deathMessage = " was the victim of a devastating prayer";
                    break;
                case MINION_LEECH:
                    deathMessage = " has been consumed by the boss";
                    break;
                default:
                    deathMessage = " died while attempting to slay the boss";
                    break;
            }
            event.setMessage(Texts.of(player.getName() + deathMessage));
        }
    }
}
