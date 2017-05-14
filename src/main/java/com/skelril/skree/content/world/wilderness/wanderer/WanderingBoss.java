/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.wilderness.wanderer;

import com.skelril.skree.content.world.wilderness.WildernessBossDetail;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;

public interface WanderingBoss<T extends Entity> {
    EntityType getEntityType();

    default int getSpawnChance() {
        return 100;
    }

    void bind(T entity, WildernessBossDetail detail);
    default boolean apply(Entity entity, WildernessBossDetail detail) {
        if (!getEntityType().getEntityClass().isInstance(entity)) {
            return false;
        }

        // Verified through the entity type check above
        // noinspection unchecked
        bind((T) entity, detail);
        return true;
    }
}
