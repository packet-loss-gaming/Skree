package com.skelril.skree.service;

import com.google.common.base.Optional;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.projectile.source.ProjectileSource;

public interface ProjectileWatcherService {
    void track(Projectile projectile, Optional<ProjectileSource> source);
}
