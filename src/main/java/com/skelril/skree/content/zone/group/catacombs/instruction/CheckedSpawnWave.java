/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.catacombs.instruction;

import com.skelril.openboss.Boss;
import com.skelril.openboss.Instruction;
import com.skelril.openboss.condition.UnbindCondition;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.zone.group.catacombs.CatacombsBossDetail;
import org.spongepowered.api.entity.living.monster.Zombie;
import org.spongepowered.api.scheduler.Task;

import java.util.Optional;

public class CheckedSpawnWave implements Instruction<UnbindCondition, Boss<Zombie, CatacombsBossDetail>> {
    @Override
    public Optional<Instruction<UnbindCondition, Boss<Zombie, CatacombsBossDetail>>> apply(
            UnbindCondition unbindCondition, Boss<Zombie, CatacombsBossDetail> zombieCatacombsBossDetailBoss
    ) {
        Task.builder().execute(() -> {
            zombieCatacombsBossDetailBoss.getDetail().getZone().checkedSpawnWave();
        }).delayTicks(1).submit(SkreePlugin.inst());
        return Optional.empty();
    }
}
