/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.market;

import java.util.UUID;

public interface MarketOffer {
    UUID getOfferer();

    MarketEntry getEntry();

    int getAmountRequested();
    int getAmountCompleted();

    default boolean failedToProcess() {
        return getAmountCompleted() == -1;
    }

    default boolean isCompleted() {
        return getAmountRequested() == getAmountCompleted();
    }
}
