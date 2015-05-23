/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.entity;

import com.google.common.collect.Lists;
import com.skelril.nitro.time.IntegratedRunnable;
import com.skelril.nitro.time.TimeFilter;
import com.skelril.skree.service.internal.dropclear.CheckProfile;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import java.util.Collection;

public abstract class EntityCleanupTask implements IntegratedRunnable {

    private final Extent extent;
    private final Collection<EntityType> checkedEntities;
    private final TimeFilter filter;

    private CheckProfile profile;

    public EntityCleanupTask(Extent extent, Collection<EntityType> checkedEntities) {
        this(extent, checkedEntities, new TimeFilter(10, 5));
    }

    public EntityCleanupTask(Extent extent, Collection<EntityType> checkedEntities, TimeFilter filter) {
        this.extent = extent;
        this.checkedEntities = Lists.newArrayList(checkedEntities);
        this.filter = filter;
    }

    public CheckProfile getLastProfile() {
        return profile;
    }

    @Override
    public boolean run(int times) {
        if (filter.matchesFilter(times)) {
            notifyCleanProgress(times);
        }
        return true;
    }

    public abstract void notifyCleanProgress(int times);

    public abstract void notifyCleanBeginning();
    public abstract void notifyCleanEnding();

    @Override
    public void end() {
        notifyCleanBeginning();

        // TODO needs chunk entity API
        if (extent instanceof World && false) {
            profile = CheckProfile.createFor((World) extent, checkedEntities);
        } else {
            profile = CheckProfile.createFor(extent, checkedEntities);
        }

        Collection<? extends Entity> entities = profile.getEntities();
        entities.stream().forEach(Entity::remove);

        notifyCleanEnding();
    }
}
