/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.world;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.world.build.BuildWorldWrapper;
import com.skelril.skree.content.world.main.MainWorldWrapper;
import com.skelril.skree.content.world.wilderness.WildernessWorldWrapper;
import com.skelril.skree.service.WorldService;
import com.skelril.skree.service.internal.world.WorldCommand;
import com.skelril.skree.service.internal.world.WorldServiceImpl;
import org.spongepowered.api.Game;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.GeneratorTypes;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldBuilder;

import java.util.Random;

public class WorldSystem {

    private static final String MAIN = "Main";
    private static final String BUILD = "Sion";
    private static final String INSTANCE = "Instance";
    private static final String WILDERNESS = "Wilderness";
    private static final String WILDERNESS_NETHER = "Wilderness_nether";

    private Random randy = new Random();

    private WorldService service;

    @Inject
    public WorldSystem(SkreePlugin plugin, Game game) {
        service = new WorldServiceImpl();

        // Handle main world
        initMain(plugin, game);

        // Create worlds
        initBuild(plugin, game);
        initInstance(plugin, game);
        initWilderness(plugin, game);

        // Command reg
        game.getCommandDispatcher().register(plugin, WorldCommand.aquireSpec(game), "world");
    }

    private void initMain(SkreePlugin plugin, Game game) {
        // Main World
        MainWorldWrapper wrapper = new MainWorldWrapper(plugin, game);

        Optional<World> curWorld = game.getServer().getWorld(MAIN);

        if (curWorld.isPresent()) {
            wrapper.addWorld(curWorld.get());
        }

        // Main wrapper reg
        game.getEventManager().register(plugin, wrapper);
        service.registerEffectWrapper(wrapper);
    }

    private void initBuild(SkreePlugin plugin, Game game) {
        // Build World
        BuildWorldWrapper wrapper = new BuildWorldWrapper(plugin, game);

        Optional<World> curWorld = game.getServer().getWorld(BUILD);
        if (!curWorld.isPresent()) {
            curWorld = obtainOverworld(game).name(BUILD).seed(randy.nextLong()).usesMapFeatures(false).build();
        }

        if (curWorld.isPresent()) {
            wrapper.addWorld(curWorld.get());
        }

        // Build wrapper reg
        game.getEventManager().register(plugin, wrapper);
        service.registerEffectWrapper(wrapper);
    }

    private void initInstance(SkreePlugin plugin, Game game) {
        // Instance World
        Optional<World> curWorld = game.getServer().getWorld(INSTANCE);
        if (!curWorld.isPresent()) {
            curWorld = obtainFlatworld(game).name(INSTANCE).seed(randy.nextLong()).usesMapFeatures(false).build();
        }

        if (curWorld.isPresent()) {
            // TODO Instance world wrapper
        }
    }

    private void initWilderness(SkreePlugin plugin, Game game) {
        // Wilderness World
        WildernessWorldWrapper wrapper = new WildernessWorldWrapper(plugin, game);

        Optional<World> curWorld = game.getServer().getWorld(WILDERNESS);
        if (!curWorld.isPresent()) {
            curWorld = obtainOverworld(game).name(WILDERNESS).seed(randy.nextLong()).usesMapFeatures(true).build();
        }

        if (curWorld.isPresent()) {
            wrapper.addWorld(curWorld.get());
        }

        // Wilderness Nether World
        curWorld = game.getServer().getWorld(WILDERNESS_NETHER);
        if (!curWorld.isPresent()) {
            curWorld = obtainNetherworld(game).name(WILDERNESS_NETHER).seed(randy.nextLong()).usesMapFeatures(true).build();
        }

        if (curWorld.isPresent()) {
            wrapper.addWorld(curWorld.get());
        }

        // Wilderness wrapper reg
        game.getEventManager().register(plugin, wrapper);
        service.registerEffectWrapper(wrapper);
    }

    private WorldBuilder obtainOverworld(Game game) {
        return obtainAutoloadingWorld(game).dimensionType(DimensionTypes.OVERWORLD).generator(GeneratorTypes.OVERWORLD);
    }

    private WorldBuilder obtainFlatworld(Game game) {
        return obtainAutoloadingWorld(game).dimensionType(DimensionTypes.OVERWORLD).generator(GeneratorTypes.FLAT);
    }

    public WorldBuilder obtainNetherworld(Game game) {
        return obtainAutoloadingWorld(game).dimensionType(DimensionTypes.NETHER).generator(GeneratorTypes.NETHER);
    }

    private WorldBuilder obtainAutoloadingWorld(Game game) {
        WorldBuilder builder = game.getRegistry().getWorldBuilder().reset();
        return builder.enabled(true).loadsOnStartup(true);
    }

}
