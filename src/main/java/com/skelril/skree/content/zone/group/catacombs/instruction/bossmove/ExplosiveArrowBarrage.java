/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.catacombs.instruction.bossmove;

import com.skelril.nitro.entity.VelocityEntitySpawner;
import com.skelril.openboss.Boss;
import com.skelril.openboss.EntityDetail;
import com.skelril.openboss.Instruction;
import com.skelril.openboss.condition.DamagedCondition;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.zone.group.catacombs.CatacombsBossDetail;
import com.skelril.skree.content.zone.group.catacombs.CatacombsInstance;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.monster.Zombie;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.explosion.Explosion;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class ExplosiveArrowBarrage implements Instruction<DamagedCondition, Boss<Zombie, CatacombsBossDetail>> {

  public boolean activate(EntityDetail detail) {
    return true;
  }

  public float explosionStrength(CatacombsBossDetail detail) {
    return 4F;
  }

  public boolean allowFire(CatacombsBossDetail detail) {
    return false;
  }

  public boolean allowBlockBreak(CatacombsBossDetail detail) {
    return false;
  }

  public long getDelay(CatacombsBossDetail detail) {
    return 7;
  }

  @Override
  public Optional<Instruction<DamagedCondition, Boss<Zombie, CatacombsBossDetail>>> apply(
      DamagedCondition damagedCondition, Boss<Zombie, CatacombsBossDetail> zombieCatacombsBossDetailBoss
  ) {
    CatacombsBossDetail detail = zombieCatacombsBossDetailBoss.getDetail();
    CatacombsInstance inst = detail.getZone();
    Zombie boss = zombieCatacombsBossDetailBoss.getTargetEntity().get();

    if (activate(detail)) {
      List<Entity> arrows = VelocityEntitySpawner.sendRadial(
          EntityTypes.TIPPED_ARROW,
          boss
      );

      Task.builder().execute(() -> {
        for (Entity arrow : arrows) {
          Location<World> target = arrow.getLocation();
          target.getExtent().triggerExplosion(
              Explosion.builder()
                  .location(target)
                  .radius(explosionStrength(detail))
                  .canCauseFire(allowFire(detail))
                  .shouldBreakBlocks(allowBlockBreak(detail))
                  .shouldDamageEntities(true)
                  .build()
          );
        }
      }).delay(getDelay(detail), TimeUnit.SECONDS).submit(SkreePlugin.inst());
    }

    return Optional.empty();
  }
}
