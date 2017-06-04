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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.skelril.skree.db.schema.Tables.PLAYERS;

public class DatabaseServiceImpl implements DatabaseService {
  private HashMap<UUID, Long> sessionStartTime = new HashMap<>();

  @Listener
  public void onLogin(ClientConnectionEvent.Auth event) {
    try (Connection con = SQLHandle.getConnection()) {
      DSLContext create = DSL.using(con);

      UUID uuid = event.getProfile().getUniqueId();
      long time = System.currentTimeMillis();
      Timestamp timeStamp = new Timestamp(time);

      create.insertInto(PLAYERS).columns(PLAYERS.UUID, PLAYERS.FIRST_LOGIN, PLAYERS.LAST_LOGIN)
          .values(uuid.toString(), timeStamp, timeStamp)
          .onDuplicateKeyUpdate()
          .set(PLAYERS.TIMES_PLAYED, PLAYERS.TIMES_PLAYED.add(1))
          .set(PLAYERS.LAST_LOGIN, timeStamp)
          .execute();

      sessionStartTime.put(uuid, time);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Listener
  public void onLogout(ClientConnectionEvent.Disconnect event) {
    try (Connection con = SQLHandle.getConnection()) {
      DSLContext create = DSL.using(con);

      UUID uuid = event.getTargetEntity().getUniqueId();
      long diff = System.currentTimeMillis() - sessionStartTime.remove(uuid);
      long diffSeconds = TimeUnit.MILLISECONDS.toSeconds(diff);

      create.update(PLAYERS).set(PLAYERS.SECONDS_PLAYED, PLAYERS.SECONDS_PLAYED.add(diffSeconds)).where(
          PLAYERS.UUID.equal(uuid.toString())
      ).execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
