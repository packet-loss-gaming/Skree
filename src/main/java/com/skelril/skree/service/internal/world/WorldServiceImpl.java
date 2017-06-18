/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.world;

import com.skelril.skree.content.world.main.MainWorldWrapper;
import com.skelril.skree.db.SQLHandle;
import com.skelril.skree.service.WorldService;
import com.skelril.skree.system.world.ServerSideWorldRegistar;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.impl.DSL;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldArchetype;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

import static com.skelril.skree.db.schema.Tables.PLAYERS;
import static com.skelril.skree.db.schema.Tables.WORLDS;

public class WorldServiceImpl implements WorldService {

  private HashMap<Class<? extends WorldEffectWrapper>, WorldEffectWrapper> worlds = new HashMap<>();

  @Override
  public void registerEffectWrapper(WorldEffectWrapper wrapper) {
    worlds.put(wrapper.getClass(), wrapper);
  }

  @Override
  public <T extends WorldEffectWrapper> Optional<T> getEffectWrapper(Class<T> wrapperClass) {
    WorldEffectWrapper wrapper = worlds.get(wrapperClass);
    //noinspection unchecked
    return Optional.ofNullable(wrapper != null ? (T) wrapper : null);
  }

  @Override
  public Optional<WorldEffectWrapper> getEffectWrapperFor(World world) {
    for (WorldEffectWrapper wrapper : worlds.values()) {
      for (World worldEntry : wrapper.getWorlds()) {
        if (world.equals(worldEntry)) {
          return Optional.of(wrapper);
        }
      }
    }
    return Optional.empty();
  }

  @Override
  public Collection<WorldEffectWrapper> getEffectWrappers() {
    return new HashSet<>(worlds.values());
  }

  @Override
  public Optional<World> loadWorld(String name, WorldArchetype archetype) {
    try {
      return Sponge.getServer().loadWorld(Sponge.getServer().createWorldProperties(name, archetype));
    } catch (IOException e) {
      return Optional.empty();
    }
  }

  @Override
  public Optional<World> loadVanillaMapFromDisk(String name) {
    return Sponge.getServer().loadWorld(name);
  }

  @Override
  public void registerWorld(String name) {
    if (Sponge.getPlatform().getType() == Platform.Type.SERVER) {
      new ServerSideWorldRegistar().register(name);
    }
  }

  @Override
  public Location<World> getWorldEntryPoint(Player player, World world) {
    Optional<WorldEffectWrapper> optWrapper = getEffectWrapperFor(world);
    return optWrapper.map(wrapper -> wrapper.getPlayerEntryPoint(player, world)).orElseGet(world::getSpawnLocation);
  }

  private Map<UUID, Long> lastPlayerLogin = new HashMap<>();

  @Listener(order = Order.PRE)
  public void onPlayerAuth(ClientConnectionEvent.Auth event) {
    try (Connection con = SQLHandle.getConnection()) {
      DSLContext create = DSL.using(con);

      UUID uuid = event.getProfile().getUniqueId();

      Record1<Timestamp> result = create.select(PLAYERS.LAST_LOGIN).from(PLAYERS).where(PLAYERS.UUID.equal(uuid.toString())).fetchOne();
      Timestamp timestamp = result.getValue(PLAYERS.LAST_LOGIN);

      lastPlayerLogin.put(uuid, timestamp.getTime());
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Listener
  public void onPlayerJoin(ClientConnectionEvent.Join event) {
    try (Connection con = SQLHandle.getConnection()) {
      DSLContext create = DSL.using(con);

      Player player = event.getTargetEntity();
      UUID uuid = player.getUniqueId();
      World world = player.getWorld();

      Record1<Timestamp> result = create.select(WORLDS.CREATED_AT).from(WORLDS).where(WORLDS.NAME.equal(world.getName())).fetchOne();
      Timestamp worldCreationTimestamp = result.getValue(WORLDS.CREATED_AT);

      long worldCreationTime = 0;
      if (worldCreationTimestamp != null) {
        worldCreationTime = worldCreationTimestamp.getTime();
      }

      if (worldCreationTime > lastPlayerLogin.remove(uuid)) {
        Location<World> spawn = getEffectWrapper(MainWorldWrapper.class).get().getPrimaryWorld().getSpawnLocation();
        player.setLocation(spawn);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Listener
  public void onPlayerQuit(ClientConnectionEvent.Disconnect event) {
    lastPlayerLogin.remove(event.getTargetEntity().getUniqueId());
  }
}
