package com.skelril.skree.service.internal.projectilewatcher;

import com.google.common.base.Optional;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.projectile.source.ProjectileSource;
import org.spongepowered.api.world.Location;

/**
 * Created by cow_fu on 7/11/15 at 7:30 PM
 */
public interface TrackedProjectileInfo {
    Projectile getProjectile();
    Optional<ProjectileSource> getProjectileSource();

    Location getLastLocation();
    void updateLocation();
}
