/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service;

import com.skelril.skree.service.internal.world.WorldEffectWrapper;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.Optional;

public interface WorldService {
    void registerEffectWrapper(WorldEffectWrapper world);
    WorldEffectWrapper getEffectWrapper(String name);
    Optional<WorldEffectWrapper> getEffectWrapperFor(World world);
    Collection<WorldEffectWrapper> getEffectWrappers();
}
