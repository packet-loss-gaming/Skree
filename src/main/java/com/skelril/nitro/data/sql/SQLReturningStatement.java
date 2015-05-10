/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.data.sql;

import java.sql.SQLException;

public interface SQLReturningStatement<T> extends SQLStatement {
    T executeStatement() throws SQLException;
}
