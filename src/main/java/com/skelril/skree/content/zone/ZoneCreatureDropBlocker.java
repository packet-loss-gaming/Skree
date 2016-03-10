/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone;

import org.spongepowered.api.entity.living.Creature;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.function.Predicate;

public class ZoneCreatureDropBlocker extends ZoneApplicableListener {
    public ZoneCreatureDropBlocker(Predicate<Location<World>> isApplicable) {
        super(isApplicable);
    }

    @Listener
    public void onEntityDrop(DropItemEvent.Destruct event) {
        Optional<Creature> optCreature = event.getCause().first(Creature.class);
        if (optCreature.isPresent() && isApplicable(optCreature.get())) {
            event.setCancelled(true);
        }
    }
}
