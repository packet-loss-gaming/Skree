/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.modifier;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.spongepowered.api.Game;
import org.spongepowered.api.service.sql.SqlService;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.jooq.impl.DSL.table;

public class LazyMySQLModifierService extends ModifierServiceImpl {

    private final Table<Record> table;
    private final Field<String> name = DSL.field("name", String.class);
    private final Field<Long> expirey = DSL.field("expirey", Long.class);

    private DataSource source;

    public LazyMySQLModifierService(Game game, String database, String table) {
        super();
        try {
            establishDataSource(game, database);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.table = table(table);
    }

    private void establishDataSource(Game game, String database) throws SQLException {
        source = game.getServiceManager().provide(SqlService.class).get().getDataSource(database);
    }

    @Override
    protected void repopulateData() {
        try {
            try (Connection con = source.getConnection()) {
                Result<?> data = DSL.using(con)
                        .select(name, expirey)
                        .from(table)
                        .fetch();

                data.forEach(a -> setExpiry(a.getValue(name), a.getValue(expirey)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void updateModifier(String modifier, long time) {
        try {
            try (Connection con = source.getConnection()) {
                DSL.using(con)
                        .insertInto(table)
                        .columns(name, expirey)
                        .values(modifier, time)
                        .onDuplicateKeyUpdate()
                        .set(expirey, time)
                        .execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
