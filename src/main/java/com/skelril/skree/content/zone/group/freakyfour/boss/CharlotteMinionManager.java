/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.freakyfour.boss;

import com.skelril.nitro.entity.EntityHealthUtil;
import com.skelril.openboss.Boss;
import com.skelril.openboss.BossManager;
import com.skelril.openboss.Instruction;
import com.skelril.openboss.condition.DamageCondition;
import com.skelril.skree.content.zone.ZoneBossDetail;
import com.skelril.skree.content.zone.group.freakyfour.FreakyFourBoss;
import com.skelril.skree.content.zone.group.freakyfour.FreakyFourInstance;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.monster.CaveSpider;

import java.util.List;
import java.util.Optional;

public class CharlotteMinionManager extends BossManager<CaveSpider, ZoneBossDetail<FreakyFourInstance>> {

  public CharlotteMinionManager() {
    handleDamage();
  }

  private void handleDamage() {
    List<Instruction<DamageCondition, Boss<CaveSpider, ZoneBossDetail<FreakyFourInstance>>>> damageProcessor = getDamageProcessor();
    damageProcessor.add((condition, boss) -> {
      FreakyFourInstance inst = boss.getDetail().getZone();

      Optional<Living> optBossEnt = inst.getBoss(FreakyFourBoss.CHARLOTTE);
      if (optBossEnt.isPresent()) {
        EntityHealthUtil.heal(optBossEnt.get(), condition.getEvent().getBaseDamage());
      }

      return Optional.empty();
    });
  }
}
