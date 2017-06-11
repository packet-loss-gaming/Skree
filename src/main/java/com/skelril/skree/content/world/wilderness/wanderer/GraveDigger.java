/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.wilderness.wanderer;

import com.skelril.openboss.Boss;
import com.skelril.openboss.BossListener;
import com.skelril.openboss.BossManager;
import com.skelril.openboss.Instruction;
import com.skelril.openboss.condition.BindCondition;
import com.skelril.openboss.condition.DamageCondition;
import com.skelril.openboss.condition.DamagedCondition;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.world.wilderness.WildernessBossDetail;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.monster.Skeleton;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.cause.entity.spawn.SpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;

import static com.skelril.nitro.entity.EntityHealthUtil.setMaxHealth;

public class GraveDigger extends SkeletonArcherWanderer {
  private final BossManager<Skeleton, WildernessBossDetail> bossManager = new BossManager<>();

  public GraveDigger() {
    setupGraveDigger();
  }

  @Override
  public EntityType getEntityType() {
    return EntityTypes.SKELETON;
  }

  @Override
  public void bind(Skeleton entity, WildernessBossDetail detail) {
    bossManager.bind(new Boss<>(entity, detail));
  }

  private void setupGraveDigger() {
    Sponge.getEventManager().registerListeners(
        SkreePlugin.inst(),
        new BossListener<>(bossManager, Skeleton.class)
    );

    List<Instruction<BindCondition, Boss<Skeleton, WildernessBossDetail>>> bindProcessor = bossManager.getBindProcessor();
    bindProcessor.add((condition, boss) -> {
      Optional<Skeleton> optBossEnt = boss.getTargetEntity();
      if (optBossEnt.isPresent()) {
        Skeleton bossEnt = optBossEnt.get();
        bossEnt.offer(Keys.DISPLAY_NAME, Text.of("Grave Digger"));
        bossEnt.offer(Keys.CUSTOM_NAME_VISIBLE, true);
        double bossHealth = 20 * 43 * boss.getDetail().getLevel();
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
      makeExplosiveTomb(targetLocation, boss);

      return Optional.empty();
    });

    List<Instruction<DamagedCondition, Boss<Skeleton, WildernessBossDetail>>> damagedProcessor = bossManager.getDamagedProcessor();
    damagedProcessor.add((condition, boss) -> {
      Optional<DamageSource> optDamageSource = condition.getDamageSource();
      if (optDamageSource.isPresent()) {
        DamageSource damageSource = optDamageSource.get();

        // Explosions are show up as custom instead of Explosion
        if (damageSource.getType() == DamageTypes.CUSTOM) {
          condition.getEvent().setCancelled(true);
        }

        if (!(damageSource instanceof IndirectEntityDamageSource)) {
          return Optional.empty();
        }

        Entity source = ((IndirectEntityDamageSource) damageSource).getIndirectSource();
        Location<World> targetLocation = source.getLocation();
        makeExplosiveTomb(targetLocation, boss);
      }
      return Optional.empty();
    });
  }

  private void makeExplosiveTomb(Location<World> targetLocation, Boss<Skeleton, WildernessBossDetail> boss) {
    makeSphere(targetLocation, 3, 3, 3);
    for (int i = 0; i < boss.getDetail().getLevel(); ++i) {
      Entity explosive = targetLocation.getExtent().createEntity(EntityTypes.PRIMED_TNT, targetLocation.getPosition());
      explosive.offer(Keys.FUSE_DURATION, 20 * 4);

      targetLocation.getExtent().spawnEntity(
          explosive, Cause.source(SpawnCause.builder().type(SpawnTypes.PLUGIN).build()).build()
      );
    }
  }

  private static double lengthSq(double x, double y, double z) {
    return (x * x) + (y * y) + (z * z);
  }

  private void makeSphere(Location<World> pos, double radiusX, double radiusY, double radiusZ) {
    radiusX += 0.5;
    radiusY += 0.5;
    radiusZ += 0.5;

    final double invRadiusX = 1 / radiusX;
    final double invRadiusY = 1 / radiusY;
    final double invRadiusZ = 1 / radiusZ;

    final int ceilRadiusX = (int) Math.ceil(radiusX);
    final int ceilRadiusY = (int) Math.ceil(radiusY);
    final int ceilRadiusZ = (int) Math.ceil(radiusZ);

    double nextXn = 0;
    forX:
    for (int x = 0; x <= ceilRadiusX; ++x) {
      final double xn = nextXn;
      nextXn = (x + 1) * invRadiusX;
      double nextYn = 0;
      forY:
      for (int y = 0; y <= ceilRadiusY; ++y) {
        final double yn = nextYn;
        nextYn = (y + 1) * invRadiusY;
        double nextZn = 0;
        for (int z = 0; z <= ceilRadiusZ; ++z) {
          final double zn = nextZn;
          nextZn = (z + 1) * invRadiusZ;

          double distanceSq = lengthSq(xn, yn, zn);
          if (distanceSq > 1) {
            if (z == 0) {
              if (y == 0) {
                break forX;
              }
              break forY;
            }
            break;
          }

          if (lengthSq(nextXn, yn, zn) <= 1 && lengthSq(xn, nextYn, zn) <= 1 && lengthSq(xn, yn, nextZn) <= 1) {
            continue;
          }

          solidify(pos.add(x, y, z));
          solidify(pos.add(-x, y, z));
          solidify(pos.add(x, -y, z));
          solidify(pos.add(x, y, -z));
          solidify(pos.add(-x, -y, z));
          solidify(pos.add(x, -y, -z));
          solidify(pos.add(-x, y, -z));
          solidify(pos.add(-x, -y, -z));
        }
      }
    }
  }

  private static final BlockState STONE_BLOCK_STATE = BlockState.builder().blockType(BlockTypes.STONE).build();

  private void solidify(Location l) {
    if (l.getBlock().getType() != BlockTypes.AIR) {
      return;
    }

    l.setBlock(STONE_BLOCK_STATE, Cause.source(SkreePlugin.container()).build());
  }
}
