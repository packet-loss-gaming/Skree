/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.global.templeoffate;

import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class TempleOfFateListener {
    private final TempleOfFateManager manager;

    public TempleOfFateListener(TempleOfFateManager manager) {
        this.manager = manager;
    }

    @Listener(order = Order.FIRST)
    public void onPlayerInteractEvent(InteractBlockEvent.Secondary event) {
        Object obj = event.getCause().root();
        if (!(obj instanceof Player)) {
            return;
        }

        Player player = (Player) obj;

        Optional<TempleOfFateInstance> optInst = manager.getApplicableZone(player);
        if (!optInst.isPresent()) return;

        TempleOfFateInstance inst = optInst.get();

        Optional<Location<World>> optLoc = event.getTargetBlock().getLocation();

        if (!optLoc.isPresent()) {
            return;
        }

        Location<World> targetBlock = optLoc.get();
        if (targetBlock.getBlockType() == BlockTypes.CHEST) {
            event.setCancelled(true);

            player.sendMessage(Text.of(TextColors.YELLOW, "You have successfully completed the Temple of Fate!"));
            inst.rewardPlayer(player);
        }
    }

}
