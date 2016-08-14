/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.mobdensity;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.animal.Animal;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSources;
import org.spongepowered.api.event.entity.CollideEntityEvent;

import java.util.List;
import java.util.stream.Collectors;

public class MobDensityListener {
    @Listener
    public void onEntityCollide(CollideEntityEvent event) {
        List<Entity> entities = event.getEntities().stream().filter(e -> e instanceof Animal).collect(Collectors.toList());
        if (entities.size() > 5) {
            entities.forEach(e -> { e.damage(5, DamageSources.GENERIC); });
        }
    }
}
