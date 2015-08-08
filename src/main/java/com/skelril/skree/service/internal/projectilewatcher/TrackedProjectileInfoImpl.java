package com.skelril.skree.service.internal.projectilewatcher;

import com.google.common.base.Optional;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.projectile.source.ProjectileSource;
import org.spongepowered.api.world.Location;

public class TrackedProjectileInfoImpl implements TrackedProjectileInfo {
    private final Projectile projectile;
    private final Optional<ProjectileSource> source;
    private Location location;

    public TrackedProjectileInfoImpl(Projectile projectile) {
        this(projectile, Optional.absent());
    }

    public TrackedProjectileInfoImpl(Projectile projectile, ProjectileSource source) {
        this(projectile, Optional.of(source));
    }

    protected TrackedProjectileInfoImpl(Projectile projectile, Optional<ProjectileSource> source) {
        this.projectile = projectile;
        this.source = source;
        updateLocation();
    }

    @Override
    public Projectile getProjectile() {
        return projectile;
    }

    @Override
    public Optional<ProjectileSource> getProjectileSource() {
        return source;
    }

    @Override
    public Location getLastLocation() {
        return location;
    }

    @Override
    public void updateLocation() {
        location = projectile.getLocation();
    }
}
