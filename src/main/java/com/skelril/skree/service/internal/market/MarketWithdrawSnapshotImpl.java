/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.market;

import java.util.UUID;

public class MarketWithdrawSnapshotImpl implements MarketWithdrawSnapshot {
    private final UUID offer;
    private final MarketWithdrawStatus status;

    public MarketWithdrawSnapshotImpl(MarketOfferSnapshot snapshot) {
        this(snapshot.getOfferID(), MarketWithdrawStatus.COMPLETED);
    }

    public MarketWithdrawSnapshotImpl(UUID offer, MarketWithdrawStatus status) {
        this.offer = offer;
        this.status = status;
    }

    @Override
    public UUID getOfferID() {
        return offer;
    }

    @Override
    public MarketWithdrawStatus getStatus() {
        return status;
    }

}
