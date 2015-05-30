/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.market.sell;

import com.skelril.skree.service.internal.market.MarketOffer;
import com.skelril.skree.service.internal.market.MarketOfferType;

public interface SellOffer extends MarketOffer {
    @Override
    default MarketOfferType getType() {
        return MarketOfferType.SELL;
    }
}
