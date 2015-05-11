/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.market.infinite;

import com.skelril.skree.service.internal.market.BuyOffer;
import com.skelril.skree.service.internal.market.MarketEntry;

import java.math.BigDecimal;

public class InfiniteBuyOffer extends InfiniteMarketOffer implements BuyOffer {
    public InfiniteBuyOffer(MarketEntry entry) {
        super(entry);
    }

    @Override
    public BigDecimal getMaxPricePerItem() {
        return entry.getValueBoughtFor();
    }
}
