/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.zone;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.function.Predicate;

public class ZoneApplicableListener {
    private final Predicate<Location<World>> isApplicable;

    public ZoneApplicableListener(Predicate<Location<World>> isApplicable) {
        this.isApplicable = isApplicable;
    }

    public boolean isApplicable(Entity entity) {
        return isApplicable(entity.getLocation());
    }

    public boolean isApplicable(Location<World> location) {
        return isApplicable.test(location);
    }
}
