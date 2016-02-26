/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.zone;


import com.sk89q.worldedit.WorldEdit;
import com.skelril.nitro.module.NModule;
import com.skelril.nitro.module.NModuleTrigger;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.zone.ZoneMeCommand;
import com.skelril.skree.content.zone.global.cursedmine.CursedMineManager;
import com.skelril.skree.content.zone.group.example.ExampleManager;
import com.skelril.skree.content.zone.group.goldrush.GoldRushManager;
import com.skelril.skree.content.zone.group.patientx.PatientXManager;
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
import org.spongepowered.api.world.World;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

@NModule(name = "Zone System")
public class ZoneSystem implements ServiceProvider<ZoneService> {

    private ZoneService service;

    private Path getWorkingDir() throws IOException {
        ConfigManager service = Sponge.getGame().getConfigManager();
        Path path = service.getPluginConfig(SkreePlugin.inst()).getDirectory();
        return Files.createDirectories(path.resolve("zones"));
    }

    @NModuleTrigger(trigger = "SERVER_STARTED", dependencies = {"World System"})
    public void init() {
        Optional<WorldService> optService = Sponge.getServiceManager().provide(WorldService.class);
        World world = optService.get().getEffectWrapper("Instance").getWorlds().iterator().next();
        Task.builder().execute(() -> {
            WorldResolver instWorldResolver = new WorldResolver(world, WorldEdit.getInstance());

            try {
                service = new ZoneServiceImpl(new ChainPlacementAllocator(getWorkingDir(), instWorldResolver));

                for (String name : Arrays.asList("Catacombs", "FreakyFour")) {
                    service.registerManager(new ExampleManager(name));
                }

                service.registerManager(new CursedMineManager());

                service.registerManager(new GoldRushManager());
                service.registerManager(new ShnugglesPrimeManager());
                service.registerManager(new PatientXManager());

                Sponge.getCommandManager().register(SkreePlugin.inst(), ZoneMeCommand.aquireSpec(), "zoneme");
                Sponge.getServiceManager().setProvider(SkreePlugin.inst(), ZoneService.class, service);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).delayTicks(1).submit(SkreePlugin.inst());
    }

    @Override
    public ZoneService getService() {
        return service;
    }
}
