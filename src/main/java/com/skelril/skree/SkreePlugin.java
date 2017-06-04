/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.skelril.nitro.module.NitroModuleManager;
import com.skelril.skree.system.aid.AidSystem;
import com.skelril.skree.system.antihack.AntiHackSystem;
import com.skelril.skree.system.arrowfishing.ArrowFishingSystem;
import com.skelril.skree.system.database.DatabaseSystem;
import com.skelril.skree.system.dropclear.DropClearSystem;
import com.skelril.skree.system.itemrestriction.ItemRestrictionSystem;
import com.skelril.skree.system.maintenance.MaintenanceSystem;
import com.skelril.skree.system.market.MarketSystem;
import com.skelril.skree.system.mobdensity.MobDensitySystem;
import com.skelril.skree.system.modifier.ModifierSystem;
import com.skelril.skree.system.playerstate.PlayerStateSystem;
import com.skelril.skree.system.projectilewatcher.ProjectileWatcherSystem;
import com.skelril.skree.system.pvp.PvPSystem;
import com.skelril.skree.system.random.RandomSystem;
import com.skelril.skree.system.region.RegionSystem;
import com.skelril.skree.system.registry.CustomRegisterySystem;
import com.skelril.skree.system.respawn.RespawnSystem;
import com.skelril.skree.system.respawnqueue.RespawnQueueSystem;
import com.skelril.skree.system.shutdown.ShutdownSystem;
import com.skelril.skree.system.teleport.TeleportSystem;
import com.skelril.skree.system.tweaker.TweakerSystem;
import com.skelril.skree.system.weather.WeatherCommandSystem;
import com.skelril.skree.system.world.WorldGeneratorSystem;
import com.skelril.skree.system.world.WorldSystem;
import com.skelril.skree.system.zone.ZoneSystem;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStateEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.logging.Logger;

@Singleton
@Plugin(id = "skree", name = "Skree", description = "The Skelril super-mod", version = "1.0")
public class SkreePlugin {

  @Inject
  private Logger logger;

  protected NitroModuleManager manager = new NitroModuleManager();

  private static SkreePlugin inst;

  @Inject
  private PluginContainer container;

  public static PluginContainer container() {
    return inst.container;
  }

  public static SkreePlugin inst() {
    return inst;
  }

  public SkreePlugin() {
    inst = this;
    registerModules();
    manager.discover();
  }

  @Listener
  public void onGameStateChange(GameStateEvent event) {
    manager.trigger(event.getState().toString());
  }

  private void registerModules() {
    ImmutableList<Class> initialized = ImmutableList.of(
        AidSystem.class,
        AntiHackSystem.class,
        ArrowFishingSystem.class,
        CustomRegisterySystem.class,
        DatabaseSystem.class,
        DropClearSystem.class,
        ItemRestrictionSystem.class,
        MaintenanceSystem.class,
        MarketSystem.class,
        MobDensitySystem.class,
        ModifierSystem.class,
        PlayerStateSystem.class,
        ProjectileWatcherSystem.class,
        PvPSystem.class,
        RandomSystem.class,
        RegionSystem.class,
        RespawnSystem.class,
        RespawnQueueSystem.class,
        ShutdownSystem.class,
        TeleportSystem.class,
        TweakerSystem.class,
        WeatherCommandSystem.class,
        WorldGeneratorSystem.class,
        WorldSystem.class,
        ZoneSystem.class
    );

    for (Class<?> entry : initialized) {
      try {
        manager.registerModule(entry.newInstance());
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }
}
