/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.catacombs.instruction.bossmove;

import com.skelril.nitro.combat.PlayerCombatParser;
import com.skelril.nitro.probability.Probability;
import com.skelril.openboss.Boss;
import com.skelril.openboss.Instruction;
import com.skelril.openboss.condition.DamagedCondition;
import com.skelril.skree.content.zone.group.catacombs.CatacombsBossDetail;
import com.skelril.skree.content.zone.group.catacombs.CatacombsInstance;
import com.skelril.skree.service.internal.zone.PlayerClassifier;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.monster.Zombie;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class DeathMark implements Instruction<DamagedCondition, Boss<Zombie, CatacombsBossDetail>> {
    private final int baseActivation;

    public DeathMark() {
        this(30);
    }

    public DeathMark(int baseActivation) {
        this.baseActivation = baseActivation;
    }

    public boolean activate(CatacombsBossDetail detail) {
        return Probability.getChance(baseActivation - detail.getWave());
    }

    @Override
    public Optional<Instruction<DamagedCondition, Boss<Zombie, CatacombsBossDetail>>> apply(
            DamagedCondition damagedCondition, Boss<Zombie, CatacombsBossDetail> zombieCatacombsBossDetailBoss
    ) {
        new PlayerCombatParser() {
            @Override
            public void processPlayerAttack(Player attacker, Living defender) {
                CatacombsBossDetail detail = zombieCatacombsBossDetailBoss.getDetail();
                CatacombsInstance inst = detail.getZone();
                if (detail.getMarked().isPresent()) {
                    if (attacker.equals(detail.getMarked().get())) {
                        inst.getPlayerMessageChannel(PlayerClassifier.SPECTATOR).send(
                                Text.of(TextColors.YELLOW, attacker.getName() + " has been freed!")
                        );
                    } else {
                        detail.getMarked().get().offer(Keys.HEALTH, 0D);
                    }
                    detail.setMarked(null);
                } else if (activate(detail)) {
                    detail.setMarked(attacker);
                    inst.getPlayerMessageChannel(PlayerClassifier.SPECTATOR).send(
                            Text.of(TextColors.DARK_RED, attacker.getName() + " has been marked!")
                    );
                }
            }
        }.parse(damagedCondition.getEvent());
        return Optional.empty();
    }
}