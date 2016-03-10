/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.function.Predicate;

public class ZoneInventoryProtector extends ZoneApplicableListener {
    public ZoneInventoryProtector(Predicate<Location<World>> isApplicable) {
        super(isApplicable);
    }

    @Listener
    public void onItemSpawn(DropItemEvent.Destruct event) {
        Optional<Player> optPlayer = event.getCause().get(NamedCause.SOURCE, Player.class);
        if (optPlayer.isPresent() && isApplicable(optPlayer.get())) {
            event.setCancelled(true);
        }
    }
}
