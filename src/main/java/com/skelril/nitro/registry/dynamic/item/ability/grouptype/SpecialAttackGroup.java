/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.item.ability.grouptype;

import com.skelril.nitro.probability.Probability;
import com.skelril.nitro.registry.dynamic.item.ability.AbilityGroup;
import com.skelril.nitro.registry.dynamic.item.ability.SpecialAttack;

import java.util.List;

public abstract class SpecialAttackGroup extends AbilityGroup {
    public abstract List<SpecialAttack> getSpecialAttacks();

    public SpecialAttack getNextAttackToRun() {
        return Probability.pickOneOf(getSpecialAttacks());
    }
}
