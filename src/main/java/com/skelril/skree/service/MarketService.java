/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service;

import com.skelril.nitro.Clause;
import org.spongepowered.api.item.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.*;

public interface MarketService {
    String VALID_ALIAS_REGEX = "^([A-Za-z-0-9 ]+|)$";

    Optional<ItemStack> getItem(String alias);

    BigDecimal getSellFactor(BigDecimal buyPrice);

    Optional<BigDecimal> getPrice(String alias);
    Optional<BigDecimal> getPrice(ItemStack stack);
    boolean setPrice(String alias, BigDecimal price);
    boolean setPrice(ItemStack stack, BigDecimal price);

    boolean addItem(ItemStack stack);
    boolean remItem(ItemStack stack);

    boolean setPrimaryAlias(String alias);
    boolean addAlias(String alias, ItemStack stack);
    boolean remAlias(String alias);

    Optional<String> getAlias(String alias);
    Optional<String> getAlias(ItemStack stack);

    /**
     * A mapping of the primary alias to the price
     * @return
     */
    List<Clause<String, BigDecimal>> getPrices();
    List<Clause<String, BigDecimal>> getPrices(String aliasConstraint);

    /** Transactions **/
    default boolean logTransactionByName(UUID user, Clause<String, Integer> itemQuantity) {
        return logTransactionByName(user, Collections.singleton(itemQuantity));
    }

    boolean logTransactionByName(UUID user, Collection<Clause<String, Integer>> itemQuantity);

    default boolean logTransactionByStack(UUID user, Clause<ItemStack, Integer> itemQuantity) {
        return logTransactionByStack(user, Collections.singleton(itemQuantity));
    }

    boolean logTransactionByStack(UUID user, Collection<Clause<ItemStack, Integer>> itemQuantity);
}
