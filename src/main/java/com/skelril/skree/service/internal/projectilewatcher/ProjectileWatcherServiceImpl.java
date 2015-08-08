package com.skelril.skree.service.internal.projectilewatcher;

import com.google.common.base.Optional;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.ProjectileWatcherService;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.projectile.source.ProjectileSource;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.ProjectileLaunchEvent;
import org.spongepowered.api.service.scheduler.Task;
import org.spongepowered.api.world.Location;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class ProjectileWatcherServiceImpl implements ProjectileWatcherService, Runnable {
    private final SkreePlugin plugin;
    private final Game game;

    private Map<UUID, TrackedProjectileInfo> watched = new HashMap<>();
    private Optional<Task> task = Optional.absent();

    public ProjectileWatcherServiceImpl(SkreePlugin plugin, Game game) {
        this.plugin = plugin;
        this.game = game;
    }

    @Subscribe
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        track(event.getLaunchedProjectile(), event.getSource());
    }

    public boolean hasChanged(TrackedProjectileInfo info) {
        Location newLoc = info.getProjectile().getLocation();
        Location oldLoc = info.getLastLocation();
        return !newLoc.equals(oldLoc);
    }

    @Override
    public void track(Projectile projectile, Optional<ProjectileSource> source) {
        watched.put(projectile.getUniqueId(), new TrackedProjectileInfoImpl(projectile, source));
        if (!task.isPresent()) {
            task = Optional.of(game.getScheduler().getTaskBuilder().execute(this).delay(1).interval(1).submit(plugin));
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

                game.getEventManager().post(new ProjectileTickEvent(entry, game));
            } else {
                it.remove();
            }
        }
        if (!updated && task.isPresent()) {
            task.get().cancel();
            task = Optional.absent();
        }
    }
}
