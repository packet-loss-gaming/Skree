/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.market;

import java.math.BigDecimal;
import java.util.UUID;

public class MarketOfferSnapshotImpl implements MarketOfferSnapshot {
    @Override
    public UUID getOfferID() {
        return null;
    }

    @Override
    public UUID getOfferer() {
        return null;
    }

    @Override
    public MarketItem getItem() {
        return null;
    }

    @Override
    public BigDecimal getPrice() {
        return null;
    }

    @Override
    public MarketOfferStatus getStatus() {
        return null;
    }

    @Override
    public int getCompletedQuantity() {
        return 0;
    }

    @Override
    public int getTotalQuantity() {
        return 0;
    }
}
