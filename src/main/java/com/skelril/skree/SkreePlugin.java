/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.skelril.skree.system.arrowfishing.ArrowFishingSystem;
import com.skelril.skree.system.dropclear.DropClearSystem;
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
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.PreInitializationEvent;
import org.spongepowered.api.event.state.ServerStartedEvent;
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

    @Subscribe
    public void onPreInit(PreInitializationEvent event) {
        customItemSystem = new CustomItemSystem(this, game);
        customItemSystem.preInit();

        customBlockSystem = new CustomBlockSystem(this, game);
        customBlockSystem.preInit();
    }

    @Subscribe
    public void onServerStart(ServerStartedEvent event) {
        registerPrimaryHybridSystems();
        System.out.println(game.getPlatform().getExecutionType());
        switch (game.getPlatform().getExecutionType()) {
            case CLIENT:
                registerPrimaryClientSystems();
                break;
            case SERVER:
                registerPrimaryServerSystems();
                break;
        }

        logger.info("Skree Started! Kaw!");
    }

    private void registerPrimaryHybridSystems() {

    }

    private void registerPrimaryClientSystems() {

    }

    private void registerPrimaryServerSystems() {
        ImmutableList<Class> initialized = ImmutableList.of(
                ArrowFishingSystem.class,
                DropClearSystem.class,
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
