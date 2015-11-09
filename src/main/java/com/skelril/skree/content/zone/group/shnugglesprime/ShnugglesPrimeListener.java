/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.shnugglesprime;

import com.skelril.nitro.probability.Probability;
import net.minecraft.entity.monster.EntityZombie;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.monster.Zombie;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.HarvestEntityEvent;
import org.spongepowered.api.text.Texts;

import java.util.Optional;

public class ShnugglesPrimeListener {

    private final ShnugglesPrimeManager manager;

    public ShnugglesPrimeListener(ShnugglesPrimeManager manager) {
        this.manager = manager;
    }

    @Listener
    public void onEntityDeath(HarvestEntityEvent event) {
        Entity entity = event.getTargetEntity();
        Optional<ShnugglesPrimeInstance> inst = manager.getApplicableZone(entity);

        if (!inst.isPresent()) {
            return;
        }

        // TODO clear drops
        // TODO convert to Sponge
        if (entity instanceof Zombie && ((EntityZombie) entity).isChild()) {
            if (Probability.getChance(28)) {
                // TODO add to drops
                // .add(newItemStack(ItemTypes.GOLD_NUGGET, Probability.getRandom(3)));
            }
            event.setExperience(0);
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
            switch (inst.getLastAttack()) {
                case 1:
                    deathMessage = " discovered how tasty the boss's wrath is";
                    break;
                case 2:
                    deathMessage = " embraced the boss's corruption";
                    break;
                case 3:
                    deathMessage = " did not die seeing";
                    break;
                case 4:
                    deathMessage = " found out the boss has two left feet";
                    break;
                case 5:
                    deathMessage = " needs not pester invincible overlords";
                    break;
                case 6:
                    deathMessage = " died to a terrible inferno";
                    break;
                case 7:
                    deathMessage = " basked in the glory of the boss";
                    break;
                case 8:
                    deathMessage = " was the victim of a devastating prayer";
                    break;
                case 9:
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
