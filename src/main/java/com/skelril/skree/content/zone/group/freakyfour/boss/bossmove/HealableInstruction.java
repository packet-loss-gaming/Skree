/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.freakyfour.boss.bossmove;

import com.skelril.nitro.entity.EntityHealthUtil;
import com.skelril.openboss.Boss;
import com.skelril.openboss.Instruction;
import com.skelril.openboss.condition.DamagedCondition;
import com.skelril.skree.content.zone.ZoneBossDetail;
import com.skelril.skree.content.zone.group.freakyfour.FreakyFourInstance;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;

import java.util.Optional;

public class HealableInstruction implements Instruction<DamagedCondition, Boss<Living, ZoneBossDetail<FreakyFourInstance>>> {
  @Override
  public Optional<Instruction<DamagedCondition, Boss<Living, ZoneBossDetail<FreakyFourInstance>>>> apply(
      DamagedCondition damagedCondition, Boss<Living, ZoneBossDetail<FreakyFourInstance>> boss
  ) {
    Optional<DamageSource> optDmgSource = damagedCondition.getDamageSource();
    if (optDmgSource.isPresent()) {
      if (optDmgSource.get().getType() == DamageTypes.EXPLOSIVE) {
        DamageEntityEvent event = damagedCondition.getEvent();
        EntityHealthUtil.heal(boss.getTargetEntity().get(), event.getFinalDamage());
        event.setCancelled(true);
      }
    }
    return Optional.empty();
  }
}
