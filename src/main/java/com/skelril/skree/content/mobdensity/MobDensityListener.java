/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.mobdensity;

import com.skelril.nitro.probability.Probability;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.animal.Animal;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.CollideEntityEvent;

public class MobDensityListener {
    @Listener
    public void onEntityCollide(CollideEntityEvent event) {
        event.getEntities().forEach(e -> {
            if (e instanceof Animal && Probability.getChance(20) && event.getCause().containsType(e.getClass())) {
                e.offer(Keys.FIRE_TICKS, 20 * 20);
            }
        });
    }
}
