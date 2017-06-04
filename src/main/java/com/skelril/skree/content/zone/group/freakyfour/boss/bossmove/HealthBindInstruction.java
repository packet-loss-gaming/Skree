/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.freakyfour.boss.bossmove;

import com.skelril.nitro.entity.EntityHealthUtil;
import com.skelril.openboss.Boss;
import com.skelril.openboss.EntityDetail;
import com.skelril.openboss.Instruction;
import com.skelril.openboss.condition.BindCondition;
import org.spongepowered.api.entity.living.Living;

import java.util.Optional;

public class HealthBindInstruction<T extends Living, K extends EntityDetail> implements Instruction<BindCondition, Boss<T, K>> {

  private final double health;

  public HealthBindInstruction(double health) {
    this.health = health;
  }

  public double getHealth() {
    return health;
  }

  @Override
  public Optional<Instruction<BindCondition, Boss<T, K>>> apply(
      BindCondition bindCondition, Boss<T, K> bossDetail
  ) {
    Living targetEntity = bossDetail.getTargetEntity().get();
    EntityHealthUtil.setMaxHealth(targetEntity, getHealth(), true);
    return Optional.empty();
  }
}
