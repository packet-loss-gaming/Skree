/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.catacombs.instruction.bossmove;

import com.skelril.nitro.probability.Probability;
import com.skelril.openboss.Boss;
import com.skelril.openboss.Instruction;
import com.skelril.openboss.condition.DamagedCondition;
import com.skelril.skree.content.zone.group.catacombs.CatacombsBossDetail;
import com.skelril.skree.content.zone.group.catacombs.CatacombsInstance;
import org.spongepowered.api.entity.living.monster.Zombie;

import java.util.Optional;

public class UndeadMinionRetaliation implements Instruction<DamagedCondition, Boss<Zombie, CatacombsBossDetail>> {
  private final int baseActivation;

  public UndeadMinionRetaliation() {
    this(25);
  }

  public UndeadMinionRetaliation(int baseActivation) {
    this.baseActivation = baseActivation;
  }

  public boolean activate(CatacombsBossDetail detail) {
    return Probability.getChance(baseActivation - detail.getWave());
  }

  public int quantity(CatacombsBossDetail detail) {
    return activate(detail) ? Probability.getRandom((int) (detail.getWave() * 1.5)) : 0;
  }

  @Override
  public Optional<Instruction<DamagedCondition, Boss<Zombie, CatacombsBossDetail>>> apply(
      DamagedCondition damagedCondition, Boss<Zombie, CatacombsBossDetail> zombieCatacombsBossDetailBoss
  ) {
    Zombie boss = zombieCatacombsBossDetailBoss.getTargetEntity().get();
    CatacombsBossDetail detail = zombieCatacombsBossDetailBoss.getDetail();
    CatacombsInstance inst = detail.getZone();
    for (int i = quantity(detail); i > 0; --i) {
      inst.spawnWaveMob(boss.getLocation());
    }
    return Optional.empty();
  }
}
