/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.data.util;

import com.google.common.base.Optional;
import org.spongepowered.api.data.property.block.LightEmissionProperty;
import org.spongepowered.api.data.property.block.SkyLuminanceProperty;
import org.spongepowered.api.world.Location;

public class LightLevelUtil {
    public static Optional<Integer> getMaxLightLevel(Location valueStore) {
        Optional<LightEmissionProperty> lightEmissionProperty = valueStore.getProperty(LightEmissionProperty.class);
        Optional<SkyLuminanceProperty> skyLuminanceProperty = valueStore.getProperty(SkyLuminanceProperty.class);

        if (!lightEmissionProperty.isPresent() || !skyLuminanceProperty.isPresent()) {
            return Optional.absent();
        }

        //noinspection ConstantConditions
        return Optional.of(Math.max(lightEmissionProperty.get().getValue(), skyLuminanceProperty.get().getValue()));
    }
}
