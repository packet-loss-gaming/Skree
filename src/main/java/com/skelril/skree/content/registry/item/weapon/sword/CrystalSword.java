/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.weapon.sword;

import com.skelril.nitro.registry.item.sword.CustomSword;

public class CrystalSword extends CustomSword {
    public CrystalSword() {
        super(ToolMaterial.EMERALD);
    }

    @Override
    public String getType() {
        return "crystal";
    }

    @Override
    public double getDamage() {
        return 8;
    }
}
