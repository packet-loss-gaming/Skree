/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.catacombs;

import com.skelril.nitro.combat.PlayerCombatParser;
import com.skelril.nitro.entity.EntityHealthPrinter;
import com.skelril.nitro.probability.Probability;
import com.skelril.nitro.text.CombinedText;
import com.skelril.nitro.text.PlaceHolderText;
import com.skelril.skree.SkreePlugin;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Agent;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.SpawnEntityEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.Optional;

public class CatacombsListener {

    private CatacombsManager manager;

    public CatacombsListener(CatacombsManager manager) {
        this.manager = manager;
    }

    @Listener
    public void onEntitySpawn(SpawnEntityEvent event) {
        for (Entity entity : event.getEntities()) {
            Optional<CatacombsInstance> optInst = manager.getApplicableZone(entity);
            if (optInst.isPresent()) {
                if  (entity instanceof Agent) {
                    if (entity.get(Keys.DISPLAY_NAME).isPresent()) {
                        continue;
                    }
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }

    private final EntityHealthPrinter healthPrinter = new EntityHealthPrinter(
            CombinedText.of(
                    TextColors.DARK_AQUA,
                    "Entity Health: ",
                    new PlaceHolderText("health int"),
                    " / ",
                    new PlaceHolderText("max health int")
            ),
            CombinedText.of(TextColors.GOLD, TextStyles.BOLD, "KO!")
    );

    @Listener
    public void onPlayerCombat(DamageEntityEvent event) {
        if (!manager.getApplicableZone(event.getTargetEntity()).isPresent()) {
            return;
        }

        new PlayerCombatParser() {
            @Override
            public void processPlayerAttack(Player attacker, Living defender) {
                Task.builder().delayTicks(1).execute(
                        () -> healthPrinter.print(MessageChannel.fixed(attacker), defender)
                ).submit(SkreePlugin.inst());
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
        Optional<CatacombsInstance> optInst = manager.getApplicableZone(player);
        if (optInst.isPresent()) {
            String deathMessage;
            switch (Probability.getRandom(7)) {
                case 1:
                    deathMessage = " is now one with the catacombs";
                    break;
                case 2:
                    deathMessage = " stumbled on some bones";
                    break;
                case 3:
                    deathMessage = " joined the undead army";
                    break;
                case 4:
                    deathMessage = " is now at the mercy of the Necromancers";
                    break;
                case 5:
                    deathMessage = " joined their ancestors";
                    break;
                case 6:
                    deathMessage = " now craves human flesh";
                    break;
                default:
                    deathMessage = " didn't make it out with all their brain";
                    break;
            }

            event.setMessage(Text.of(player.getName(), deathMessage));
        }
    }
}
