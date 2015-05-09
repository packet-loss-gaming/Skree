/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.zone;

import com.skelril.skree.service.api.ZoneService;
import com.skelril.skree.util.Clause;
import org.spongepowered.api.entity.player.Player;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.stream.Collectors;

public class ZoneServiceImpl implements ZoneService {
    private Map<String, ZoneManager<?>> managers = new HashMap<>();
    private Map<Player, WeakReference<? extends Zone>> previousZone = new WeakHashMap<>();

    private List<ZoneSpaceAllocator> allocator;

    public ZoneServiceImpl(ZoneSpaceAllocator allocator) {
        this(Collections.singletonList(allocator));
    }

    public ZoneServiceImpl(List<ZoneSpaceAllocator> allocator) {
        this.allocator = allocator;
    }

    private ZoneSpaceAllocator pickAllocator() {
        allocator.sort((a1, a2) -> {
            if (a1.getLoad() == a2.getLoad()) {
                return 0;
            }
            return a1.getLoad() < a2.getLoad() ? -1 : 1;
        });
        return allocator.get(0);
    }

    @Override
    public void registerManager(ZoneManager<?> manager) {
        managers.put(manager.getName(), manager);
    }

    @Override
    public Clause<Player, ZoneStatus> requestZone(String managerName, Player player) {
        ZoneManager<?> manager = managers.get(managerName);
        return manager != null ? requestZone(manager, player) : null;
    }

    @Override
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

    @Override
    public Clause<Player, ZoneStatus> requestZone(ZoneManager<?> manager, Player player) {
        Zone zone = manager.discover(pickAllocator());
        if (zone != null) {
            return addToZone(zone, player);
        }
        return new Clause<>(player, ZoneStatus.CREATION_FAILED);
    }

    @Override
    public Collection<Clause<Player, ZoneStatus>> requestZone(ZoneManager<?> manager, Collection<Player> players) {
        Zone zone = manager.discover(pickAllocator());
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

    @Override
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
}
