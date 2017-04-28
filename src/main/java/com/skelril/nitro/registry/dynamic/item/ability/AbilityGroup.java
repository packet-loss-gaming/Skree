/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.item.ability;

import java.util.ArrayList;
import java.util.List;

public final class AbilityGroup {
    private AbilityCooldownProfile coolDown;
    private transient List<AbilityCluster> clusters = new ArrayList<>();

    public AbilityGroup(AbilityCooldownProfile coolDown) {
        this.coolDown = coolDown;
    }

    public AbilityCooldownProfile getCoolDown() {
        return coolDown;
    }

    public List<AbilityCluster> getClusters() {
        return clusters;
    }
}
