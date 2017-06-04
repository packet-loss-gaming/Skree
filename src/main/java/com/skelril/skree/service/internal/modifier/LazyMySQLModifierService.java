/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.modifier;

import com.skelril.skree.db.SQLHandle;
import org.jooq.Result;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

import static com.skelril.skree.db.schema.tables.Modifiers.MODIFIERS;

public class LazyMySQLModifierService extends ModifierServiceImpl {

  @Override
  protected void repopulateData() {
    try {
      try (Connection con = SQLHandle.getConnection()) {
        Result<?> results = DSL.using(con)
            .select(MODIFIERS.NAME, MODIFIERS.EXPIREY)
            .from(MODIFIERS)
            .fetch();
        results.forEach(a -> setExpiry(a.getValue(MODIFIERS.NAME), a.getValue(MODIFIERS.EXPIREY).getTime()));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void updateModifier(String modifier, long time) {
    try {
      try (Connection con = SQLHandle.getConnection()) {
        Timestamp newExpirey = new Timestamp(time);
        DSL.using(con)
            .insertInto(MODIFIERS)
            .columns(MODIFIERS.NAME, MODIFIERS.EXPIREY)
            .values(modifier, newExpirey)
            .onDuplicateKeyUpdate()
            .set(MODIFIERS.EXPIREY, newExpirey)
            .execute();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
