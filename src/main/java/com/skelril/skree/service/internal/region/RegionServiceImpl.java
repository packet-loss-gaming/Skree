/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.region;

import com.skelril.skree.service.RegionService;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;

public class RegionServiceImpl implements RegionService {
    private Map<World, RegionManager> managerMap = new HashMap<>();
    private Map<Player, RegionReference> selectionMap = new WeakHashMap<>();

    public void addManager(World world, RegionManager manager) {
        managerMap.put(world, manager);
    }

    @Override
    public Optional<RegionReference> get(Location<World> location) {
        RegionManager manager = managerMap.get(location.getExtent());
        if (manager != null) {
            return manager.getRegion(new RegionPoint(location.getPosition()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<RegionReference> getOrCreate(Location<World> location) {
        RegionManager manager = managerMap.get(location.getExtent());
        if (manager != null) {
            RegionPoint point = new RegionPoint(location.getPosition());
            Optional<RegionReference> optRegion = manager.getRegion(point);
            if (optRegion.isPresent()) {
                return optRegion;
            }
            return Optional.of(manager.addRegion(new Region(UUID.randomUUID(), location.getExtent().getName(), point)));
        }
        return Optional.empty();
    }

    @Override
    public Optional<RegionReference> getMarkedRegion(Location<World> location) {
        RegionManager manager = managerMap.get(location.getExtent());
        if (manager != null) {
            return manager.getMarkedRegion(new RegionPoint(location.getPosition()));
        }
        return Optional.empty();
    }

    @Override
    public void rem(Location<World> location) {
        RegionManager manager = managerMap.get(location.getExtent());
        if (manager != null) {
            RegionPoint point = new RegionPoint(location.getPosition());
            Optional<RegionReference> optRegion = manager.getRegion(point);
            if (optRegion.isPresent()) {
                manager.remRegion(optRegion.get().getReferred());
            }
        }
    }

    @Override
    public void setSelectedRegion(Player player, RegionReference region) {
        selectionMap.put(player, region);
    }

    @Override
    public Optional<RegionReference> getSelectedRegion(Player player) {
        return Optional.ofNullable(selectionMap.get(player));
    }
}
