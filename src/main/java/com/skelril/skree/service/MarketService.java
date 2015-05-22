/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service;

import com.skelril.skree.service.internal.market.*;
import com.skelril.skree.service.internal.market.buy.BuyOffer;
import com.skelril.skree.service.internal.market.sell.SellOffer;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.UUID;

/**
 * A market system composed internally of three tables
 * - Items
 * - Offers
 * - Transactions (TODO at a later date)
 *
 * Example usage:
 *      // Get the item
 *      MarketItem item = market.getItem("Magic Bean");
 *      // Get the price
 *      PriceSnapshot price = market.getPrice(item);
 *      // Make a buy offer based off the maximum you are advised to offer based on the items
 *      // current value
 *      BuyOffer offer = new InstantBuyOffer(player, item, price.getMaxAdvisory());
 *      // Get the result
 *      MarketOfferSnapshot result = market.offer(offer);
 *      // Get the status of the offer
 *      MarketOfferStatus status = result.getStatus();
 */
public interface MarketService {
    // Supply
    MarketOfferSnapshot offer(BuyOffer offer);
    MarketOfferSnapshot offer(SellOffer offer);

    // Modification
    void submit(PriceUpdate update);

    // Lookup
    MarketItem getItem(UUID ID);
    MarketItem getItem(String name);
    MarketItem getItem(ItemStack itemStack);

    PriceSnapshot getPrice(MarketItem type);

    MarketOfferSnapshot getOffer(UUID ID);
    MarketOfferSnapshot getOffer(MarketOfferSnapshot offer);

    // Transactions
    //    MarketTransaction getTransaction(UUID ID);
    //    MarketTransaction getTransactions(PlayerMarketQuery);
    //    MarketTransaction getTransactions(ItemMarketQuery);
    //    MarketTransaction getTransactions(TimeQuery);
}
