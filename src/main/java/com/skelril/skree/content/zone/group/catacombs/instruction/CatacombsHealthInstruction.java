/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.catacombs.instruction;

import com.skelril.nitro.entity.EntityHealthUtil;
import com.skelril.openboss.Boss;
import com.skelril.openboss.Instruction;
import com.skelril.openboss.condition.BindCondition;
import com.skelril.skree.content.zone.group.catacombs.CatacombsBossDetail;
import org.spongepowered.api.entity.living.monster.Zombie;

import java.util.Optional;

public class CatacombsHealthInstruction implements Instruction<BindCondition, Boss<Zombie, CatacombsBossDetail>> {

  private final int baseHP;

  public CatacombsHealthInstruction(int baseHP) {
    this.baseHP = baseHP;
  }

  @Override
  public Optional<Instruction<BindCondition, Boss<Zombie, CatacombsBossDetail>>> apply(
      BindCondition bindCondition, Boss<Zombie, CatacombsBossDetail> zombieCatacombsBossDetailBoss
  ) {
    Zombie targetEntity = zombieCatacombsBossDetailBoss.getTargetEntity().get();
    int wave = zombieCatacombsBossDetailBoss.getDetail().getWave();

    EntityHealthUtil.setMaxHealth(targetEntity, wave * baseHP, true);
    return Optional.empty();
  }
}
