/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.admin;

import com.skelril.nitro.registry.item.sword.CustomSword;

public class SwordOGreatPain extends CustomSword {
    public SwordOGreatPain() {
        super(ToolMaterial.WOOD);
    }

    @Override
    public String getType() {
        return "oGreatPain";
    }

    @Override
    public double getDamage() {
        return 100000000;
    }

    @Override
    public int getMaxDamage() {
        return 14;
    }
}