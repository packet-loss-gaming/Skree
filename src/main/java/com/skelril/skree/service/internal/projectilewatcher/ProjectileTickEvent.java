package com.skelril.skree.service.internal.projectilewatcher;

import org.spongepowered.api.Game;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.event.AbstractEvent;
import org.spongepowered.api.event.entity.EntityEvent;

public class ProjectileTickEvent extends AbstractEvent implements EntityEvent {
    private final TrackedProjectileInfo projectile;
    private final Game game;

    public ProjectileTickEvent(TrackedProjectileInfo projectile, Game game) {
        this.projectile = projectile;
        this.game = game;
    }

    @Override
    public Projectile getEntity() {
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
