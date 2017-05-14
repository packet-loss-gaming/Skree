/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.wilderness.wanderer;

import com.skelril.nitro.probability.Probability;
import com.skelril.openboss.Boss;
import com.skelril.openboss.BossListener;
import com.skelril.openboss.BossManager;
import com.skelril.openboss.Instruction;
import com.skelril.openboss.condition.BindCondition;
import com.skelril.openboss.condition.DamageCondition;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.world.wilderness.WildernessBossDetail;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.monster.Skeleton;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;

import static com.skelril.nitro.entity.EntityHealthUtil.setMaxHealth;

public class StormBringer implements WanderingBoss<Skeleton> {
    private final BossManager<Skeleton, WildernessBossDetail> bossManager = new BossManager<>();

    public StormBringer() {
        setupStormBringer();
    }

    @Override
    public EntityType getEntityType() {
        return EntityTypes.SKELETON;
    }

    @Override
    public void bind(Skeleton entity, WildernessBossDetail detail) {
        bossManager.bind(new Boss<>(entity, detail));
    }

    private void setupStormBringer() {
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new BossListener<>(bossManager, Skeleton.class)
        );

        List<Instruction<BindCondition, Boss<Skeleton, WildernessBossDetail>>> bindProcessor = bossManager.getBindProcessor();
        bindProcessor.add((condition, boss) -> {
            Optional<Skeleton> optBossEnt = boss.getTargetEntity();
            if (optBossEnt.isPresent()) {
                Skeleton bossEnt = optBossEnt.get();
                bossEnt.offer(Keys.DISPLAY_NAME, Text.of("Storm Bringer"));
                double bossHealth = 20 * 30 * boss.getDetail().getLevel();
                setMaxHealth(bossEnt, bossHealth, true);
            }
            return Optional.empty();
        });

        List<Instruction<DamageCondition, Boss<Skeleton, WildernessBossDetail>>> damageProcessor = bossManager.getDamageProcessor();
        damageProcessor.add((condition, boss) -> {
            Entity eToHit = condition.getAttacked();
            if (!(eToHit instanceof Living)) {
                return Optional.empty();
            }

            Living toHit = (Living) eToHit;
            Location<World> targetLocation = toHit.getLocation();
            for (int i = boss.getDetail().getLevel() * Probability.getRangedRandom(1, 10); i >= 0; --i) {
                Task.builder().execute(() -> {
                    Entity lightning = targetLocation.getExtent().createEntity(EntityTypes.LIGHTNING, targetLocation.getPosition());
                    targetLocation.getExtent().spawnEntity(
                            lightning, Cause.source(SpawnCause.builder().type(SpawnTypes.PLUGIN).build()).build()
                    );
                }).delayTicks(5 * (6 + i)).submit(SkreePlugin.inst());
            }

            return Optional.empty();
        });
    }
}
