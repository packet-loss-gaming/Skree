/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.wilderness;

import com.skelril.nitro.probability.Probability;
import com.skelril.skree.content.world.wilderness.wanderer.WanderingBoss;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class WanderingMobManager {
    private Map<String, WanderingBoss<? extends Entity>> wanderers = new HashMap<>();

    public WanderingMobManager(Map<String, WanderingBoss<? extends Entity>> wanderers) {
        this.wanderers = wanderers;
    }

    public Collection<String> getSupportedWanderers() {
        return wanderers.keySet();
    }

    public Collection<String> getSupportedWanderersOfType(EntityType entityType) {
        return wanderers.entrySet().stream().filter((entry) -> {
            WanderingBoss<? extends Entity> wanderer = entry.getValue();
            return wanderer.getEntityType() == entityType;
        }).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    public boolean chanceBind(String wandererType, int level, Entity targetEntity) {
        WanderingBoss<? extends Entity> wanderer = wanderers.get(wandererType);
        if (wanderer == null) {
            return false;
        }

        return Probability.getChance(wanderer.getSpawnChance()) && bind(wanderer, level, targetEntity);
    }

    public boolean bind(String wandererType, int level, Entity targetEntity) {
        WanderingBoss<? extends Entity> wanderer = wanderers.get(wandererType);
        if (wanderer == null) {
            return false;
        }

        return bind(wanderer, level, targetEntity);
    }

    public boolean summon(String wandererType, int level, Location<World> location) {
        WanderingBoss<? extends Entity> wanderer = wanderers.get(wandererType);
        if (wanderer == null) {
            return false;
        }

        Entity entity = location.getExtent().createEntity(wanderer.getEntityType(), location.getPosition());
        boolean spawned = location.getExtent().spawnEntity(entity, Cause.source(SpawnCause.builder().type(SpawnTypes.PLUGIN).build()).build());
        if (!spawned) {
            return false;
        }

        return wanderer.apply(entity, new WildernessBossDetail(level));
    }

    private boolean bind(WanderingBoss<? extends Entity> wanderer, int level, Entity targetEntity) {
        return wanderer.apply(targetEntity, new WildernessBossDetail(level));
    }
}
