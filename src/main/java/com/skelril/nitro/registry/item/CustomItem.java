/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.item;

import net.minecraft.item.Item;

public abstract class CustomItem extends Item implements ICustomItem {
    protected CustomItem() {
        this.maxStackSize = __getMaxStackSize();
        this.setCreativeTab(__getCreativeTab());

        if (this instanceof DegradableItem) {
            this.setMaxDamage(((DegradableItem) this).__getMaxUses());
        }
    }
}
