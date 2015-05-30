/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.market.fixedprice.mysql.aggressive;

import com.skelril.nitro.data.sql.SQLReturningStatement;

import java.sql.Connection;
import java.sql.SQLException;

public class CountAllOfferStatement implements SQLReturningStatement<Integer> {
    @Override
    public Integer executeStatement() throws SQLException {
        return null;
    }

    @Override
    public void setConnection(Connection connection) {

    }
}
