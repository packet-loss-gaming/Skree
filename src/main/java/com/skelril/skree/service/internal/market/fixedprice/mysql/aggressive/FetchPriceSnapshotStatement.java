/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.market.fixedprice.mysql.aggressive;

import com.skelril.nitro.data.sql.SQLReturningStatement;
import com.skelril.skree.service.internal.market.PriceSnapshot;

import java.sql.Connection;
import java.sql.SQLException;

public class FetchPriceSnapshotStatement implements SQLReturningStatement<PriceSnapshot> {
    @Override
    public PriceSnapshot executeStatement() throws SQLException {
        return null;
    }

    @Override
    public void setConnection(Connection connection) {

    }
}
