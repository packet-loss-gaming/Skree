/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.combat;

import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.Living;

import java.util.Collection;

public interface CombatRelationship {

    Collection<Living> getOffensiveEntities();
    Collection<EntityType> getOffensiveTypes();

    Collection<Living> getDefensiveEntities();
    Collection<EntityType> getDefensiveTypes();

    CombatRelationshipType getType();
}
