/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.item.ability.grouptype;

import com.skelril.nitro.registry.dynamic.item.ability.AbilityCooldownManager;
import com.skelril.nitro.registry.dynamic.item.ability.SpecialAttack;

import java.util.List;

public class MeleeAttackGroup extends SpecialAttackGroup {
    private List<SpecialAttack> meleeAttacks;

    @Override
    public List<SpecialAttack> getSpecialAttacks() {
        return meleeAttacks;
    }

    @Override
    public GroupListener getListenerFor(String itemID, AbilityCooldownManager coolDownManager) {
        return new MeleeAttackGroupListener(this, itemID, coolDownManager);
    }
}
