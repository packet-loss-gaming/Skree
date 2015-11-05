/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone.group.shnugglesprime;

import com.skelril.nitro.Clause;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.internal.zone.ZoneRegion;
import com.skelril.skree.service.internal.zone.ZoneSpaceAllocator;
import com.skelril.skree.service.internal.zone.group.GroupZoneManager;
import org.spongepowered.api.Game;

import java.util.Iterator;

public class ShnugglesPrimeManager  extends GroupZoneManager<ShnugglesPrimeInstance> implements Runnable {

    public ShnugglesPrimeManager(Game game) {
        game.getScheduler().createTaskBuilder().intervalTicks(20).execute(this).submit(SkreePlugin.inst());
    }

    @Override
    public ShnugglesPrimeInstance discover(ZoneSpaceAllocator allocator) {
        Clause<ZoneRegion, ZoneRegion.State> result = allocator.regionFor(getName());
        ZoneRegion region = result.getKey();

        ShnugglesPrimeInstance instance = new ShnugglesPrimeInstance(region);
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
            it.remove();
        }
    }
}
