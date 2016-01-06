/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.data.util;


import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class LightLevelUtil {
    public static Optional<Integer> getMaxLightLevel(Location<World> valueStore) {
        /*
        Optional<LightEmissionProperty> lightEmissionProperty = valueStore.getProperty(LightEmissionProperty.class);
        Optional<SkyLuminanceProperty> skyLuminanceProperty = valueStore.getProperty(SkyLuminanceProperty.class);

        if (!lightEmissionProperty.isPresent() || !skyLuminanceProperty.isPresent()) {
            return Optional.empty();
        }

        //noinspection ConstantConditions
        return Optional.of(Math.max(lightEmissionProperty.get().getValue(), skyLuminanceProperty.get().getValue()));
        */
        return Optional.of(getMaxLightLevelNMS(valueStore));
    }

    private static int getMaxLightLevelNMS(Location<World> loc) {
        BlockPos bpos = new BlockPos(loc.getX(), loc.getY(), loc.getZ());

        net.minecraft.world.World nmsWorld = ((net.minecraft.world.World) loc.getExtent());

        int sky = nmsWorld.getLightFor(EnumSkyBlock.SKY, bpos) - nmsWorld.getSkylightSubtracted();
        int block = nmsWorld.getLightFor(EnumSkyBlock.BLOCK, bpos);

        return Math.max(sky, block);
    }
}
