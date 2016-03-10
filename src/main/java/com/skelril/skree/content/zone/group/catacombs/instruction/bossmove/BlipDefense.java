/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.catacombs.instruction.bossmove;

import com.flowpowered.math.vector.Vector3d;
import com.skelril.nitro.probability.Probability;
import com.skelril.openboss.Boss;
import com.skelril.openboss.Instruction;
import com.skelril.openboss.condition.DamagedCondition;
import com.skelril.skree.content.zone.group.catacombs.CatacombsBossDetail;
import org.spongepowered.api.entity.living.monster.Zombie;

import java.util.Optional;

public class BlipDefense implements Instruction<DamagedCondition, Boss<Zombie, CatacombsBossDetail>> {
    public double getMultiplier() {
        return 4;
    }

    public double getYFloor() {
        return .175;
    }

    public double getYCiel() {
        return .8;
    }

    public boolean activate(CatacombsBossDetail detail) {
        return Probability.getChance(5);
    }

    @Override
    public Optional<Instruction<DamagedCondition, Boss<Zombie, CatacombsBossDetail>>> apply(
            DamagedCondition damagedCondition, Boss<Zombie, CatacombsBossDetail> zombieCatacombsBossDetailBoss
    ) {
        CatacombsBossDetail detail = zombieCatacombsBossDetailBoss.getDetail();

        if (activate(detail)) {
            Zombie boss = zombieCatacombsBossDetailBoss.getTargetEntity().get();
            Vector3d vel = boss.getRotation();
            vel = vel.mul(getMultiplier());
            vel = new Vector3d(vel.getX(), Math.min(getYCiel(), Math.max(getYFloor(), vel.getY())), vel.getZ());
            boss.setVelocity(vel);
        }

        return null;
    }
}
