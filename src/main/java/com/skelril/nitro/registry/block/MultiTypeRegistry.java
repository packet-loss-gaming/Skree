/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.registry.block;

import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;

public class MultiTypeRegistry {
    public static boolean isRedstoneOre(BlockType type) {
        return type.equals(BlockTypes.REDSTONE_ORE) || type.equals(BlockTypes.LIT_REDSTONE_ORE);
    }

    public static boolean isWater(BlockType type) {
        return type.equals(BlockTypes.WATER) || type.equals(BlockTypes.FLOWING_WATER);
    }
}
