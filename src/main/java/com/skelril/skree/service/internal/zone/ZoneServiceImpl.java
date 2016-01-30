/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.zone;

import com.skelril.nitro.Clause;
import com.skelril.skree.service.ZoneService;
import org.spongepowered.api.entity.living.player.Player;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Consumer;
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
        managers.put(manager.getSystemName(), manager);
    }

    @Override
    public Optional<Integer> getMaxGroupSize(String managerName) {
        return managers.get(ZoneService.mangleManagerName(managerName)).getMaxGroupSize();
    }

    @Override
    public void requestZone(String managerName, Player player, Consumer<Optional<Clause<Player, ZoneStatus>>> callback) {
        ZoneManager<?> manager = managers.get(ZoneService.mangleManagerName(managerName));
        if (manager != null) {
            requestZone(manager, player, callback);
        } else {
            callback.accept(Optional.empty());
        }
    }

    @Override
    public void requestZone(String managerName, Collection<Player> players, Consumer<Optional<Collection<Clause<Player, ZoneStatus>>>> callback) {
        ZoneManager<?> manager = managers.get(ZoneService.mangleManagerName(managerName));
        if (manager != null) {
            requestZone(manager, players, callback);
        } else {
            callback.accept(Optional.empty());
        }
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
    public <T extends Zone> Optional<Integer> getMaxGroupSize(ZoneManager<T> manager) {
        return manager.getMaxGroupSize();
    }

    @Override
    public <T extends Zone> void requestZone(ZoneManager<T> manager, Player player, Consumer<Optional<Clause<Player, ZoneStatus>>> callback) {
        manager.discover(pickAllocator(), optZone -> {
            if (optZone.isPresent()) {
                callback.accept(Optional.of(addToZone(optZone.get(), player)));
                return;
            }
            callback.accept(Optional.of(new Clause<>(player, ZoneStatus.CREATION_FAILED)));
        });
    }

    @Override
    public <T extends Zone> void requestZone(ZoneManager<T> manager, Collection<Player> players, Consumer<Optional<Collection<Clause<Player, ZoneStatus>>>> callback) {
        Optional<Integer> optMaxGroupSize = getMaxGroupSize(manager);
        if (optMaxGroupSize.isPresent() && optMaxGroupSize.get() < players.size()) {
            callback.accept(Optional.of(players.stream().map(player ->
                    new Clause<>(player, ZoneStatus.MAX_GROUP_SIZE_EXCEEDED)).collect(Collectors.toList()
            )));
            return;
        }

        manager.discover(pickAllocator(), optZone -> {
            if (optZone.isPresent()) {
                callback.accept(Optional.of(players.stream().map(player ->
                        addToZone(optZone.get(), player)).collect(Collectors.toList()
                )));
                return;
            }
            callback.accept(Optional.of(players.stream().map(player ->
                    new Clause<>(player, ZoneStatus.CREATION_FAILED)).collect(Collectors.toList()
            )));
        });
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

    @Override
    public Collection<Clause<Player, ZoneStatus>> rejoin(Collection<Player> players) {
        return players.stream().map(this::rejoin).collect(Collectors.toList());
    }
}
