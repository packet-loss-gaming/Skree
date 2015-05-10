/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.modifier.mysql.lazy;

import com.skelril.skree.service.internal.modifier.ModifierServiceImpl;
import org.spongepowered.api.Game;
import org.spongepowered.api.service.sql.SqlService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class LazyMySQLModifierService extends ModifierServiceImpl {

    private String table;
    private DataSource source;

    public LazyMySQLModifierService(Game game, String database, String table) {
        super();
        try {
            establishDataSource(game, database);
            establishDatatable(table);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.table = table;
    }

    private void establishDataSource(Game game, String database) throws SQLException {
        source = game.getServiceManager().provide(SqlService.class).get().getDataSource(database);
    }

    private void establishDatatable(String table) throws SQLException {
        try (Connection con = source.getConnection()) {
            TableCreationStatement statement = new TableCreationStatement(table);
            statement.setConnection(con);
            statement.executeStatement();
        }
    }

    @Override
    protected void repopulateData() {
        try {
            try (Connection con = source.getConnection()) {
                FetchStatement statement = new FetchStatement(table);
                statement.setConnection(con);
                modifiers.putAll(statement.executeStatement());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void updateModifier(String modifier, long time) {
        try {
            try (Connection con = source.getConnection()) {
                UpdateStatement statement = new UpdateStatement(table, modifier, time);
                statement.setConnection(con);
                statement.executeStatement();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
