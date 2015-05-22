/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.market.sell;

import com.skelril.skree.service.internal.market.MarketItem;
import com.skelril.skree.service.internal.market.MarketOfferImpl;
import com.skelril.skree.service.internal.market.MarketOfferPriority;
import org.spongepowered.api.entity.player.User;

import java.math.BigDecimal;

public class InstantSellOffer extends MarketOfferImpl implements SellOffer {
    public InstantSellOffer(User user, MarketItem item, BigDecimal price) {
        this(user, item, price, 1);
    }

    public InstantSellOffer(User user, MarketItem item, BigDecimal price, int quantity) {
        super(user, item, price, quantity, MarketOfferPriority.INSTANT);
    }
}
