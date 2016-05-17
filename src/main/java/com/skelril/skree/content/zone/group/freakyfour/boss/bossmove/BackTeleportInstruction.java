/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.freakyfour.boss.bossmove;

import com.flowpowered.math.vector.Vector3d;
import com.skelril.nitro.combat.PlayerCombatParser;
import com.skelril.nitro.entity.EntityDirectionUtil;
import com.skelril.nitro.numeric.MathExt;
import com.skelril.nitro.probability.Probability;
import com.skelril.openboss.Boss;
import com.skelril.openboss.Instruction;
import com.skelril.openboss.condition.DamagedCondition;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.zone.ZoneBossDetail;
import com.skelril.skree.content.zone.group.freakyfour.FreakyFourConfig;
import com.skelril.skree.content.zone.group.freakyfour.FreakyFourInstance;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.monster.Skeleton;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.scheduler.Task;

import java.util.Optional;

public class BackTeleportInstruction implements Instruction<DamagedCondition, Boss<Living, ZoneBossDetail<FreakyFourInstance>>> {

    private FreakyFourConfig config;

    public BackTeleportInstruction(FreakyFourConfig config) {
        this.config = config;
    }

    private void throwBack(Living entity) {
        Vector3d vel = EntityDirectionUtil.getFacingVector(entity);
        vel = vel.mul(-Probability.getRangedRandom(1.2, 1.5));
        vel = new Vector3d(vel.getX(), MathExt.bound(vel.getY(), .175, .8), vel.getZ());
        entity.setVelocity(vel);
    }

    @Override
    public Optional<Instruction<DamagedCondition, Boss<Living, ZoneBossDetail<FreakyFourInstance>>>> apply(
            DamagedCondition damagedCondition, Boss<Living, ZoneBossDetail<FreakyFourInstance>> livingZoneBossDetailBoss
    ) {
        DamageEntityEvent event = damagedCondition.getEvent();

        new PlayerCombatParser() {
            @Override
            public void processPlayerAttack(Player attacker, Living defender) {
                boolean backTeleport = Probability.getChance(config.backTeleport);
                if (backTeleport || damagedCondition.getDamageSource().get() instanceof IndirectEntityDamageSource) {
                    double distSQ = 2;
                    double maxDist = 1;

                    if (defender instanceof Skeleton) {
                        distSQ = defender.getLocation().getPosition().distanceSquared(attacker.getLocation().getPosition());
                        maxDist = config.snipeeTeleportDist;
                    }

                    if (backTeleport || distSQ > Math.pow(maxDist, 2)) {
                        final Entity finalDamager = attacker;
                        Task.builder().execute(() -> {
                            defender.setLocation(finalDamager.getLocation());
                            throwBack(defender);
                        }).delayTicks(1).submit(SkreePlugin.inst());
                    }
                }
            }
        }.parse(event);

        return Optional.empty();
    }
}
