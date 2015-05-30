/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.market;

import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Collection;

public interface MarketItemBuilder {
    MarketItem fromItemStack(ItemStack stack);

    ItemStack create();
    Collection<ItemStack> create(int amt);
}
