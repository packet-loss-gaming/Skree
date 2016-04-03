/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.world;

import com.skelril.skree.db.SQLHandle;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

import static com.skelril.skree.db.schema.Tables.WORLDS;

public class ServerSideWorldRegistar {
    public void register(String name) {
        try (Connection con = SQLHandle.getConnection()) {
            DSLContext create = DSL.using(con);

            Timestamp createdTime = new Timestamp(System.currentTimeMillis());

            create.insertInto(WORLDS).columns(WORLDS.NAME, WORLDS.CREATED_AT)
                    .values(name, createdTime)
                    .onDuplicateKeyUpdate().set(WORLDS.CREATED_AT, createdTime)
                    .execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
