/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.catacombs;

import com.skelril.nitro.probability.Probability;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.registry.item.CustomItemTypes;
import com.skelril.skree.service.internal.zone.PlayerClassifier;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Tristate;

import java.util.Optional;

import static com.skelril.nitro.transformer.ForgeTransformer.tf;

public class CatacombsListener {

    private CatacombsManager manager;

    public CatacombsListener(CatacombsManager manager) {
        this.manager = manager;
    }

    @Listener
    public void onRightClick(InteractBlockEvent.Secondary.MainHand event) {
        Optional<Player> optPlayer = event.getCause().first(Player.class);

        if (!optPlayer.isPresent()) return;

        Player player = optPlayer.get();

        Optional<CatacombsInstance> optInst = manager.getApplicableZone(player);
        if (!optInst.isPresent()) {
            return;
        }

        CatacombsInstance inst = optInst.get();

        if (inst.hasUsedPhantomClock()) {
            return;
        }

        Optional<ItemStack> optHeldItem = player.getItemInHand(HandTypes.MAIN_HAND);

        if (optHeldItem.isPresent()) {
            ItemStack held = optHeldItem.get();
            if (held.getItem() == CustomItemTypes.PHANTOM_CLOCK) {
                Task.builder().execute(() -> {
                    tf(player).inventory.decrStackSize(tf(player).inventory.currentItem, 1);
                    inst.setUsedPhantomClock(true);

                    inst.getPlayerMessageChannel(PlayerClassifier.SPECTATOR).send(
                            Text.of(TextColors.GOLD, "A Phantom Clock has been used to increase wave speed!")
                    );
                }).delayTicks(1).submit(SkreePlugin.inst());

                event.setUseBlockResult(Tristate.FALSE);
            }
        }
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
