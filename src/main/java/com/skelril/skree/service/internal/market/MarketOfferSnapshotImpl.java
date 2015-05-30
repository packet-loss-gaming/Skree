/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.market;

import java.math.BigDecimal;
import java.util.UUID;

public class MarketOfferSnapshotImpl implements MarketOfferSnapshot {

    private final UUID offerID;
    private final UUID offerer;
    private final MarketOfferType type;
    private final MarketItem item;
    private final BigDecimal price;
    private final MarketOfferStatus status;
    private final int taken;
    private final int complete;
    private final int total;

    public MarketOfferSnapshotImpl(UUID offerID, UUID offerer, MarketOfferType type, MarketItem item, BigDecimal price, MarketOfferStatus status, int taken, int complete, int total) {
        this.offerID = offerID;
        this.offerer = offerer;
        this.type = type;
        this.item = item;
        this.price = price;
        this.status = status;
        this.taken = taken;
        this.complete = complete;
        this.total = total;
    }

    @Override
    public UUID getOfferID() {
        return offerID;
    }

    @Override
    public UUID getOfferer() {
        return offerer;
    }

    @Override
    public MarketOfferType getType() {
        return type;
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
    public MarketOfferStatus getStatus() {
        if (complete == total) {
            return MarketOfferStatus.COMPLETE;
        }
        return status;
    }

    @Override
    public int getTakenQuantity() {
        return taken;
    }

    @Override
    public int getCompletedQuantity() {
        return complete;
    }

    @Override
    public int getTotalQuantity() {
        return total;
    }
}
