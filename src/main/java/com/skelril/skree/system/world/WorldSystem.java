/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.world;


import com.skelril.nitro.module.NModule;
import com.skelril.nitro.module.NModuleTrigger;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.world.*;
import com.skelril.skree.content.world.build.BuildWorldWrapper;
import com.skelril.skree.content.world.instance.InstanceWorldWrapper;
import com.skelril.skree.content.world.main.MainWorldWrapper;
import com.skelril.skree.content.world.wilderness.WildernessMetaCommand;
import com.skelril.skree.content.world.wilderness.WildernessTeleportCommand;
import com.skelril.skree.content.world.wilderness.WildernessWorldGeneratorModifier;
import com.skelril.skree.content.world.wilderness.WildernessWorldWrapper;
import com.skelril.skree.service.WorldService;
import com.skelril.skree.service.internal.world.WorldServiceImpl;
import com.skelril.skree.system.ServiceProvider;
import org.spongepowered.api.Platform;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.GeneratorTypes;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldArchetype;

import java.util.Optional;

@NModule(name = "World System")
public class WorldSystem implements ServiceProvider<WorldService> {

    private static final String MAIN = "Main";
    private static final String BUILD = "Kalazben";
    private static final String BUILD_OLD = "Sion";
    private static final String INSTANCE = "Instance";
    private static final String WILDERNESS = "Wilderness";
    private static final String WILDERNESS_NETHER = "Wilderness_nether";

    private WorldService service;

    @NModuleTrigger(trigger = "SERVER_STARTED")
    public void init() {
        service = new WorldServiceImpl();

        // Register the service & command
        Sponge.getEventManager().registerListeners(SkreePlugin.inst(), service);
        Sponge.getServiceManager().setProvider(SkreePlugin.inst(), WorldService.class, service);
        Sponge.getCommandManager().register(SkreePlugin.inst(), SetSpawnCommand.aquireSpec(), "setspawn");
        Sponge.getCommandManager().register(SkreePlugin.inst(), WorldCommand.aquireSpec(), "world");
        Sponge.getCommandManager().register(SkreePlugin.inst(), WorldListCommand.aquireSpec(), "worlds");
        Sponge.getCommandManager().register(SkreePlugin.inst(), WildernessMetaCommand.aquireSpec(), "wmeta");
        Sponge.getCommandManager().register(SkreePlugin.inst(), WildernessTeleportCommand.aquireSpec(), "wtp");

        initArchetypes();

        // Handle main world
        initMain();

        // initCity();

        // Create worlds
        initBuild();
        initInstance();
        initWilderness();
    }

    private void initArchetypes() {
        Optional<WorldArchetype> optCityArchetype = Sponge.getGame().getRegistry().getType(WorldArchetype.class, "skree:city");
        if (!optCityArchetype.isPresent()) {
            obtainOverworld().usesMapFeatures(false).generatorModifiers(new NoOreWorldGeneratorModifier()).build("skree:city", "city");
        }
        Optional<WorldArchetype> optBuildArchetype = Sponge.getGame().getRegistry().getType(WorldArchetype.class, "skree:build");
        if (!optBuildArchetype.isPresent()) {
            obtainOverworld().usesMapFeatures(false)
                    .generatorModifiers(new NoOreWorldGeneratorModifier()).build("skree:build", "build");
        }
        Optional<WorldArchetype> optBarrierArchetype = Sponge.getGame().getRegistry().getType(WorldArchetype.class, "skree:barrier");
        if (!optBarrierArchetype.isPresent()) {
            obtainFlatworld().usesMapFeatures(false)
                    .generatorModifiers(new BarrierWorldGeneratorModifier()).build("skree:barrier", "barrier");
        }
        Optional<WorldArchetype> optVoidArchetype = Sponge.getGame().getRegistry().getType(WorldArchetype.class, "skree:void");
        if (!optVoidArchetype.isPresent()) {
            obtainFlatworld().usesMapFeatures(false)
                    .generatorModifiers(new VoidWorldGeneratorModifier()).build("skree:void", "void");
        }
        Optional<WorldArchetype> optWildArchetype = Sponge.getGame().getRegistry().getType(WorldArchetype.class, "skree:wilderness");
        if (!optWildArchetype.isPresent()) {
            obtainOverworld().usesMapFeatures(true)
                    .generatorModifiers(new WildernessWorldGeneratorModifier()).build("skree:wilderness", "wilderness");
        }
        Optional<WorldArchetype> optWildNetherArchetype = Sponge.getGame().getRegistry().getType(WorldArchetype.class, "skree:wilderness_nether");
        if (!optWildNetherArchetype.isPresent()) {
            obtainNetherworld().usesMapFeatures(true).build("skree:wilderness_nether", "wilderness_nether");
        }
    }

    private void initCity() {
        // City World
        MainWorldWrapper wrapper = new MainWorldWrapper();

        Optional<World> curWorld = Sponge.getServer().getWorld("City");
        if (!curWorld.isPresent()) {
            curWorld = instantiate(
                    "City",
                    Sponge.getGame().getRegistry().getType(WorldArchetype.class, "skree:city").get()
            );
            // registerWorld("City");
        }

        if (curWorld.isPresent()) {
            wrapper.addWorld(curWorld.get());
        }

        Sponge.getEventManager().registerListeners(SkreePlugin.inst(), wrapper);
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
                    BUILD,
                    Sponge.getGame().getRegistry().getType(WorldArchetype.class, "skree:build").get()
            );
            registerWorld(BUILD);
        }

        if (curWorld.isPresent()) {
            wrapper.addWorld(curWorld.get());
        }

        Optional<World> oldWorld = Sponge.getServer().getWorld(BUILD_OLD);
        if (!oldWorld.isPresent()) {
            oldWorld = instantiate(
                    BUILD_OLD,
                    Sponge.getGame().getRegistry().getType(WorldArchetype.class, "skree:barrier").get()
            );
            registerWorld(BUILD_OLD);
        }

        if (oldWorld.isPresent()) {
            wrapper.addWorld(oldWorld.get());
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
                    INSTANCE,
                    Sponge.getGame().getRegistry().getType(WorldArchetype.class, "skree:void").get()
            );
            registerWorld(INSTANCE);
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
                    WILDERNESS,
                    Sponge.getGame().getRegistry().getType(WorldArchetype.class, "skree:wilderness").get()
            );
            registerWorld(WILDERNESS);
        }

        if (curWorld.isPresent()) {
            wrapper.addWorld(curWorld.get());
        }

        // Wilderness Nether World
        curWorld = Sponge.getServer().getWorld(WILDERNESS_NETHER);
        if (!curWorld.isPresent()) {
            curWorld = instantiate(
                    WILDERNESS_NETHER,
                    Sponge.getGame().getRegistry().getType(WorldArchetype.class, "skree:wilderness_nether").get()
            );
            registerWorld(WILDERNESS_NETHER);
        }

        if (curWorld.isPresent()) {
            wrapper.addWorld(curWorld.get());
        }

        // Wilderness wrapper reg
        Sponge.getEventManager().registerListeners(SkreePlugin.inst(), wrapper);
        service.registerEffectWrapper(wrapper);
    }

    private WorldArchetype.Builder obtainOverworld() {
        return obtainAutoloadingWorld().dimension(DimensionTypes.OVERWORLD).generator(GeneratorTypes.LARGE_BIOMES);
    }

    private WorldArchetype.Builder obtainFlatworld() {
        return obtainAutoloadingWorld().dimension(DimensionTypes.OVERWORLD).generator(GeneratorTypes.FLAT);
    }

    public WorldArchetype.Builder obtainNetherworld() {
        return obtainAutoloadingWorld().dimension(DimensionTypes.NETHER).generator(GeneratorTypes.NETHER);
    }

    private WorldArchetype.Builder obtainAutoloadingWorld() {
        WorldArchetype.Builder builder = WorldArchetype.builder();
        return builder.enabled(true).loadsOnStartup(true);
    }

    private Optional<World> instantiate(String name, WorldArchetype settings) {
        try {
            return Sponge.getServer().loadWorld(Sponge.getServer().createWorldProperties(name, settings));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    private void registerWorld(String worldName) {
        if (Sponge.getPlatform().getType() == Platform.Type.SERVER) {
            new ServerSideWorldRegistar().register(worldName);
        }
    }

    @Override
    public WorldService getService() {
        return service;
    }
}
