/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.market;

import java.math.BigDecimal;
import java.util.UUID;

public interface MarketOfferSnapshot {
    UUID getOfferID();
    UUID getOfferer();
    MarketOfferType getType();

    MarketItem getItem();
    BigDecimal getPrice();

    MarketOfferStatus getStatus();

    int getTakenQuantity();
    default int getAvailibleQuantity() {
        return getCompletedQuantity() - getTakenQuantity();
    }

    int getCompletedQuantity();
    default int getPendingQuantity() {
        return getTotalQuantity() - getCompletedQuantity();
    }
    int getTotalQuantity();
}
