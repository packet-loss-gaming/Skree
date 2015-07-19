package com.skelril.skree.content.arrowfishing;

import com.google.common.base.Optional;
import com.skelril.nitro.probability.Probability;
import com.skelril.skree.service.ModifierService;
import com.skelril.skree.service.internal.projectilewatcher.ProjectileTickEvent;
import com.skelril.skree.service.internal.projectilewatcher.TrackedProjectileInfo;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.projectile.Arrow;
import org.spongepowered.api.entity.projectile.source.ProjectileSource;
import org.spongepowered.api.event.Subscribe;

import static com.skelril.skree.content.modifier.Modifiers.UBER_ARROW_FISHING;

/**
 * Created by cow_fu on 7/11/15 at 7:02 PM
 */
public class Arrowfishing {
    @Subscribe
    public void onProjectileTickEvent(ProjectileTickEvent event) {

        if (!(event.getEntity() instanceof Arrow) || Probability.getChance(3)) {
            return;
        }
        Arrow arrow = (Arrow) event.getEntity();
        int dropFish = 4;

        TrackedProjectileInfo info = event.getProjectileInfo();

        if (info.getProjectileSource().isPresent()) {
            ProjectileSource source = info.getProjectileSource().get();

            if (source instanceof Living) {
                Optional<ModifierService> optService = event.getGame().getServiceManager().provide(ModifierService.class);

                if (optService.isPresent() && optService.get().isActive(UBER_ARROW_FISHING)) {
                    dropFish = Probability.getRandom(dropFish);
                } else {
                    dropFish = Probability.getRandom((int) Math.pow(dropFish, 2));
                }
            } else {
                dropFish = Probability.getRandom((int) Math.pow(dropFish, 3));
            }
        }
    }
}
