/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.catacombs.instruction.bossmove;

import com.skelril.nitro.entity.EntityHealthUtil;
import com.skelril.nitro.probability.Probability;
import com.skelril.openboss.Boss;
import com.skelril.openboss.Instruction;
import com.skelril.openboss.condition.DamageCondition;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.zone.group.catacombs.CatacombsBossDetail;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.monster.Zombie;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class SoulReaper implements Instruction<DamageCondition, Boss<Zombie, CatacombsBossDetail>> {
    private final int baseActivation;

    public SoulReaper() {
        this(15);
    }

    public SoulReaper(int baseActivation) {
        this.baseActivation = baseActivation;
    }

    public boolean activate(CatacombsBossDetail detail) {
        return Probability.getChance(baseActivation - detail.getWave());
    }

    @Override
    public Optional<Instruction<DamageCondition, Boss<Zombie, CatacombsBossDetail>>> apply(
            DamageCondition damageCondition, Boss<Zombie, CatacombsBossDetail> zombieCatacombsBossDetailBoss
    ) {
        CatacombsBossDetail detail = zombieCatacombsBossDetailBoss.getDetail();
        Entity attacked = damageCondition.getAttacked();
        if (attacked instanceof Player && activate(detail)) {
            Task.builder().execute(() -> {
                Optional<Zombie> optZombie = zombieCatacombsBossDetailBoss.getTargetEntity();
                if (optZombie.isPresent()) {
                    Zombie boss = optZombie.get();
                    double stolen = EntityHealthUtil.getHealth((Living) attacked) - 1;

                    attacked.offer(Keys.HEALTH, 1D);
                    EntityHealthUtil.heal(boss, stolen);
                    ((Player) attacked).sendMessage(Text.of(TextColors.RED, "The necromancer reaps your soul."));
                }
            }).delayTicks(1).submit(SkreePlugin.inst());
        }
        return Optional.empty();
    }
}
