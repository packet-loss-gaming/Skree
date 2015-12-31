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
import com.skelril.skree.system.pvp.PvPSystem;
import com.skelril.skree.system.registry.block.CustomBlockSystem;
import com.skelril.skree.system.registry.item.CustomItemSystem;
import com.skelril.skree.system.shutdown.ShutdownSystem;
import com.skelril.skree.system.teleport.TeleportSystem;
import com.skelril.skree.system.weather.WeatherCommandSystem;
import com.skelril.skree.system.world.WorldSystem;
import com.skelril.skree.system.zone.ZoneSystem;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;

import java.util.logging.Logger;

@Singleton
@Plugin(id = "Skree", name = "Skree", version = "1.0")
public class SkreePlugin {

    @Inject
    private Logger logger;

    public static CustomItemSystem customItemSystem;
    public static CustomBlockSystem customBlockSystem;

    private static SkreePlugin inst;

    public static SkreePlugin inst() {
        return inst;
    }

    public SkreePlugin() {
        inst = this;
    }

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        // Handle the database connection setup very early on
        new DatabaseSystem();

        customItemSystem = new CustomItemSystem();
        customItemSystem.preInit();

        customBlockSystem = new CustomBlockSystem();
        customBlockSystem.preInit();

        customItemSystem.associate();
        customBlockSystem.associate();

        Sponge.getRegistry().registerWorldGeneratorModifier(new VoidWorldGeneratorModifier());
        Sponge.getRegistry().registerWorldGeneratorModifier(new NoOreWorldGeneratorModifier());
        Sponge.getRegistry().registerWorldGeneratorModifier(new WildernessWorldGeneratorModifier());
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        switch (Sponge.getPlatform().getExecutionType()) {
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
                PlayerStateSystem.class,
                ProjectileWatcherSystem.class,
                PvPSystem.class,
                ShutdownSystem.class,
                TeleportSystem.class,
                WorldSystem.class,
                WeatherCommandSystem.class,
                ZoneSystem.class
        );

        for (Class<?> entry : initialized) {
            try {
                entry.newInstance();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
