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
import com.skelril.skree.content.world.instance.InstanceWorldWrapper;
import com.skelril.skree.content.zone.ZoneMeCommand;
import com.skelril.skree.content.zone.global.cursedmine.CursedMineManager;
import com.skelril.skree.content.zone.global.templeoffate.TempleOfFateManager;
import com.skelril.skree.content.zone.global.theforge.TheForgeManager;
import com.skelril.skree.content.zone.group.catacombs.CatacombsManager;
import com.skelril.skree.content.zone.group.desmiredungeon.DesmireDungeonManager;
import com.skelril.skree.content.zone.group.freakyfour.FreakyFourManager;
import com.skelril.skree.content.zone.group.goldrush.GoldRushManager;
import com.skelril.skree.content.zone.group.jungleraid.JungleRaidManager;
import com.skelril.skree.content.zone.group.patientx.PatientXManager;
import com.skelril.skree.content.zone.group.shnugglesprime.ShnugglesPrimeManager;
import com.skelril.skree.content.zone.group.skywars.SkyWarsManager;
import com.skelril.skree.service.WorldService;
import com.skelril.skree.service.ZoneService;
import com.skelril.skree.service.internal.zone.WorldResolver;
import com.skelril.skree.service.internal.zone.ZoneServiceImpl;
import com.skelril.skree.service.internal.zone.allocator.CacheBasedAllocator;
import com.skelril.skree.service.internal.zone.decorator.Decorators;
import com.skelril.skree.system.ServiceProvider;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.World;

import java.util.Optional;

@NModule(name = "Zone System")
public class ZoneSystem implements ServiceProvider<ZoneService> {

    private ZoneService service;

    @NModuleTrigger(trigger = "SERVER_STARTED", dependencies = {"World System"})
    public void init() {
        Optional<WorldService> optService = Sponge.getServiceManager().provide(WorldService.class);
        World world = optService.get().getEffectWrapper(InstanceWorldWrapper.class).get().getWorlds().iterator().next();
        Task.builder().execute(() -> {
            WorldResolver instWorldResolver = new WorldResolver(world, WorldEdit.getInstance());

            service = new ZoneServiceImpl(new CacheBasedAllocator(Decorators.ZONE_PRIMARY_DECORATOR, instWorldResolver));

            service.registerManager(new CursedMineManager());
            service.registerManager(new TempleOfFateManager());
            service.registerManager(new TheForgeManager());

            service.registerManager(new CatacombsManager());
            service.registerManager(new DesmireDungeonManager());
            service.registerManager(new FreakyFourManager());
            service.registerManager(new GoldRushManager());
            service.registerManager(new JungleRaidManager());
            service.registerManager(new PatientXManager());
            service.registerManager(new ShnugglesPrimeManager());
            service.registerManager(new SkyWarsManager());

            Sponge.getServiceManager().setProvider(SkreePlugin.inst(), ZoneService.class, service);
            Sponge.getCommandManager().register(SkreePlugin.inst(), ZoneMeCommand.aquireSpec(), "zoneme");
        }).delayTicks(1).submit(SkreePlugin.inst());
    }

    @Override
    public ZoneService getService() {
        return service;
    }
}
