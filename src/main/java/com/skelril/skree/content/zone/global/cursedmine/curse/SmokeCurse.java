/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.global.cursedmine.curse;

import com.flowpowered.math.vector.Vector3d;
import com.skelril.nitro.probability.Probability;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;

import java.util.function.Consumer;

public class SmokeCurse implements Consumer<Player> {
    private static final ParticleEffect smokeEffect = ParticleEffect.builder().type(
            ParticleTypes.LARGE_SMOKE
    ).quantity(1).velocity(new Vector3d(0, .4, 0)).build();

    @Override
    public void accept(Player player) {
        for (int x = -1; x <= 1; ++x) {
            for (int z = -1; z <= 1; ++z) {
                for (int y = 0; y <=1; ++y) {
                    for (int i = 0; i < 10; ++i) {
                        player.getWorld().spawnParticles(smokeEffect, player.getLocation().getPosition().add(
                                x + Probability.getRangedRandom(0, 1.0),
                                y + Probability.getRangedRandom(0, 1.0),
                                z + Probability.getRangedRandom(0, 1.0)
                        ));
                    }
                }
            }
        }
    }
}
