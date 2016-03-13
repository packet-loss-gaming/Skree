/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.freakyfour;

import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;

public enum FreakyFourBoss {
    CHARLOTTE(EntityTypes.SPIDER),
    FRIMUS(EntityTypes.BLAZE),
    DA_BOMB(EntityTypes.CREEPER),
    SNIPEE(EntityTypes.SKELETON);

    private EntityType type;

    private FreakyFourBoss(EntityType type) {
        this.type = type;
    }

    public EntityType getEntityType() {
        return type;
    }
}
