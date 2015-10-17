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
    ItemStack getItem(String alias);

    BigDecimal getSellFactor(BigDecimal buyPrice);

    BigDecimal getPrice(String alias);
    BigDecimal getPrice(ItemStack stack);
    boolean setPrice(String alias, BigDecimal price);

    void addItem(ItemStack stack);

    void setPrimaryAlias(String alias);
    boolean addAlias(String alias, ItemStack stack);

    String getAlias(ItemStack stack);

    /**
     * A mapping of the primary alias to the price
     * @return
     */
    List<Clause<String, BigDecimal>> getPrices();
    List<Clause<String, BigDecimal>> getPrices(String aliasConstraint);
}
