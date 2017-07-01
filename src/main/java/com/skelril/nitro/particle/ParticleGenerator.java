/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.particle;

import com.skelril.nitro.probability.Probability;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;


public class ParticleGenerator {
  private static final ParticleEffect FLAME = ParticleEffect.builder().type(ParticleTypes.FLAME).quantity(1).build();

  public static void mobSpawnerFlames(Location<World> location, int intensity) {
    for (int i = 0; i < 20 * intensity; ++i) {
      location.getExtent().spawnParticles(FLAME, location.getPosition().add(
          Probability.getRangedRandom(-.5, .5),
          Probability.getRangedRandom(0.0, 1.0),
          Probability.getRangedRandom(-.5, .5)
      ));
    }
  }

  private static final ParticleEffect SMOKE = ParticleEffect.builder().type(ParticleTypes.SMOKE).quantity(1).build();

  public static void smoke(Location<World> location, int intensity) {
    for (int i = 0; i < 20 * intensity; ++i) {
      location.getExtent().spawnParticles(SMOKE, location.getPosition().add(
          Probability.getRangedRandom(-.5, .5),
          Probability.getRangedRandom(0.0, 1.0),
          Probability.getRangedRandom(-.5, .5)
      ));
    }
  }

  private static final ParticleEffect ENDER_TELEPORT = ParticleEffect.builder().type(ParticleTypes.ENDER_TELEPORT).quantity(1).build();

  public static void enderTeleport(Location<World> location, int intensity) {
    for (int i = 0; i < 20 * intensity; ++i) {
      location.getExtent().spawnParticles(ENDER_TELEPORT, location.getPosition().add(
          Probability.getRangedRandom(-.5, .5),
          Probability.getRangedRandom(0.0, 1.0),
          Probability.getRangedRandom(-.5, .5)
      ));
    }
  }
}
