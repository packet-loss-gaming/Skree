/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone;

import com.skelril.nitro.combat.PlayerCombatParser;
import com.skelril.nitro.entity.EntityHealthPrinter;
import com.skelril.nitro.text.CombinedText;
import com.skelril.nitro.text.PlaceHolderText;
import com.skelril.skree.SkreePlugin;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.function.Predicate;

public class ZoneGlobalHealthPrinter extends ZoneApplicableListener {
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

    public ZoneGlobalHealthPrinter(Predicate<Location<World>> isApplicable) {
        super(isApplicable);
    }

    @Listener
    public void onPlayerCombat(DamageEntityEvent event) {
        if (!isApplicable(event.getTargetEntity())) {
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
}
