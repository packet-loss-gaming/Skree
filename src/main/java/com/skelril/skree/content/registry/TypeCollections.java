/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;
import com.skelril.skree.content.registry.block.CustomBlockTypes;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;

public class TypeCollections {

    private static final ImmutableCollection<BlockType> ores = ImmutableSet.of(
            BlockTypes.COAL_ORE,
            BlockTypes.DIAMOND_ORE,
            BlockTypes.EMERALD_ORE,
            BlockTypes.REDSTONE_ORE,
            BlockTypes.GOLD_ORE,
            BlockTypes.IRON_ORE,
            BlockTypes.LAPIS_ORE,
            BlockTypes.LIT_REDSTONE_ORE,
            BlockTypes.QUARTZ_ORE,
            (BlockType) CustomBlockTypes.JURACK_ORE
    );

    public static ImmutableCollection<BlockType> ore() {
        return ores;
    }
}
