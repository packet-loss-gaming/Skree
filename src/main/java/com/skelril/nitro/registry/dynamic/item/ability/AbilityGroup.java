/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.item.ability;

import com.skelril.nitro.registry.dynamic.item.ability.grouptype.GroupListener;

public abstract class AbilityGroup {
    private AbilityCooldownProfile coolDown;

    public AbilityCooldownProfile getCoolDown() {
        return coolDown;
    }

    public abstract GroupListener getListenerFor(String itemID, AbilityCooldownManager coolDownManager);
}
