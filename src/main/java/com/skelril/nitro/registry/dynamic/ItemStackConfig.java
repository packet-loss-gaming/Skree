/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.dynamic;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;

public class ItemStackConfig {
    String id;
    int data;

    public ItemStack toNSMStack() {
        ItemType spongeType = Sponge.getRegistry().getType(ItemType.class, id).get();
        return new ItemStack((Item) spongeType, 1, data);
    }
}
