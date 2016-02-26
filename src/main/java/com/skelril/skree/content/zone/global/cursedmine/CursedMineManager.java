/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.global.cursedmine;

import com.skelril.nitro.Clause;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.zone.global.cursedmine.hitlist.HitList;
import com.skelril.skree.service.internal.zone.ZoneRegion;
import com.skelril.skree.service.internal.zone.ZoneSpaceAllocator;
import com.skelril.skree.service.internal.zone.global.GlobalZoneManager;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class CursedMineManager extends GlobalZoneManager<CursedMineInstance> implements Runnable {
    private HitList hitList = new HitList();

    public CursedMineManager() {
        Sponge.getEventManager().registerListeners(
                SkreePlugin.inst(),
                new CursedMineListener(this)
        );

        Task.builder().intervalTicks(20).execute(this).submit(SkreePlugin.inst());
    }

    public HitList getHitList() {
        return hitList;
    }

    public Optional<CursedMineInstance> getApplicableZone(BlockSnapshot block) {
        return getApplicableZone(block.getLocation().get());
    }

    public Optional<CursedMineInstance> getApplicableZone(Entity entity) {
        return getApplicableZone(entity.getLocation());
    }

    public Optional<CursedMineInstance> getApplicableZone(Location<World> loc) {
        Optional<CursedMineInstance> optInst = getActiveZone();
        if (optInst.isPresent() && optInst.get().contains(loc)) {
            return Optional.of(optInst.get());
        }
        return Optional.empty();
    }

    @Override
    public void init(ZoneSpaceAllocator allocator, Consumer<CursedMineInstance> callback) {
        Function<Clause<ZoneRegion, ZoneRegion.State>, CursedMineInstance> initFunc = clause -> {
            ZoneRegion region = clause.getKey();

            return new CursedMineInstance(region, hitList);
        };

        Consumer<CursedMineInstance> postInitFunc = instance -> {
            instance.init();

            callback.accept(instance);
        };

        allocator.regionFor(getSystemName(), initFunc, postInitFunc);
    }

    @Override
    public String getName() {
        return "Cursed Mine";
    }

    @Override
    public void run() {
        Optional<CursedMineInstance> optInst = getActiveZone();
        if (optInst.isPresent()) {
            CursedMineInstance inst = optInst.get();
            if (inst.isActive()) {
                inst.run();
                return;
            }
            zone.forceEnd();
            zone = null;
        }
    }
}
