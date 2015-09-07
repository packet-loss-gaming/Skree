package com.skelril.skree.service.internal.projectilewatcher;

import org.spongepowered.api.Game;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.entity.TargetEntityEvent;
import org.spongepowered.api.util.event.callback.CallbackList;

// TODO Add cause support
public class ProjectileTickEvent implements TargetEntityEvent {
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
    public CallbackList getCallbacks() {
        return new CallbackList();
    }

    @Override
    public Game getGame() {
        return game;
    }

    public TrackedProjectileInfo getProjectileInfo() {
        return projectile;
    }
}
