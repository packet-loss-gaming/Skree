/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.catacombs.instruction;

import com.skelril.nitro.probability.Probability;
import com.skelril.openboss.Boss;
import com.skelril.openboss.Instruction;
import com.skelril.openboss.condition.DamageCondition;
import com.skelril.skree.content.zone.group.catacombs.CatacombsBossDetail;
import org.spongepowered.api.entity.living.monster.Zombie;
import org.spongepowered.api.event.entity.DamageEntityEvent;

import java.util.Optional;

public class WaveDamageModifier implements Instruction<DamageCondition, Boss<Zombie, CatacombsBossDetail>> {
  @Override
  public Optional<Instruction<DamageCondition, Boss<Zombie, CatacombsBossDetail>>> apply(
      DamageCondition damageCondition, Boss<Zombie, CatacombsBossDetail> zombieCatacombsBossDetailBoss
  ) {
    int wave = zombieCatacombsBossDetailBoss.getDetail().getWave();
    DamageEntityEvent event = damageCondition.getEvent();
    event.setBaseDamage(Probability.getRandom(Probability.getRandom(wave * .2)) + event.getBaseDamage());
    return Optional.empty();
  }
}