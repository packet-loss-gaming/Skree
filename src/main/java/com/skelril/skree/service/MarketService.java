/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service;

import com.skelril.nitro.Clause;
import org.spongepowered.api.item.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.List;

public interface MarketService {
    BigDecimal getPrice(String alias);
    boolean setPrice(String alias, BigDecimal price);

    void addItem(ItemStack stack);

    void setPrimaryAlias(String alias, ItemStack stack);
    boolean addAlias(String alias, ItemStack stack);

    String getAlias(ItemStack stack);

    /**
     * A mapping of the primary alias to the price
     * @return
     */
    List<Clause<String, BigDecimal>> getPrices();
}
