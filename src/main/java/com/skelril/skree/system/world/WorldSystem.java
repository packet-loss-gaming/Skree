/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.world;


import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.world.NoOreWorldGeneratorModifier;
import com.skelril.skree.content.world.VoidWorldGeneratorModifier;
import com.skelril.skree.content.world.WorldCommand;
import com.skelril.skree.content.world.WorldListCommand;
import com.skelril.skree.content.world.build.BuildWorldWrapper;
import com.skelril.skree.content.world.instance.InstanceWorldWrapper;
import com.skelril.skree.content.world.main.MainWorldWrapper;
import com.skelril.skree.content.world.wilderness.WildernessWorldGeneratorModifier;
import com.skelril.skree.content.world.wilderness.WildernessWorldWrapper;
import com.skelril.skree.service.WorldService;
import com.skelril.skree.service.internal.world.WorldServiceImpl;
import com.skelril.skree.system.ServiceProvider;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.GeneratorTypes;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldCreationSettings;

import java.util.Optional;
import java.util.Random;

public class WorldSystem implements ServiceProvider<WorldService> {

    private static final String MAIN = "Main";
    private static final String BUILD = "Sion";
    private static final String INSTANCE = "Instance";
    private static final String WILDERNESS = "Wilderness";
    private static final String WILDERNESS_NETHER = "Wilderness_nether";

    private Random randy = new Random();

    private WorldService service;

    public WorldSystem() {
        service = new WorldServiceImpl();

        // Register the service & command
        Sponge.getServiceManager().setProvider(SkreePlugin.inst(), WorldService.class, service);
        Sponge.getCommandManager().register(SkreePlugin.inst(), WorldCommand.aquireSpec(), "world");
        Sponge.getCommandManager().register(SkreePlugin.inst(), WorldListCommand.aquireSpec(), "worlds");

        // Handle main world
        initMain();

        // Create worlds
        initBuild();
        initInstance();
        initWilderness();
    }

    private void initMain() {
        // Main World
        MainWorldWrapper wrapper = new MainWorldWrapper();

        Optional<World> curWorld = Sponge.getServer().getWorld(MAIN);

        if (curWorld.isPresent()) {
            wrapper.addWorld(curWorld.get());
        }

        // Main wrapper reg
        Sponge.getEventManager().registerListeners(SkreePlugin.inst(), wrapper);
        service.registerEffectWrapper(wrapper);
    }

    private void initBuild() {
        // Build World
        BuildWorldWrapper wrapper = new BuildWorldWrapper();

        Optional<World> curWorld = Sponge.getServer().getWorld(BUILD);
        if (!curWorld.isPresent()) {
            curWorld = instantiate(
                    obtainOverworld().name(BUILD).seed(randy.nextLong()).usesMapFeatures(false)
                            .generatorModifiers(new NoOreWorldGeneratorModifier()).build()
            );
        }

        if (curWorld.isPresent()) {
            wrapper.addWorld(curWorld.get());
        }

        // Build wrapper reg
        Sponge.getEventManager().registerListeners(SkreePlugin.inst(), wrapper);
        service.registerEffectWrapper(wrapper);
    }

    private void initInstance() {
        // Instance World
        InstanceWorldWrapper wrapper = new InstanceWorldWrapper();

        Optional<World> curWorld = Sponge.getServer().getWorld(INSTANCE);
        if (!curWorld.isPresent()) {
            curWorld = instantiate(
                    obtainFlatworld().name(INSTANCE).seed(randy.nextLong()).usesMapFeatures(false)
                            .generatorModifiers(new VoidWorldGeneratorModifier()).build()
            );
        }

        if (curWorld.isPresent()) {
            wrapper.addWorld(curWorld.get());
        }

        // Instance wrapper reg
        Sponge.getEventManager().registerListeners(SkreePlugin.inst(), wrapper);
        service.registerEffectWrapper(wrapper);
    }

    private void initWilderness() {
        // Wilderness World
        WildernessWorldWrapper wrapper = new WildernessWorldWrapper();

        Optional<World> curWorld = Sponge.getServer().getWorld(WILDERNESS);
        if (!curWorld.isPresent()) {
            curWorld = instantiate(
                    obtainOverworld().name(WILDERNESS).seed(randy.nextLong()).usesMapFeatures(true)
                            .generatorModifiers(new WildernessWorldGeneratorModifier()).build()
            );
        }

        if (curWorld.isPresent()) {
            wrapper.addWorld(curWorld.get());
        }

        // Wilderness Nether World
        curWorld = Sponge.getServer().getWorld(WILDERNESS_NETHER);
        if (!curWorld.isPresent()) {
            curWorld = instantiate(
                    obtainNetherworld().name(WILDERNESS_NETHER).seed(randy.nextLong()).usesMapFeatures(true).build()
            );
        }

        if (curWorld.isPresent()) {
            wrapper.addWorld(curWorld.get());
        }

        // Wilderness wrapper reg
        Sponge.getEventManager().registerListeners(SkreePlugin.inst(), wrapper);
        service.registerEffectWrapper(wrapper);
    }

    private WorldCreationSettings.Builder obtainOverworld() {
        return obtainAutoloadingWorld().dimension(DimensionTypes.OVERWORLD).generator(GeneratorTypes.OVERWORLD);
    }

    private WorldCreationSettings.Builder obtainFlatworld() {
        return obtainAutoloadingWorld().dimension(DimensionTypes.OVERWORLD).generator(GeneratorTypes.FLAT);
    }

    public WorldCreationSettings.Builder obtainNetherworld() {
        return obtainAutoloadingWorld().dimension(DimensionTypes.NETHER).generator(GeneratorTypes.NETHER);
    }

    private WorldCreationSettings.Builder obtainAutoloadingWorld() {
        WorldCreationSettings.Builder builder = WorldCreationSettings.builder();
        return builder.enabled(true).loadsOnStartup(true);
    }

    private Optional<World> instantiate(WorldCreationSettings settings) {
        try {
            return Sponge.getServer().loadWorld(Sponge.getServer().createWorldProperties(settings).get());
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    @Override
    public WorldService getService() {
        return service;
    }
}
