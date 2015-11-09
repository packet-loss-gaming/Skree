/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.shnugglesprime;

import com.skelril.nitro.Clause;
import com.skelril.openboss.BossManager;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.zone.ZoneBossDetail;
import com.skelril.skree.service.internal.zone.ZoneRegion;
import com.skelril.skree.service.internal.zone.ZoneSpaceAllocator;
import com.skelril.skree.service.internal.zone.group.GroupZoneManager;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.monster.Giant;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

public class ShnugglesPrimeManager  extends GroupZoneManager<ShnugglesPrimeInstance> implements Runnable {

    private Queue<ZoneRegion> freeRegions = new LinkedList<>();
    private final BossManager<Giant, ZoneBossDetail<ShnugglesPrimeInstance>> bossManager = new BossManager<>();

    public ShnugglesPrimeManager(Game game) {
        SkreePlugin.inst().getGame().getEventManager().registerListeners(
                SkreePlugin.inst(),
                new ShnugglesPrimeListener(this)
        );

        setupBossManager();
        game.getScheduler().createTaskBuilder().intervalTicks(20).execute(this).submit(SkreePlugin.inst());
    }

    private void setupBossManager() {

    }

    public Optional<ShnugglesPrimeInstance> getApplicableZone(Entity entity) {
        for (ShnugglesPrimeInstance inst : zones) {
            if (inst.contains(entity)) {
                return Optional.of(inst);
            }
        }
        return Optional.empty();
    }

    @Override
    public ShnugglesPrimeInstance discover(ZoneSpaceAllocator allocator) {
        ZoneRegion region = freeRegions.poll();
        if (region == null) {
            Clause<ZoneRegion, ZoneRegion.State> result = allocator.regionFor(getName());
            region = result.getKey();
        }

        ShnugglesPrimeInstance instance = new ShnugglesPrimeInstance(region, bossManager);
        instance.init();

        zones.add(instance);

        return instance;
    }

    @Override
    public String getName() {
        return "Shnuggles Prime";
    }

    @Override
    public void run() {
        Iterator<ShnugglesPrimeInstance> it = zones.iterator();
        while (it.hasNext()) {
            ShnugglesPrimeInstance next = it.next();
            if (next.isActive()) {
                next.run();
                continue;
            }
            next.forceEnd();
            freeRegions.add(next.getRegion());
            it.remove();
        }
    }
}
