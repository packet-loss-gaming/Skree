/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.catacombs.instruction.bossmove;

import com.skelril.nitro.position.CuboidContainmentPredicate;
import com.skelril.openboss.Boss;
import com.skelril.openboss.EntityDetail;
import com.skelril.openboss.Instruction;
import com.skelril.openboss.condition.DamagedCondition;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.cause.entity.damage.DamageTypes;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;

import java.util.Optional;

public abstract class DamageNearby<T extends Boss<? extends Living, ?>> implements Instruction<DamagedCondition, T> {
    public boolean checkTarget(T boss, Living entity) {
        return true;
    }

    public abstract double getDamage(EntityDetail detail);

    public void damage(T boss, Living entity) {
        entity.damage(
                getDamage(boss.getDetail()),
                EntityDamageSource.builder().type(DamageTypes.ATTACK).entity(boss.getTargetEntity().get()).build()
        );
    }

    @Override
    public Optional<Instruction<DamagedCondition, T>> apply(DamagedCondition damagedCondition, T bossDetail) {
        Living boss = bossDetail.getTargetEntity().get();
        CuboidContainmentPredicate predicate = new CuboidContainmentPredicate(boss.getLocation().getPosition(), 2, 2, 2);
        boss.getNearbyEntities(e -> predicate.test(e.getLocation().getPosition())).stream()
                .filter(e -> e instanceof Living)
                .map(e -> (Living) e)
                .filter(e -> checkTarget(bossDetail, e))
                .forEach(e -> damage(bossDetail, e));

        return Optional.empty();
    }
}
