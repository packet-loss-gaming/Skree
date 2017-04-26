/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.ability.combat;

import com.skelril.nitro.registry.dynamic.item.ability.SpecialAttack;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.text.Text;

public class DoomBlade implements SpecialAttack {
    @Override
    public void run(Living owner, Living target) {
        notify(owner, Text.of("Your sword dishes out an incredible 0 damage!"));
    }
}
