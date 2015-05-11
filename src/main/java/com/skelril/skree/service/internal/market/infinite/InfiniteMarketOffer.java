/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.market.infinite;

import com.skelril.skree.service.internal.market.MarketEntry;
import com.skelril.skree.service.internal.market.MarketOffer;

import java.util.UUID;

public class InfiniteMarketOffer implements MarketOffer {

    protected MarketEntry entry;

    public InfiniteMarketOffer(MarketEntry entry) {
        this.entry = entry;
    }

    @Override
    public UUID getOfferer() {
        return null;
    }

    @Override
    public MarketEntry getEntry() {
        return entry;
    }

    @Override
    public int getAmountRequested() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getAmountCompleted() {
        return 0;
    }
}
