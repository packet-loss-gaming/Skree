/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.tool.hoe;

import com.skelril.nitro.registry.item.hoe.CustomHoe;

public class CrystalHoe extends CustomHoe {
    @Override
    public String __getType() {
        return "crystal";
    }

    @Override
    public int __getMaxUses() {
        return ToolMaterial.EMERALD.getMaxUses();
    }
}
