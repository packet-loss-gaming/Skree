/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.region;

import com.skelril.nitro.module.NModule;
import com.skelril.nitro.module.NModuleTrigger;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.region.RegionCommand;
import com.skelril.skree.content.world.build.BuildWorldWrapper;
import com.skelril.skree.service.RegionService;
import com.skelril.skree.service.WorldService;
import com.skelril.skree.service.internal.region.RegionManager;
import com.skelril.skree.service.internal.region.RegionServiceImpl;
import com.skelril.skree.system.ServiceProvider;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.World;

import java.util.Optional;

@NModule(name = "Region System")
public class RegionSystem implements ServiceProvider<RegionService> {

    private RegionServiceImpl service;

    @NModuleTrigger(trigger = "SERVER_STARTED", dependencies = {"World System"})
    public void init() {
        service = new RegionServiceImpl();

        Optional<WorldService> optWorldService = Sponge.getServiceManager().provide(WorldService.class);
        if (optWorldService.isPresent()) {
            for (World world : optWorldService.get().getEffectWrapper(BuildWorldWrapper.class).get().getWorlds()) {
                RegionManager manager = new RegionManager(world.getName());
                manager.load();
                service.addManager(world, manager);
            }
        }

        // Register the service & command
        Sponge.getEventManager().registerListeners(SkreePlugin.inst(), service);
        Sponge.getServiceManager().setProvider(SkreePlugin.inst(), RegionService.class, service);
        Sponge.getCommandManager().register(SkreePlugin.inst(), RegionCommand.aquireSpec(), "region", "rg");
    }

    @Override
    public RegionService getService() {
        return service;
    }
}
