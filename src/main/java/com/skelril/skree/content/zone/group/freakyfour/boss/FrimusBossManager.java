/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.freakyfour.boss;

import com.skelril.nitro.combat.PlayerCombatParser;
import com.skelril.nitro.entity.EntityHealthUtil;
import com.skelril.nitro.probability.Probability;
import com.skelril.openboss.Boss;
import com.skelril.openboss.BossManager;
import com.skelril.openboss.Instruction;
import com.skelril.openboss.condition.BindCondition;
import com.skelril.openboss.condition.DamageCondition;
import com.skelril.openboss.condition.DamagedCondition;
import com.skelril.openboss.condition.UnbindCondition;
import com.skelril.skree.content.zone.ZoneBossDetail;
import com.skelril.skree.content.zone.group.catacombs.instruction.bossmove.NamedBindInstruction;
import com.skelril.skree.content.zone.group.freakyfour.FreakyFourConfig;
import com.skelril.skree.content.zone.group.freakyfour.FreakyFourInstance;
import com.skelril.skree.content.zone.group.freakyfour.boss.bossmove.FreakyFourBossDeath;
import com.skelril.skree.content.zone.group.freakyfour.boss.bossmove.HealthBindInstruction;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;
import java.util.Optional;

public class FrimusBossManager extends BossManager<Living, ZoneBossDetail<FreakyFourInstance>> {

    private FreakyFourConfig config;

    public FrimusBossManager(FreakyFourConfig config) {
        this.config = config;
        handleBinds();
        handleUnbinds();
        handleDamage();
        handleDamaged();
    }

    private void handleBinds() {

        List<Instruction<BindCondition, Boss<Living, ZoneBossDetail<FreakyFourInstance>>>> bindProcessor = getBindProcessor();
        bindProcessor.add(new NamedBindInstruction<>("Frimus"));
        bindProcessor.add(new HealthBindInstruction<>(config.frimusHP));
    }

    private void handleUnbinds() {
        List<Instruction<UnbindCondition, Boss<Living, ZoneBossDetail<FreakyFourInstance>>>> unbindProcessor = getUnbindProcessor();
        unbindProcessor.add(new FreakyFourBossDeath());
    }

    private void handleDamage() {
        List<Instruction<DamageCondition, Boss<Living, ZoneBossDetail<FreakyFourInstance>>>> damageProcessor = getDamageProcessor();
        damageProcessor.add((condition, boss) -> {
            Entity attacked = condition.getAttacked();
            if (attacked instanceof Living) {
                EntityHealthUtil.forceDamage(
                        (Living) attacked,
                        Math.max(
                                1,
                                Probability.getRandom(EntityHealthUtil.getHealth((Living) attacked)) - 5
                        )
                );
                condition.getEvent().setBaseDamage(0);
            }
            return Optional.empty();
        });
    }

    private void handleDamaged() {
        List<Instruction<DamagedCondition, Boss<Living, ZoneBossDetail<FreakyFourInstance>>>> damagedProcessor = getDamagedProcessor();
        damagedProcessor.add((condition, boss) -> {
            DamageEntityEvent event = condition.getEvent();

            new PlayerCombatParser() {
                @Override
                public void processPlayerAttack(Player attacker, Living defender) {
                    if (condition.getDamageSource().get() instanceof IndirectEntityDamageSource) {
                        attacker.sendMessage(Text.of(TextColors.RED, "Projectiles can't harm me... Mwahahaha!"));
                        event.setCancelled(true);
                    }
                }
            }.parse(event);

            return Optional.empty();
        });
    }
}