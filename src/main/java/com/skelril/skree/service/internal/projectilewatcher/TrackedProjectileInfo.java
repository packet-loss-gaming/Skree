package com.skelril.skree.service.internal.projectilewatcher;


import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.projectile.source.ProjectileSource;
import org.spongepowered.api.world.Location;

import java.util.Optional;

public interface TrackedProjectileInfo {
    Projectile getProjectile();
    Optional<ProjectileSource> getProjectileSource();

    Location getLastLocation();
    void updateLocation();
}
