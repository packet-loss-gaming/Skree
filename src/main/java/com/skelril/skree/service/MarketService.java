/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service;

import com.skelril.skree.service.internal.market.*;
import org.spongepowered.api.entity.player.User;
import org.spongepowered.api.item.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface MarketService {
    MarketEntry lookup(ItemStack stack);
    MarketEntry lookup(String name);

    void setValue(MarketEntry entry, BigDecimal price);
    void setBuyPercentValue(MarketEntry entry, float rate);
    void setSellPercentValue(MarketEntry entry, float rate);

    default SellOffer sell(User user, MarketEntry entry) {
        return sell(user, entry, 1);
    }
    SellOffer sell(User user, MarketEntry entry, int amount);

    default SellOffer sellRequest(User user, MarketEntry entry, BigDecimal price) {
        return sellRequest(user, entry, price, 1);
    }
    SellOffer sellRequest(User user, MarketEntry entry, BigDecimal price, int amount);


    default BuyOffer buy(User user, MarketEntry entry) {
        return buy(user, entry, 1);
    }
    BuyOffer buy(User user, MarketEntry entry, int amount);

    default BuyOffer buyRequest(User user, MarketEntry entry, BigDecimal price) {
        return buyRequest(user, entry, price, 1);
    }
    BuyOffer buyRequest(User user, MarketEntry entry, BigDecimal price, int amount);

    List<BuyOffer> findBuyOffers(MarketEntry entry);
    List<SellOffer> findSellOffers(MarketEntry entry);
    List<MarketOffer> findOffers(MarketEntry entry);

    List<MarketOffer> getActiveOffers();

    List<MarketTransaction> getTransactions();
    List<MarketTransaction> getTransactions(Date start, Date end);

    List<MarketTransaction> getPurchasesFrom(User user);
    List<MarketTransaction> getPurchasesFrom(User user, Date start, Date end);

    List<MarketTransaction> getSalesTo(User user);
    List<MarketTransaction> getSalesTo(User user, Date start, Date end);

    List<MarketTransaction> getTransactionsFrom(User user);
    List<MarketTransaction> getTransactionsFrom(User user, Date start, Date end);
}
