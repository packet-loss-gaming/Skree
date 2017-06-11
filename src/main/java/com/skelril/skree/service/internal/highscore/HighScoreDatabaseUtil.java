/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.highscore;

import com.skelril.skree.db.SQLHandle;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static com.skelril.skree.db.schema.Tables.PLAYERS;
import static com.skelril.skree.db.schema.tables.HighScores.HIGH_SCORES;

class HighScoreDatabaseUtil {
  public static Optional<Integer> get(UUID playerId, ScoreType scoreType) {
    try (Connection con = SQLHandle.getConnection()) {
      DSLContext create = DSL.using(con);
      Record1<Integer> result = create.select(HIGH_SCORES.VALUE).from(HIGH_SCORES).where(
          HIGH_SCORES.PLAYER_ID.equal(
              create.select(PLAYERS.ID).from(PLAYERS).where(PLAYERS.UUID.equal(playerId.toString()))
          ).and(
              HIGH_SCORES.SCORE_TYPE_ID.equal(scoreType.getId())
          )
      ).fetchOne();
      return result == null ? Optional.empty() : Optional.of(result.getValue(HIGH_SCORES.VALUE));
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }

  public static void incrementalUpdate(UUID playerId, ScoreType scoreType, int amt) {
    try (Connection con = SQLHandle.getConnection()) {
      DSLContext create = DSL.using(con);
      create.insertInto(HIGH_SCORES).columns(HIGH_SCORES.PLAYER_ID, HIGH_SCORES.SCORE_TYPE_ID, HIGH_SCORES.VALUE)
          .values(create.select(PLAYERS.ID).from(PLAYERS).where(PLAYERS.UUID.equal(playerId.toString())).fetchOne().value1(), scoreType.getId(), amt)
          .onDuplicateKeyUpdate()
          .set(HIGH_SCORES.VALUE, HIGH_SCORES.VALUE.add(amt))
          .execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void override(UUID playerId, ScoreType scoreType, int value) {
    try (Connection con = SQLHandle.getConnection()) {
      DSLContext create = DSL.using(con);
      create.insertInto(HIGH_SCORES).columns(HIGH_SCORES.PLAYER_ID, HIGH_SCORES.SCORE_TYPE_ID, HIGH_SCORES.VALUE)
          .values(create.select(PLAYERS.ID).from(PLAYERS).where(PLAYERS.UUID.equal(playerId.toString())).fetchOne().value1(), scoreType.getId(), value)
          .onDuplicateKeyUpdate()
          .set(HIGH_SCORES.VALUE, value)
          .execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static void overrideIfBetter(UUID playerId, ScoreType scoreType, int value) {
    Optional<Integer> optExistingValue = get(playerId, scoreType);
    if (!optExistingValue.isPresent()) {
      override(playerId, scoreType, value);
      return;
    }

    int existingValue = optExistingValue.get();
    if (scoreType.getOrder() == ScoreType.Order.DESC) {
      if (value <= existingValue) {
        return;
      }
    } else {
      if (existingValue <= value) {
        return;
      }
    }

    override(playerId, scoreType, value);
  }
}
