/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.world;

import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import java.util.Collection;

public interface WorldEffectWrapper {
    String getName();

    boolean isApplicable(Extent extent);

    void addWorld(World world);
    void addWorld(Collection<World> worlds);

    Collection<World> getWorlds();
}
