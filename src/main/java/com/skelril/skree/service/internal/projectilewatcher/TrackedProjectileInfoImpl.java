/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.projectilewatcher;


import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.world.Location;

public class TrackedProjectileInfoImpl implements TrackedProjectileInfo {
    private final Projectile projectile;
    private final Cause cause;
    private Location location;

    public TrackedProjectileInfoImpl(Projectile projectile, Cause cause) {
        this.projectile = projectile;
        this.cause = cause;
        updateLocation();
    }

    @Override
    public Projectile getProjectile() {
        return projectile;
    }

    @Override
    public Cause getCause() {
        return cause;
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
