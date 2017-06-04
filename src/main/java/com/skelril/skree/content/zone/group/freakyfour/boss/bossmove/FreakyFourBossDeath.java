/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.freakyfour.boss.bossmove;

import com.skelril.openboss.Boss;
import com.skelril.openboss.Instruction;
import com.skelril.openboss.condition.UnbindCondition;
import com.skelril.skree.content.zone.ZoneBossDetail;
import com.skelril.skree.content.zone.group.freakyfour.FreakyFourInstance;
import org.spongepowered.api.entity.living.Living;

import java.util.Optional;

public class FreakyFourBossDeath implements Instruction<UnbindCondition, Boss<Living, ZoneBossDetail<FreakyFourInstance>>> {
  @Override
  public Optional<Instruction<UnbindCondition, Boss<Living, ZoneBossDetail<FreakyFourInstance>>>> apply(
      UnbindCondition unbindCondition, Boss<Living, ZoneBossDetail<FreakyFourInstance>> boss
  ) {
    FreakyFourInstance inst = boss.getDetail().getZone();

    inst.bossDied(inst.getCurrentboss().get());

    return Optional.empty();
  }
}