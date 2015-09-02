package com.skelril.skree.service.internal.projectilewatcher;

import org.spongepowered.api.Game;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.event.AbstractEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.entity.EntityEvent;
import org.spongepowered.api.world.World;

// TODO Add cause support
public class ProjectileTickEvent extends AbstractEvent implements EntityEvent {
    private final TrackedProjectileInfo projectile;
    private final Transform<World> transform;
    private final Game game;

    public ProjectileTickEvent(TrackedProjectileInfo projectile, Game game) {
        this.projectile = projectile;
        this.transform = projectile.getProjectile().getTransform();
        this.game = game;
    }

    @Override
    public Entity getSourceEntity() {
        return projectile.getProjectile();
    }

    @Override
    public Transform<World> getSourceTransform() {
        return transform;
    }

    @Override
    public Cause getCause() {
        return null;
    }

    @Override
    public Game getGame() {
        return game;
    }

    public TrackedProjectileInfo getProjectileInfo() {
        return projectile;
    }
}
