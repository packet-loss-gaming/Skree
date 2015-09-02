/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.market;

import org.spongepowered.api.entity.living.player.User;

import java.math.BigDecimal;
import java.util.UUID;

public abstract class MarketOfferImpl implements MarketOffer {
    private final UUID offerer;
    private final MarketItem item;
    private final BigDecimal price;
    private final int quantity;
    private final MarketOfferPriority priority;

    public MarketOfferImpl(User user, MarketItem item, BigDecimal price, int quantity, MarketOfferPriority priority) {
        this.priority = priority;
        this.offerer = user.getUniqueId();
        this.item = item;
        this.price = price;
        this.quantity = quantity;
    }

    @Override
    public UUID getOfferer() {
        return offerer;
    }

    @Override
    public MarketItem getItem() {
        return item;
    }

    @Override
    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public MarketOfferPriority getPriority() {
        return priority;
    }
}
