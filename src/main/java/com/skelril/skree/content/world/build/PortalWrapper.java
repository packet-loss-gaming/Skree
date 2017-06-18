/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.world.build;

import com.skelril.skree.db.SQLHandle;
import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.impl.DSL;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static com.skelril.skree.db.schema.Tables.PLAYERS;
import static com.skelril.skree.db.schema.Tables.WORLDS;
import static com.skelril.skree.db.schema.tables.Portals.PORTALS;

class PortalWrapper {

  public static void writePortalUpdate(Player player, Location<World> fromLocation) {
    try (Connection con = SQLHandle.getConnection()) {
      DSLContext create = DSL.using(con);
      create.insertInto(PORTALS).columns(PORTALS.FROM_WORLD_ID, PORTALS.PLAYER_ID, PORTALS.X, PORTALS.Y, PORTALS.Z)
          .values(
              create.select(WORLDS.ID).from(WORLDS).where(WORLDS.NAME.equal(fromLocation.getExtent().getName())).asField(),
              create.select(PLAYERS.ID).from(PLAYERS).where(PLAYERS.UUID.equal(player.getUniqueId().toString())).asField(),
              DSL.val(fromLocation.getBlockX()),
              DSL.val(fromLocation.getBlockY()),
              DSL.val(fromLocation.getBlockZ())
          )
          .onDuplicateKeyUpdate()
          .set(PORTALS.X, fromLocation.getBlockX())
          .set(PORTALS.Y, fromLocation.getBlockY())
          .set(PORTALS.Z, fromLocation.getBlockZ())
          .execute();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  public static Optional<Location<World>> getPreviousPortal(Player player, World world) {
    try (Connection con = SQLHandle.getConnection()) {
      DSLContext create = DSL.using(con);
      Record3<Integer, Integer, Integer> result = create.select(PORTALS.X, PORTALS.Y, PORTALS.Z).from(PORTALS)
          .where(
              PORTALS.FROM_WORLD_ID.equal(create.select(WORLDS.ID).from(WORLDS).where(WORLDS.NAME.equal(world.getName()))).and(
                  PORTALS.PLAYER_ID.equal(create.select(PLAYERS.ID).from(PLAYERS).where(PLAYERS.UUID.equal(player.getUniqueId().toString())))
              )
          )
          .fetchOne();

      return Optional.of(new Location<>(world, result.get(PORTALS.X) + .5, result.get(PORTALS.Y), result.get(PORTALS.Z) + .5));
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }
}
