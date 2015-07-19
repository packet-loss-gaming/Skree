package com.skelril.skree.service;

import com.google.common.base.Optional;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.entity.projectile.source.ProjectileSource;

/**
 * Created by cow_fu on 7/11/15 at 7:11 PM
 */
public interface ProjectileWatcherService {
    void track(Projectile projectile, Optional<ProjectileSource> source);
}
