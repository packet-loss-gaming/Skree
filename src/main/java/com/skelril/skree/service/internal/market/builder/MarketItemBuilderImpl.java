/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.market.builder;

import com.skelril.skree.service.internal.market.MarketItem;
import com.skelril.skree.service.internal.market.MarketItemBuilder;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Collection;

public class MarketItemBuilderImpl implements MarketItemBuilder {
    @Override
    public MarketItem fromItemStack(ItemStack stack) {
        return null;
    }

    @Override
    public ItemStack create() {
        return null;
    }

    @Override
    public Collection<ItemStack> create(int amt) {
        return null;
    }
}
