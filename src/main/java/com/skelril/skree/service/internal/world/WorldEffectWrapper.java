/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.world;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Collection;

public interface WorldEffectWrapper {
    String getName();

    default boolean isApplicable(Entity entity) {
        return isApplicable(entity.getWorld());
    }
    default boolean isApplicable(Location<World> location) {
        return isApplicable(location.getExtent());
    }
    boolean isApplicable(World world);

    void addWorld(World world);

    World getPrimaryWorld();
    Collection<World> getWorlds();
}
