/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.projectilewatcher;

import org.spongepowered.api.Game;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.entity.TargetEntityEvent;
import org.spongepowered.api.event.impl.AbstractEvent;

// TODO Add cause support
public class ProjectileTickEvent extends AbstractEvent implements TargetEntityEvent {
    private final TrackedProjectileInfo projectile;
    private final Game game;

    public ProjectileTickEvent(TrackedProjectileInfo projectile, Game game) {
        this.projectile = projectile;
        this.game = game;
    }

    @Override
    public Entity getTargetEntity() {
        return projectile.getProjectile();
    }

    @Override
    public Game getGame() {
        return game;
    }

    public TrackedProjectileInfo getProjectileInfo() {
        return projectile;
    }
}
