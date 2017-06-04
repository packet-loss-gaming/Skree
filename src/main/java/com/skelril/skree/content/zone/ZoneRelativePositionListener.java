/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone;

import com.flowpowered.math.vector.Vector3i;
import com.skelril.skree.service.internal.zone.Zone;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.function.Function;

public class ZoneRelativePositionListener<T extends Zone> extends ZoneApplicableListener<T> {
    public ZoneRelativePositionListener(Function<Location<World>, Optional<T>> applicabilityFunct) {
        super(applicabilityFunct);
    }


    @Listener
    public void onBlockInteract(InteractBlockEvent.Secondary event, @First Player player) {
        Optional<Location<World>> optLocation = event.getTargetBlock().getLocation();
        if (!optLocation.isPresent()) {
            return;
        }

        Location<World> location = optLocation.get();

        Optional<T> optInst = getApplicable(location);
        if (!optInst.isPresent()) {
            return;
        }

        T inst = optInst.get();
        Vector3i minPoint = inst.getRegion().getMinimumPoint();
        Vector3i clickedPoint = location.getBlockPosition();

        Vector3i offset = clickedPoint.sub(minPoint);

        player.sendMessage(Text.of("Offset: ", offset));
    }
}

