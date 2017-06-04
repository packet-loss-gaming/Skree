/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.projectilewatcher;

import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.TargetEntityEvent;
import org.spongepowered.api.event.impl.AbstractEvent;

public class ProjectileTickEvent extends AbstractEvent implements TargetEntityEvent {
  private final TrackedProjectileInfo projectile;

  public ProjectileTickEvent(TrackedProjectileInfo projectile) {
    this.projectile = projectile;
  }

  @Override
  public Projectile getTargetEntity() {
    return projectile.getProjectile();
  }

  public TrackedProjectileInfo getProjectileInfo() {
    return projectile;
  }

  @Override
  public Cause getCause() {
    return projectile.getCause();
  }
}
