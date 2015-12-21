/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.skelril.skree.content.world.NoOreWorldGeneratorModifier;
import com.skelril.skree.content.world.VoidWorldGeneratorModifier;
import com.skelril.skree.content.world.wilderness.WildernessWorldGeneratorModifier;
import com.skelril.skree.system.arrowfishing.ArrowFishingSystem;
import com.skelril.skree.system.database.DatabaseSystem;
import com.skelril.skree.system.dropclear.DropClearSystem;
import com.skelril.skree.system.market.MarketSystem;
import com.skelril.skree.system.modifier.ModifierSystem;
import com.skelril.skree.system.playerstate.PlayerStateSystem;
import com.skelril.skree.system.projectilewatcher.ProjectileWatcherSystem;
import com.skelril.skree.system.registry.block.CustomBlockSystem;
import com.skelril.skree.system.registry.item.CustomItemSystem;
import com.skelril.skree.system.shutdown.ShutdownSystem;
import com.skelril.skree.system.teleport.TeleportSystem;
import com.skelril.skree.system.weather.WeatherCommandSystem;
import com.skelril.skree.system.world.WorldSystem;
import com.skelril.skree.system.zone.ZoneSystem;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.util.logging.Logger;

@Singleton
@Plugin(id = "Skree", name = "Skree", version = "1.0")
public class SkreePlugin {

    @Inject
    private Game game;

    @Inject
    private Logger logger;

    public static CustomItemSystem customItemSystem;
    public static CustomBlockSystem customBlockSystem;

    private static SkreePlugin inst;

    public static SkreePlugin inst() {
        return inst;
    }

    public Game getGame() {
        return game;
    }

    public SkreePlugin() {
        inst = this;
    }

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        // Handle the database connection setup very early on
        new DatabaseSystem(this, game);

        customItemSystem = new CustomItemSystem(this, game);
        customItemSystem.preInit();

        customBlockSystem = new CustomBlockSystem(this, game);
        customBlockSystem.preInit();

        game.getRegistry().registerWorldGeneratorModifier(new VoidWorldGeneratorModifier());
        game.getRegistry().registerWorldGeneratorModifier(new NoOreWorldGeneratorModifier());
        game.getRegistry().registerWorldGeneratorModifier(new WildernessWorldGeneratorModifier());
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        switch (game.getPlatform().getExecutionType()) {
            case SERVER:
                registerPrimaryServerSystems();
                break;
        }

        logger.info("Skree Started! Kaw!");
    }

    private void registerPrimaryServerSystems() {
        ImmutableList<Class> initialized = ImmutableList.of(
                ArrowFishingSystem.class,
                DropClearSystem.class,
                MarketSystem.class,
                ModifierSystem.class,
                ProjectileWatcherSystem.class,
                PlayerStateSystem.class,
                ShutdownSystem.class,
                TeleportSystem.class,
                WorldSystem.class,
                WeatherCommandSystem.class,
                ZoneSystem.class
        );

        for (Class<?> entry : initialized) {
            try {
                Constructor<?> constructor = entry.getConstructor(SkreePlugin.class, Game.class);
                constructor.newInstance(this, game);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
