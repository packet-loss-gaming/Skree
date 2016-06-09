/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.item.armor;

import net.minecraft.inventory.EntityEquipmentSlot;

public abstract class CustomHelmet extends CustomArmor {
    public CustomHelmet() {
        super(EntityEquipmentSlot.HEAD);
    }

    @Override
    public String __getArmorCategory() {
        return "helmet";
    }
}
