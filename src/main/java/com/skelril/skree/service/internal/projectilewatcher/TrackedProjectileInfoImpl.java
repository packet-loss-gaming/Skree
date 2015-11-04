/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.projectilewatcher;


import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.projectile.source.ProjectileSource;
import org.spongepowered.api.world.Location;

import java.util.Optional;

public class TrackedProjectileInfoImpl implements TrackedProjectileInfo {
    private final Projectile projectile;
    private final Optional<ProjectileSource> source;
    private Location location;

    public TrackedProjectileInfoImpl(Projectile projectile) {
        this(projectile, Optional.empty());
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
