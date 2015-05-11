/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.market.fixedprice;

import com.google.common.collect.Lists;
import com.skelril.skree.service.MarketService;
import com.skelril.skree.service.internal.market.*;
import com.skelril.skree.service.internal.market.infinite.InfiniteBuyOffer;
import com.skelril.skree.service.internal.market.infinite.InfiniteSellOffer;
import org.spongepowered.api.entity.player.User;
import org.spongepowered.api.item.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.*;

public abstract class FixedPriceMarketServiceImpl implements MarketService {

    protected Map<String, FixedPriceMarketEntry> entries = new HashMap<>();

    @Override
    public MarketEntry lookup(ItemStack stack) {
        return null;
    }

    @Override
    public MarketEntry lookup(String name) {
        return entries.get(name);
    }

    @Override
    public void setValue(MarketEntry entry, BigDecimal price) {
        entries.get(entry.getName()).setValue(price);
    }

    @Override
    public void setBuyPercentValue(MarketEntry entry, float rate) {
        entries.get(entry.getName()).setBuyPercent(rate);
    }

    @Override
    public void setSellPercentValue(MarketEntry entry, float rate) {
        entries.get(entry.getName()).setSellPercent(rate);
    }

    @Override
    public SellOffer sell(User user, MarketEntry entry, int amount) {
        FixedPriceSellOffer offer = new FixedPriceSellOffer(
                user.getUniqueId(),
                entry,
                entry.getValueBoughtFor(),
                amount
        );

        complete(offer);
        return offer;
    }

    @Override
    public SellOffer sellRequest(User user, MarketEntry entry, BigDecimal price, int amount) {
        BigDecimal systemsOffer = entries.get(entry.getName()).getValueBoughtFor();
        FixedPriceSellOffer offer = new FixedPriceSellOffer(user.getUniqueId(), entry, price, amount);
        // If the system's offer is greater than or equal to the amount you're requesting
        // allow the transaction, otherwise fail
        if (systemsOffer.compareTo(offer.getMinPricePerItem()) >= 0) {
            complete(offer);
        } else {
            offer.fail();
        }
        return offer;
    }

    @Override
    public BuyOffer buy(User user, MarketEntry entry, int amount) {
        FixedPriceBuyOffer offer = new FixedPriceBuyOffer(
                user.getUniqueId(),
                entry,
                entry.getValueSoldFor(),
                amount
        );

        complete(offer);
        return offer;
    }

    @Override
    public BuyOffer buyRequest(User user, MarketEntry entry, BigDecimal price, int amount) {
        BigDecimal systemsOffer = entries.get(entry.getName()).getValueSoldFor();
        FixedPriceBuyOffer offer = new FixedPriceBuyOffer(user.getUniqueId(), entry, price, amount);
        // If your offer is greater than or equal to the amount the system is requesting
        // allow the transaction, otherwise fail
        if (offer.getMaxPricePerItem().compareTo(systemsOffer) >= 0) {
            complete(offer);
        } else {
            offer.fail();
        }
        return offer;
    }


    @Override
    public List<BuyOffer> findBuyOffers(MarketEntry entry) {
        return Collections.singletonList(new InfiniteBuyOffer(entry));
    }

    @Override
    public List<SellOffer> findSellOffers(MarketEntry entry) {
        return Collections.singletonList(new InfiniteSellOffer(entry));
    }

    @Override
    public List<MarketOffer> findOffers(MarketEntry entry) {
        return Lists.newArrayList(new InfiniteSellOffer(entry), new InfiniteBuyOffer(entry));
    }

    @Override
    public List<MarketOffer> getActiveOffers() {
        return Collections.emptyList();
    }

    protected void complete(FixedPriceSellOffer sold) {
        int completed = sold.getAmountRemaining();

        InfiniteBuyOffer bought = new InfiniteBuyOffer(sold.getEntry());
        sold.complete(completed);

        logTransaction(new MarketTransactionImpl(bought, sold, completed));
    }

    protected void complete(FixedPriceBuyOffer bought) {
        int completed = bought.getAmountRemaining();

        InfiniteSellOffer sold = new InfiniteSellOffer(bought.getEntry());
        bought.complete(completed);

        logTransaction(new MarketTransactionImpl(bought, sold, completed));
    }

    protected abstract void logTransaction(MarketTransaction transaction);
}
