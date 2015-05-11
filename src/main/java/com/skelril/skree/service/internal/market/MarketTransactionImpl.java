/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.market;

import java.math.BigDecimal;
import java.util.UUID;

public class MarketTransactionImpl implements MarketTransaction {

    private UUID buyer;
    private UUID seller;
    private MarketEntry entry;
    private int amount;
    private BigDecimal pricePerItem;

    public MarketTransactionImpl(BuyOffer bought, SellOffer sold, int completed) {
        this(bought.getOfferer(), sold.getOfferer(), sold.getEntry(), completed, sold.getMinPricePerItem());
    }

    public MarketTransactionImpl(UUID buyer, UUID seller, MarketEntry entry, int amount, BigDecimal pricePerItem) {
        this.buyer = buyer;
        this.seller = seller;
        this.entry = entry;
        this.amount = amount;
        this.pricePerItem = pricePerItem;
    }

    @Override
    public UUID getBuyer() {
        return buyer;
    }

    @Override
    public UUID getSeller() {
        return seller;
    }

    @Override
    public MarketEntry getEntry() {
        return entry;
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public BigDecimal getPricePerItem() {
        return pricePerItem;
    }

    @Override
    public BigDecimal getTotalPrice() {
        return pricePerItem.multiply(new BigDecimal(amount));
    }
}
