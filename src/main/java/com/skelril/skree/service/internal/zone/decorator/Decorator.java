/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.zone.decorator;

import com.flowpowered.math.vector.Vector3i;
import com.skelril.skree.service.internal.zone.WorldResolver;
import com.skelril.skree.service.internal.zone.ZoneWorldBoundingBox;

import java.util.function.Consumer;
import java.util.function.Function;

public interface Decorator {
  <T> ZoneWorldBoundingBox pasteAt(WorldResolver world, Vector3i origin, String resourceName, Function<ZoneWorldBoundingBox, T> initMapper, Consumer<T> callback);
}
