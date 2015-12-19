/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.projectilewatcher;


import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.ProjectileWatcherService;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.projectile.LaunchProjectileEvent;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;

import java.util.*;

public class ProjectileWatcherServiceImpl implements ProjectileWatcherService, Runnable {
    private final SkreePlugin plugin;
    private final Game game;

    private Map<UUID, TrackedProjectileInfo> watched = new HashMap<>();
    private Optional<Task> task = Optional.empty();

    public ProjectileWatcherServiceImpl(SkreePlugin plugin, Game game) {
        this.plugin = plugin;
        this.game = game;
    }

    @Listener
    public void onProjectileLaunch(LaunchProjectileEvent event) {
        track(event.getTargetEntity(), event.getCause());
    }

    public boolean hasChanged(TrackedProjectileInfo info) {
        Location newLoc = info.getProjectile().getLocation();
        Location oldLoc = info.getLastLocation();
        return !newLoc.equals(oldLoc);
    }

    @Override
    public void track(Projectile projectile, Cause cause) {
        watched.put(projectile.getUniqueId(), new TrackedProjectileInfoImpl(projectile, cause));
        if (!task.isPresent()) {
            task = Optional.of(game.getScheduler().createTaskBuilder().execute(this).delayTicks(1).intervalTicks(1).submit(plugin));
        }
    }

    @Override
    public void run() {
        Iterator<TrackedProjectileInfo> it = watched.values().iterator();
        boolean updated = false;

        while (it.hasNext()) {
            TrackedProjectileInfo entry = it.next();

            if (hasChanged(entry)) {
                entry.updateLocation();
                updated = true;

                game.getEventManager().post(new ProjectileTickEvent(entry));
            } else {
                it.remove();
            }
        }
        if (!updated && task.isPresent()) {
            task.get().cancel();
            task = Optional.empty();
        }
    }
}
