/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service;

import com.skelril.skree.service.internal.region.RegionReference;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public interface RegionService {
    Optional<RegionReference> get(Location<World> location);
    Optional<RegionReference> getOrCreate(Location<World> location);
    Optional<RegionReference> getMarkedRegion(Location<World> location);

    void rem(Location<World> location);

    void setSelectedRegion(Player player, RegionReference region);
    Optional<RegionReference> getSelectedRegion(Player player);
}
