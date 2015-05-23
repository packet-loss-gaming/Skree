/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.market.fixedprice.mysql.aggressive;

import com.skelril.skree.service.MarketService;
import com.skelril.skree.service.internal.market.*;
import com.skelril.skree.service.internal.market.buy.BuyOffer;
import com.skelril.skree.service.internal.market.sell.SellOffer;
import org.spongepowered.api.item.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.UUID;

public class AggressiveMySQLFixedPriceMarketServiceImpl implements MarketService {
    @Override
    public MarketOfferSnapshot offer(BuyOffer offer) {
        BigDecimal maxAdvisory = getPrice(offer.getItem()).getMaxAdvisory();
        if (offer.getPrice().compareTo(maxAdvisory) == -1) {
            return new MarketOfferSnapshotImpl();
        }
        return new MarketOfferSnapshotImpl();
    }

    @Override
    public MarketOfferSnapshot offer(SellOffer offer) {
        BigDecimal minAdvisory = getPrice(offer.getItem()).getMinAdvisory();
        return null;
    }

    @Override
    public void submit(PriceUpdate update) {

    }

    @Override
    public MarketItem getItem(UUID ID) {
        return null;
    }

    @Override
    public MarketItem getItem(String name) {
        return null;
    }

    @Override
    public MarketItem getItem(ItemStack itemStack) {
        return null;
    }

    @Override
    public PriceSnapshot getPrice(MarketItem type) {
        return null;
    }

    @Override
    public MarketOfferSnapshot getOffer(UUID ID) {
        return null;
    }

    @Override
    public MarketOfferSnapshot getOffer(MarketOfferSnapshot offer) {
        return null;
    }
}
