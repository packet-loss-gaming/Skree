/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service;

import com.skelril.skree.service.internal.combat.CombatRelationship;
import com.skelril.skree.service.internal.combat.CombatRelationshipType;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.Living;

public interface CombatService {
    void establishRelationship(CombatRelationship relationship);

    CombatRelationshipType getStatus(Living attacker, Living defender);
    CombatRelationshipType getStatus(EntityType attackerType, Living defender);
    CombatRelationshipType getStatus(Living attacker, EntityType defender);
    CombatRelationshipType getStatus(EntityType attackerType, EntityType defenderType);
}
