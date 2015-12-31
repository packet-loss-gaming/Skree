/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.zone;


import com.sk89q.worldedit.WorldEdit;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.zone.ZoneMeCommand;
import com.skelril.skree.content.zone.global.anexample.AnExampleManager;
import com.skelril.skree.content.zone.group.example.ExampleManager;
import com.skelril.skree.content.zone.group.shnugglesprime.ShnugglesPrimeManager;
import com.skelril.skree.service.WorldService;
import com.skelril.skree.service.ZoneService;
import com.skelril.skree.service.internal.zone.WorldResolver;
import com.skelril.skree.service.internal.zone.ZoneServiceImpl;
import com.skelril.skree.service.internal.zone.allocator.ChainPlacementAllocator;
import com.skelril.skree.system.ServiceProvider;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigManager;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.ProviderExistsException;
import org.spongepowered.api.world.World;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class ZoneSystem implements ServiceProvider<ZoneService> {

    private ZoneService service;

    public ZoneSystem() {
        Task.builder().delay(3, TimeUnit.SECONDS).execute(
                () -> {
                    System.out.println("Starting zone system...");
                    initialize();
                }
        ).submit(SkreePlugin.inst());
    }

    private Path getWorkingDir() throws IOException {
        ConfigManager service = Sponge.getGame().getConfigManager();
        Path path = service.getPluginConfig(SkreePlugin.inst()).getDirectory();
        return Files.createDirectories(path.resolve("zones"));
    }

    private void initialize() {
        // TODO this is a very dumb way of doing this
        Optional<WorldService> optService = Sponge.getServiceManager().provide(WorldService.class);
        if (!optService.isPresent()) {
            Task.builder().delayTicks(1).execute(this::initialize).submit(SkreePlugin.inst());
            return;
        }


        World world = optService.get().getEffectWrapper("Instance").getWorlds().iterator().next();
        WorldResolver instWorldResolver = new WorldResolver(world, WorldEdit.getInstance());

        try {
            service = new ZoneServiceImpl(new ChainPlacementAllocator(getWorkingDir(), instWorldResolver));

            for (String name : Arrays.asList("CursedMine")) {
                service.registerManager(new AnExampleManager(name));
            }
            for (String name : Arrays.asList("Catacombs", "FreakyFour", "GoldRush", "PatientX")) {
                service.registerManager(new ExampleManager(name));
            }

            service.registerManager(new ShnugglesPrimeManager());

            Sponge.getCommandManager().register(SkreePlugin.inst(), ZoneMeCommand.aquireSpec(), "zoneme");

            try {
                Sponge.getServiceManager().setProvider(SkreePlugin.inst(), ZoneService.class, service);
            } catch (ProviderExistsException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ZoneService getService() {
        return service;
    }
}
