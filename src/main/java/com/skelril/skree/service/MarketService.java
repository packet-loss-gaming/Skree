/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service;

import com.skelril.skree.service.internal.market.MarketEntry;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.math.BigDecimal;

public interface MarketService {
    MarketEntry lookup(ItemStack stack);
    MarketEntry lookup(String name);

    void setValue(MarketEntry entry, BigDecimal price);
    void setBuyPercentValue(MarketEntry entry, float rate);
    void setSellPercentValue(MarketEntry entry, float rate);

    BigDecimal priceCheck(MarketEntry entry);

    void sell(Player player, MarketEntry entry);
    void sell(Player player, MarketEntry entry, int amount);

    void buy(Player player, MarketEntry entry);
    void buy(Player player, MarketEntry entry, int amount);
}
