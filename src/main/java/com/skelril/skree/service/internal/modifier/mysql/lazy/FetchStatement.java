/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.modifier.mysql.lazy;

import com.skelril.nitro.data.sql.SQLReturningStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class FetchStatement implements SQLReturningStatement<Map<String, Long>> {

    private Connection connection;
    private String sql;

    public FetchStatement(String table) {
        this.sql = "SELECT `name`, `expiry` FROM `" + table + "`";
    }

    @Override
    public Map<String, Long> executeStatement() throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            try (ResultSet results = statement.executeQuery()) {
                Map<String, Long> modifiers = new HashMap<>();
                while (results.next()) {
                    modifiers.put(results.getString(1), results.getLong(2));
                }
                return modifiers;
            }
        }
    }

    @Override
    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
