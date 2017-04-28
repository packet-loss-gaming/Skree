/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.item.ability;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;

public class AbilityCooldownHandler {
    private AbilityCooldownProfile profile;
    private AbilityCooldownManager manager;

    public AbilityCooldownHandler(AbilityCooldownProfile profile, AbilityCooldownManager manager) {
        this.profile = profile;
        this.manager = manager;
    }

    public boolean canUseAbility(Entity entity) {
        return entity instanceof Player && canUseAbility((Player) entity);
    }

    private boolean canUseAbility(Player player) {
        return manager.canUseAbility(player, profile);
    }

    public void useAbility(Entity entity) {
        if (entity instanceof Player) {
            useAbility((Player) entity);
        }
    }

    private void useAbility(Player player) {
        manager.usedAbility(player, profile);
    }
}
