/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.modifier.mysql.lazy;

import com.skelril.nitro.data.sql.SQLVoidStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateStatement implements SQLVoidStatement {

    private Connection connection;
    private String sql;
    private String name;
    private long expiry;

    public UpdateStatement(String table, String name, long expiry) {
        this.sql = "INSERT INTO `" + table + "` (name, expiry) VALUES (?, ?) ON DUPLICATE KEY UPDATE expiry=values(expiry)";
        this.name = name;
        this.expiry = expiry;
    }

    @Override
    public void executeStatement() throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setLong(2, expiry);
            statement.execute();
        }
    }

    @Override
    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
