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

public class TableCreationStatement implements SQLVoidStatement {

    private Connection connection;
    private String sql;

    public TableCreationStatement(String table) {
        sql = "CREATE TABLE IF NOT EXISTS `" + table +"` (" +
                "`id` INT NOT NULL AUTO_INCREMENT," +
                "`name` VARCHAR(25) NOT NULL," +
                "`expiry` LONG NOT NULL," +
                "PRIMARY KEY (`id`)," +
                "UNIQUE INDEX `name` (`name`)" +
                ");";
    }

    @Override
    public void executeStatement() throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        }
    }

    @Override
    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
