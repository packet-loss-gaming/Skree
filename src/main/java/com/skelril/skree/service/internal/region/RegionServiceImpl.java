/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.region;

import com.google.common.collect.Sets;
import com.skelril.skree.service.RegionService;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class RegionServiceImpl implements RegionService {
    private Map<World, RegionManager> managerMap = new HashMap<>();
    private Map<Player, Region> selectionMap = new WeakHashMap<>();

    public void addManager(World world, RegionManager manager) {
        managerMap.put(world, manager);
    }

    @Override
    public Optional<Region> get(Location<World> location) {
        RegionManager manager = managerMap.get(location.getExtent());
        if (manager != null) {
            return Optional.ofNullable(manager.getRegion(new RegionPoint(location.getPosition())).orElse(null));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Region> getOrCreate(Location<World> location, User user) {
        RegionManager manager = managerMap.get(location.getExtent());
        if (manager != null) {
            RegionPoint point = new RegionPoint(location.getPosition());
            Optional<CachedRegion> optRegion = manager.getRegion(point);
            if (optRegion.isPresent()) {
                Region region = optRegion.get();
                if (region.getMembers().contains(user.getUniqueId())) {
                    return Optional.of(region);
                }
            } else {
                return Optional.of(manager.addRegion(new RegionDatabaseHandle(
                        UUID.randomUUID(),
                        location.getExtent().getName(),
                        point,
                        Sets.newHashSet(user.getUniqueId())
                )));
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Region> getMarkedRegion(Location<World> location) {
        RegionManager manager = managerMap.get(location.getExtent());
        if (manager != null) {
            return Optional.ofNullable(manager.getMarkedRegion(new RegionPoint(location.getPosition())).orElse(null));
        }
        return Optional.empty();
    }

    @Override
    public void rem(Location<World> location) {
        RegionManager manager = managerMap.get(location.getExtent());
        if (manager != null) {
            RegionPoint point = new RegionPoint(location.getPosition());
            Optional<CachedRegion> optRegion = manager.getMarkedRegion(point);
            if (optRegion.isPresent()) {
                manager.remRegion(optRegion.get().getHandle());
            }
        }
    }

    @Override
    public int cleanup() {
        int total = 0;
        for (RegionManager manager : managerMap.values()) {
            total += manager.cleanup();
        }
        return total;
    }

    @Override
    public void setSelectedRegion(Player player, Region region) {
        selectionMap.put(player, region);
    }

    @Override
    public Optional<Region> getSelectedRegion(Player player) {
        return Optional.ofNullable(selectionMap.get(player));
    }

    private Map<Player, Long> recentNotices = new WeakHashMap<>();

    private boolean check(Player player, Location<World> loc) {
        RegionPoint point = new RegionPoint(loc.getPosition());
        RegionManager manager = managerMap.get(loc.getExtent());

        if (manager != null) {
            Optional<CachedRegion> optRef = manager.getRegion(point);
            if (optRef.isPresent()) {
                CachedRegion ref = optRef.get();
                if (ref.isEditPrevented(player, point)) {
                    if (player.hasPermission("skree.admin.edit.regions")) {
                        long lastNotice = recentNotices.getOrDefault(player, 0L);
                        if (System.currentTimeMillis() - lastNotice > TimeUnit.SECONDS.toMillis(15)) {
                            player.sendMessage(Text.of(TextColors.RED, "Warning! You have been given an admin exemption to perform this action!"));
                            recentNotices.put(player, System.currentTimeMillis());
                        }
                        return false;
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Listener
    public void onInteract(InteractBlockEvent event) {
        Optional<Player> optPlayer = event.getCause().first(Player.class);
        if (optPlayer.isPresent()) {
            Player player = optPlayer.get();

            Optional<Location<World>> optLoc = event.getTargetBlock().getLocation();
            if (optLoc.isPresent()) {
                if (check(player, optLoc.get())) {
                    event.setCancelled(true);
                    if (event.getCause().root().equals(player)) {
                        player.sendMessage(Text.of(TextColors.RED, "You can't interact with blocks here!"));
                    }
                    return;
                }
            }
        }
    }

    @Listener
    public void onInteract(InteractEntityEvent event) {
        Optional<Player> optPlayer = event.getCause().first(Player.class);
        if (optPlayer.isPresent()) {
            Player player = optPlayer.get();

            Entity target = event.getTargetEntity();

            if (target.getType() != EntityTypes.PLAYER && check(player, target.getLocation())) {
                event.setCancelled(true);
                if (event.getCause().root().equals(player)) {
                    player.sendMessage(Text.of(TextColors.RED, "You can't interact with entities here!"));
                }
                return;
            }
        }
    }

    @Listener
    public void onBlockChange(ChangeBlockEvent event) {
        Optional<Player> optPlayer = event.getCause().first(Player.class);
        if (optPlayer.isPresent()) {
            Player player = optPlayer.get();
            for (Transaction<BlockSnapshot> block : event.getTransactions()) {
                Optional<Location<World>> optLoc = block.getOriginal().getLocation();
                if (optLoc.isPresent()) {
                    if (check(player, optLoc.get())) {
                        event.setCancelled(true);
                        if (event.getCause().root().equals(player)) {
                            player.sendMessage(Text.of(TextColors.RED, "You can't change blocks here!"));
                        }
                        return;
                    }
                }
            }
        }
    }
}
