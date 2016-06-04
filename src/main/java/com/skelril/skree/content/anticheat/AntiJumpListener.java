/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.anticheat;

import com.flowpowered.math.vector.Vector3d;
import com.skelril.skree.SkreePlugin;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.filter.IsCancelled;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class AntiJumpListener {

    private static final double UPWARDS_VELOCITY = .1;
    private static final double RADIUS = 2;
    private static final double LEAP_DISTANCE = 1.2;

    @Listener(order = Order.POST)
    @IsCancelled(value = Tristate.TRUE)
    public void onBlockPlace(ChangeBlockEvent.Place event, @Root Player player) {
        final Location<World> playerLoc = player.getLocation();

        for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
            Optional<Location<World>> optLoc = transaction.getOriginal().getLocation();
            if (!optLoc.isPresent()) {
                continue;
            }

            Location<World> blockLoc = optLoc.get();

            final int blockY = blockLoc.getBlockY();

            if (Math.abs(player.getVelocity().getY()) > UPWARDS_VELOCITY && playerLoc.getY() > blockY) {
                Task.builder().execute(() -> {
                    Vector3d position = player.getLocation().getPosition();
                    if (position.getY() >= (blockY + LEAP_DISTANCE)) {

                        if (playerLoc.getPosition().distanceSquared(blockLoc.getPosition()) > Math.pow(RADIUS, 2)) {
                            return;
                        }

                        player.sendMessage(Text.of(TextColors.RED, "Hack jumping detected."));
                        player.setLocation(playerLoc.setPosition(new Vector3d(position.getX(), blockY, position.getZ())));
                    }
                }).delayTicks(4).submit(SkreePlugin.inst());
            }
        }
    }
}
