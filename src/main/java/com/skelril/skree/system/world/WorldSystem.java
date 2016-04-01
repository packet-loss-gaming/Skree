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
import com.skelril.skree.db.SQLHandle;
import com.skelril.skree.service.WorldService;
import com.skelril.skree.service.internal.world.WorldServiceImpl;
import com.skelril.skree.system.ServiceProvider;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.GeneratorTypes;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldCreationSettings;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.Random;

import static com.skelril.skree.db.schema.Tables.WORLDS;

@NModule(name = "World System")
public class WorldSystem implements ServiceProvider<WorldService> {

    private static final String MAIN = "Main";
    private static final String BUILD = "Sion";
    private static final String INSTANCE = "Instance";
    private static final String WILDERNESS = "Wilderness";
    private static final String WILDERNESS_NETHER = "Wilderness_nether";

    private Random randy = new Random();

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
            registerWorld(BUILD);
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
                            .generatorModifiers(new SolidWorldGeneratorModifier()).build()
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
                    obtainOverworld().name(WILDERNESS).seed(randy.nextLong()).usesMapFeatures(true)
                            .generatorModifiers(new WildernessWorldGeneratorModifier()).build()
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
                    obtainNetherworld().name(WILDERNESS_NETHER).seed(randy.nextLong()).usesMapFeatures(true).build()
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

    private void registerWorld(String name) {
        try (Connection con = SQLHandle.getConnection()) {
            DSLContext create = DSL.using(con);
            create.insertInto(WORLDS).columns(WORLDS.NAME)
                    .values(name)
                    .onDuplicateKeyUpdate().set(WORLDS.CREATED_AT, new Timestamp(System.currentTimeMillis()))
                    .execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public WorldService getService() {
        return service;
    }
}
