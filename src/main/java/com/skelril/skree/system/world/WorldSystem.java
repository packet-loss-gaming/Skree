/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.world;


import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.skelril.nitro.JarResourceLoader;
import com.skelril.nitro.module.NModule;
import com.skelril.nitro.module.NModuleTrigger;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.world.LoadWorldCommand;
import com.skelril.skree.content.world.SetSpawnCommand;
import com.skelril.skree.content.world.WorldCommand;
import com.skelril.skree.content.world.WorldListCommand;
import com.skelril.skree.content.world.build.BuildWorldWrapper;
import com.skelril.skree.content.world.instance.InstanceWorldWrapper;
import com.skelril.skree.content.world.main.MainWorldWrapper;
import com.skelril.skree.content.world.wilderness.*;
import com.skelril.skree.service.WorldService;
import com.skelril.skree.service.internal.world.WorldEffectWrapper;
import com.skelril.skree.service.internal.world.WorldServiceImpl;
import com.skelril.skree.system.ServiceProvider;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigManager;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldArchetype;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;

@NModule(name = "World System")
public class WorldSystem implements ServiceProvider<WorldService> {
    private Map<String, WorldEffectWrapper> wrappers = new HashMap<>();
    private WorldService service;
    private WorldSystemConfig config;

    private Path getWorldConfiguration() throws IOException {
        ConfigManager service = Sponge.getGame().getConfigManager();
        Path path = service.getPluginConfig(SkreePlugin.inst()).getDirectory();
        return path.resolve("worlds.json");
    }

    private void loadConfiguration() {
        // Insert ugly configuration code
        try {
            Path targetFile = getWorldConfiguration();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            if (!Files.exists(targetFile)) {
                new JarResourceLoader("/defaults/").loadFromResources((getResource) -> {
                    try {
                        Files.copy(getResource.apply("worlds.json"), targetFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            try (BufferedReader reader = Files.newBufferedReader(targetFile)) {
                config = gson.fromJson(reader, WorldSystemConfig.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path getWildernessConfiguration() throws IOException {
        ConfigManager service = Sponge.getGame().getConfigManager();
        Path path = service.getPluginConfig(SkreePlugin.inst()).getDirectory();
        return path.resolve("wilderness.json");
    }

    private WildernessConfig loadWildernessConfiguration() {
        // Insert ugly configuration code
        try {
            Path targetFile = getWildernessConfiguration();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            if (!Files.exists(targetFile)) {
                new JarResourceLoader("/defaults/").loadFromResources((getResource) -> {
                    try {
                        Files.copy(getResource.apply("wilderness.json"), targetFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            try (BufferedReader reader = Files.newBufferedReader(targetFile)) {
                return gson.fromJson(reader, WildernessConfig.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @NModuleTrigger(trigger = "SERVER_STARTED")
    public void init() {
        service = new WorldServiceImpl();

        // Register the service & command
        Sponge.getEventManager().registerListeners(SkreePlugin.inst(), service);
        Sponge.getServiceManager().setProvider(SkreePlugin.inst(), WorldService.class, service);
        Sponge.getCommandManager().register(SkreePlugin.inst(), LoadWorldCommand.aquireSpec(), "loadworld");
        Sponge.getCommandManager().register(SkreePlugin.inst(), SetSpawnCommand.aquireSpec(), "setspawn");
        Sponge.getCommandManager().register(SkreePlugin.inst(), WorldCommand.aquireSpec(), "world");
        Sponge.getCommandManager().register(SkreePlugin.inst(), WorldListCommand.aquireSpec(), "worlds");
        Sponge.getCommandManager().register(SkreePlugin.inst(), WildernessMetaCommand.aquireSpec(), "wmeta");
        Sponge.getCommandManager().register(SkreePlugin.inst(), SummonWandererCommand.aquireSpec(), "wanderer");
        Sponge.getCommandManager().register(SkreePlugin.inst(), WildernessTeleportCommand.aquireSpec(), "wtp");

        loadConfiguration();

        initArchetypes();
        initWrappers();
        initWorlds();
    }

    private void buildArchetype(ArchetypeConfig archetypeConfig) throws Throwable {
        GameRegistry registry = Sponge.getRegistry();
        Optional<WorldArchetype> optTargetArchetype = registry.getType(WorldArchetype.class, archetypeConfig.getId());
        if (optTargetArchetype.isPresent()) {
            return;
        }

        WorldArchetype.Builder archeTypeBuilder = obtainAutoloadingWorld();

        String dimensionName = archetypeConfig.getDimension();
        DimensionType dimension = registry.getType(DimensionType.class, dimensionName).orElseThrow((Supplier<Throwable>) () -> {
            return new RuntimeException("No dimension type: " + dimensionName);
        });
        archeTypeBuilder.dimension(dimension);

        String generatorName = archetypeConfig.getGenerator();
        GeneratorType generator = registry.getType(GeneratorType.class, generatorName).orElseThrow((Supplier<Throwable>) () -> {
            return new RuntimeException("No generator type: " + generatorName);
        });
        archeTypeBuilder.generator(generator);

        boolean usesMapFeatures = archetypeConfig.usesMapFeatures();
        archeTypeBuilder.usesMapFeatures(usesMapFeatures);

        List<WorldGeneratorModifier> modifiers = new ArrayList<>();
        for (String modifierId : archetypeConfig.getModifiers()) {
            modifiers.add(registry.getType(WorldGeneratorModifier.class, modifierId).orElseThrow((Supplier<Throwable>) () -> {
                return new RuntimeException("No world generator modifier: " + modifierId);
            }));
        }

        archeTypeBuilder.generatorModifiers(modifiers.toArray(new WorldGeneratorModifier[modifiers.size()]));

        archeTypeBuilder.build(archetypeConfig.getId(), archetypeConfig.getName());
    }

    private void initArchetypes() {
        for (ArchetypeConfig archetypeConfig : config.getArchetypes()) {
            try {
                buildArchetype(archetypeConfig);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    private void initWrappers() {
        List<WorldEffectWrapper> wrappers = Lists.newArrayList(
                new MainWorldWrapper(),
                new BuildWorldWrapper(),
                new InstanceWorldWrapper(),
                new WildernessWorldWrapper(loadWildernessConfiguration())
        );

        for (WorldEffectWrapper wrapper : wrappers) {
            this.wrappers.put(wrapper.getName(), wrapper);
            Sponge.getEventManager().registerListeners(SkreePlugin.inst(), wrapper);
            service.registerEffectWrapper(wrapper);
        }
    }

    private World getOrCreateWorld(WorldConfig worldConfig) throws Throwable {
        String worldName = worldConfig.getName();
        Optional<World> optTargetWorld = Sponge.getServer().getWorld(worldName);
        if (optTargetWorld.isPresent()) {
            return optTargetWorld.get();
        }

        GameRegistry registry = Sponge.getRegistry();
        String archetypeName = worldConfig.getArchetype();
        WorldArchetype archetype = registry.getType(WorldArchetype.class, archetypeName).orElseThrow((Supplier<Throwable>) () -> {
            return new RuntimeException("No world archetype: " + archetypeName);
        });
        optTargetWorld = service.loadWorld(worldName, archetype);
        return optTargetWorld.get();
    }

    private void loadWorld(WorldConfig worldConfig) throws Throwable {
        World world = getOrCreateWorld(worldConfig);

        String targetWrapper = worldConfig.getWrapper();
        if (targetWrapper == null || targetWrapper.isEmpty()) {
            return;
        }

        wrappers.get(targetWrapper).addWorld(world);
    }

    private void initWorlds() {
        for (WorldConfig worldConfig : config.getWorlds()) {
            try {
                loadWorld(worldConfig);
            } catch(Throwable t) {
                t.printStackTrace();
            }
        }
    }

    private WorldArchetype.Builder obtainAutoloadingWorld() {
        WorldArchetype.Builder builder = WorldArchetype.builder();
        return builder.enabled(true).loadsOnStartup(true);
    }

    @Override
    public WorldService getService() {
        return service;
    }
}
