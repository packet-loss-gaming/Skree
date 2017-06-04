/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.freakyfour.boss;

import com.skelril.openboss.Boss;
import com.skelril.openboss.BossManager;
import com.skelril.openboss.Instruction;
import com.skelril.openboss.condition.BindCondition;
import com.skelril.openboss.condition.UnbindCondition;
import com.skelril.skree.content.zone.ZoneBossDetail;
import com.skelril.skree.content.zone.group.catacombs.instruction.bossmove.NamedBindInstruction;
import com.skelril.skree.content.zone.group.freakyfour.FreakyFourConfig;
import com.skelril.skree.content.zone.group.freakyfour.FreakyFourInstance;
import com.skelril.skree.content.zone.group.freakyfour.boss.bossmove.FreakyFourBossDeath;
import com.skelril.skree.content.zone.group.freakyfour.boss.bossmove.HealthBindInstruction;
import org.spongepowered.api.entity.living.Living;

import java.util.List;

public class CharlotteBossManager extends BossManager<Living, ZoneBossDetail<FreakyFourInstance>> {
  private CharlotteMinionManager minionManager = new CharlotteMinionManager();
  private FreakyFourConfig config;

  public CharlotteBossManager(FreakyFourConfig config) {
    this.config = config;
    handleBinds();
    handleUnbinds();
  }

  public CharlotteMinionManager getMinionManager() {
    return minionManager;
  }

  private void handleBinds() {
    List<Instruction<BindCondition, Boss<Living, ZoneBossDetail<FreakyFourInstance>>>> bindProcessor = getBindProcessor();
    bindProcessor.add(new NamedBindInstruction<>("Charlotte"));
    bindProcessor.add(new HealthBindInstruction<>(config.charlotteHP));
  }

  private void handleUnbinds() {
    List<Instruction<UnbindCondition, Boss<Living, ZoneBossDetail<FreakyFourInstance>>>> unbindProcessor = getUnbindProcessor();
    unbindProcessor.add(new FreakyFourBossDeath());
  }
}
