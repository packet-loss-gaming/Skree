/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.database;

import com.skelril.skree.db.SQLHandle;
import com.skelril.skree.service.DatabaseService;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.world.LoadWorldEvent;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

import static com.skelril.skree.db.schema.Tables.PLAYERS;
import static com.skelril.skree.db.schema.Tables.WORLDS;

public class DatabaseServiceImpl implements DatabaseService {
    @Listener
    public void onLogin(ClientConnectionEvent.Auth event) {
        try (Connection con = SQLHandle.getConnection()) {
            DSLContext create = DSL.using(con);
            Timestamp loginTime = new Timestamp(System.currentTimeMillis());
            create.insertInto(PLAYERS).columns(PLAYERS.UUID, PLAYERS.LAST_LOGIN)
                    .values(event.getProfile().getUniqueId().toString(), loginTime)
                    .onDuplicateKeyUpdate().set(PLAYERS.LAST_LOGIN, loginTime)
                    .execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Listener
    public void onWorldLoad(LoadWorldEvent event) {
        try (Connection con = SQLHandle.getConnection()) {
            DSLContext create = DSL.using(con);
            create.insertInto(WORLDS).columns(WORLDS.NAME)
                    .values(event.getTargetWorld().getName())
                    .onDuplicateKeyIgnore()
                    .execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
