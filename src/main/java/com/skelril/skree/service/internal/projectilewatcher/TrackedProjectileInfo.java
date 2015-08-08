package com.skelril.skree.service.internal.projectilewatcher;

import com.google.common.base.Optional;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.projectile.source.ProjectileSource;
import org.spongepowered.api.world.Location;

public interface TrackedProjectileInfo {
    Projectile getProjectile();
    Optional<ProjectileSource> getProjectileSource();

    Location getLastLocation();
    void updateLocation();
}
