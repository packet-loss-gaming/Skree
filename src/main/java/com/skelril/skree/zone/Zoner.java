/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.zone;

import com.skelril.skree.util.Clause;
import org.spongepowered.api.entity.player.Player;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

public class Zoner {
    private Map<String, ZoneManager<?>> managers = new HashMap<>();
    private Map<Player, WeakReference<? extends Zone>> previousZone = new WeakHashMap<>();

    private ZoneSpaceAllocator allocator;

    public <T extends Zone> void registerManager(ZoneManager<T> manager) {
        managers.put(manager.getName(), manager);
    }

    public Clause<Player, ZoneStatus> requestZone(String managerName, Player player) {
        ZoneManager<?> manager = managers.get(managerName);
        return manager != null ? requestZone(manager, player) : null;
    }

    public Collection<Clause<Player, ZoneStatus>> requestZone(String managerName, Collection<Player> players) {
        ZoneManager<?> manager = managers.get(managerName);
        return manager != null ? requestZone(manager, players) : null;
    }

    private Clause<Player, ZoneStatus> addToZone(Zone zone, Player player) {
        // Add player
        Clause<Player, ZoneStatus> result = zone.add(player);

        // Create a weak reference so that we don't have to handle
        // cleaning up zones
        if (result.getValue() == ZoneStatus.ADDED) {
            previousZone.put(player, new WeakReference<>(zone));
        }
        return result;
    }

    public <T extends Zone> Clause<Player, ZoneStatus> requestZone(ZoneManager<T> manager, Player player) {
        T zone = manager.discover(pickAllocator());
        if (zone != null) {
            return addToZone(zone, player);
        }
        return new Clause<>(player, ZoneStatus.CREATION_FAILED);
    }

    public <T extends Zone> Collection<Clause<Player, ZoneStatus>> requestZone(ZoneManager<T> manager, Collection<Player> players) {
        T zone = manager.discover(pickAllocator());
        if (zone != null) {
            return players.stream().map(player -> addToZone(zone, player)).collect(Collectors.toList());
        }
        return players.stream().map(player -> new Clause<>(player, ZoneStatus.CREATION_FAILED)).collect(Collectors.toList());
    }

    private Clause<Zone, ZoneStatus> getPreviousZone(Player player) {
        WeakReference<? extends Zone> zoneRef = previousZone.get(player);
        if (zoneRef == null) {
            return new Clause<>(null, ZoneStatus.REF_LOST);
        }

        Zone zone = zoneRef.get();
        if (zone == null || !zone.isActive()) {
            return new Clause<>(null, ZoneStatus.DESPAWNED);
        }

        return new Clause<>(zone, ZoneStatus.EXIST_AND_ACTIVE);
    }

    public Clause<Player, ZoneStatus> rejoin(Player player) {
        Clause<Zone, ZoneStatus> previous = getPreviousZone(player);
        if (previous.getValue() == ZoneStatus.EXIST_AND_ACTIVE) {
            return previous.getKey().add(player);
        }
        return new Clause<>(player, previous.getValue());
    }

    public Collection<Clause<Player, ZoneStatus>> rejoin(Collection<Player> players) {
        return players.stream().map(this::rejoin).collect(Collectors.toList());
    }

    private ZoneSpaceAllocator pickAllocator() {
        return allocator;
    }
}
