/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.zone.decorator;

import com.flowpowered.math.vector.Vector3i;
import com.skelril.nitro.Clause;
import com.skelril.skree.service.internal.zone.WorldResolver;
import com.skelril.skree.service.internal.zone.ZoneRegion;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Decorator {
    <T> ZoneRegion pasteAt(WorldResolver world, Vector3i origin, String resourceName, Function<Clause<ZoneRegion, ZoneRegion.State>, T> initMapper, Consumer<T> callback);
}
