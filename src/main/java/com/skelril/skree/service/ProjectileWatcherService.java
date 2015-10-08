package com.skelril.skree.service;


import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.projectile.source.ProjectileSource;

import java.util.Optional;

public interface ProjectileWatcherService {
    void track(Projectile projectile, Optional<ProjectileSource> source);
}
