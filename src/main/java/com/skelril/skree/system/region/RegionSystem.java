/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.system.region;

import com.skelril.nitro.module.NModule;
import com.skelril.nitro.module.NModuleTrigger;
import com.skelril.skree.SkreePlugin;
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
            for (World world : optWorldService.get().getEffectWrapper("Build").getWorlds()) {
                service.addManager(world, new RegionManager());
            }
        }

        // Register the service & command
        Sponge.getEventManager().registerListeners(SkreePlugin.inst(), service);
        Sponge.getServiceManager().setProvider(SkreePlugin.inst(), RegionService.class, service);
    }

    @Override
    public RegionService getService() {
        return service;
    }
}
