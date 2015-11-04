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

public interface TrackedProjectileInfo {
    Projectile getProjectile();
    Optional<ProjectileSource> getProjectileSource();

    Location getLastLocation();
    void updateLocation();
}
