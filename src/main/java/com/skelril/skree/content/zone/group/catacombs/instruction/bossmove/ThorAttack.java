/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.catacombs.instruction.bossmove;

import com.skelril.openboss.Boss;
import com.skelril.openboss.Instruction;
import com.skelril.openboss.condition.DamageCondition;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.zone.group.catacombs.CatacombsBossDetail;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.monster.Zombie;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnCause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class ThorAttack implements Instruction<DamageCondition, Boss<Zombie, CatacombsBossDetail>> {
    @Override
    public Optional<Instruction<DamageCondition, Boss<Zombie, CatacombsBossDetail>>> apply(
            DamageCondition damageCondition, Boss<Zombie, CatacombsBossDetail> zombieCatacombsBossDetailBoss
    ) {
        Entity bossEnt = zombieCatacombsBossDetailBoss.getTargetEntity().get();
        Entity toHit = damageCondition.getAttacked();
        toHit.setVelocity(bossEnt.getRotation().mul(2));

        Task.builder().execute(() -> {
            Location<World> targetLoc = toHit.getLocation();
            Task.builder().execute(() -> {
                Optional<Entity> optEnt = toHit.getWorld().createEntity(EntityTypes.LIGHTNING, targetLoc.getPosition());
                if (optEnt.isPresent()) {
                    toHit.getWorld().spawnEntity(optEnt.get(), Cause.source(SpawnCause.builder().type(SpawnTypes.PLUGIN).build()).build());
                }
            }).delay(750, TimeUnit.MILLISECONDS).submit(SkreePlugin.inst());
        }).delay(1500, TimeUnit.MILLISECONDS).submit(SkreePlugin.inst());
        return Optional.empty();
    }
}
