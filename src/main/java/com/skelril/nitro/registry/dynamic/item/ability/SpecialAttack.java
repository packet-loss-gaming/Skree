/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic.item.ability;

import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

public interface SpecialAttack extends Ability {
    void run(Living owner, Living target);

    default void notify(Living living, Text message) {
        if (living instanceof Player) {
            ((Player) living).sendMessage(message);
        }
    }
}
