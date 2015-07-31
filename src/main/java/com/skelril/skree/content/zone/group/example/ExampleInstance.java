/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.example;

import com.skelril.nitro.Clause;
import com.skelril.skree.service.internal.zone.Zone;
import com.skelril.skree.service.internal.zone.ZoneRegion;
import com.skelril.skree.service.internal.zone.ZoneStatus;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.world.Location;

import java.util.Collection;

public class ExampleInstance implements Zone {

    private ZoneRegion region;

    public ExampleInstance(ZoneRegion region) {
        this.region = region;
    }

    @Override
    public boolean init() {
        return false;
    }

    @Override
    public void forceEnd() {

    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public Clause<Player, ZoneStatus> add(Player player) {
        player.setLocation(new Location(region.getExtent(), region.getMaximumPoint()));
        return new Clause<>(player, ZoneStatus.ADDED);
    }

    @Override
    public Clause<Player, ZoneStatus> remove(Player player) {
        return null;
    }

    @Override
    public Collection<Player> getPlayers() {
        return null;
    }
}
