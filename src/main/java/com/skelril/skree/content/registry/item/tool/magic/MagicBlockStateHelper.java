/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.item.tool.magic;

import com.skelril.nitro.item.ItemDropper;
import com.skelril.nitro.item.ItemStackFactory;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.content.registry.block.CustomBlockTypes;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Collection;

import static com.skelril.nitro.item.ItemStackFactory.newItemStackCollection;

public class MagicBlockStateHelper {

    private static int workingLadder = 0;
    private static int workingPlatform = 0;

    private static int foundLadder = 0;
    private static int foundPlatform = 0;

    protected static void dropItems(Location<World> loc, Cause cause) {
        ItemStack ladder = ItemStackFactory.newItemStack((BlockType) CustomBlockTypes.MAGIC_LADDER);
        ItemStack platform = ItemStackFactory.newItemStack((BlockType) CustomBlockTypes.MAGIC_PLATFORM);

        Collection<ItemStack> drops = new ArrayList<>();
        drops.addAll(newItemStackCollection(ladder, foundLadder));
        drops.addAll(newItemStackCollection(platform, foundPlatform));

        new ItemDropper(loc).dropStacks(drops, SpawnTypes.DROPPED_ITEM);

        resetCounts();
    }

    protected static void resetCounts() {
        foundLadder = foundPlatform = 0;
    }

    protected static void startLadder(Location<World> block) {
        if (workingLadder-- > 0) {
            return;
        }

        ladder(block);
        ladderRecursion(block);
        workingLadder = foundLadder - 1;
        workingPlatform = foundPlatform;
    }

    protected static void startPlatform(Location<World> block) {
        if (workingPlatform-- > 0) {
            return;
        }

        platform(block);
        platformRecursion(block);
        workingLadder = foundLadder;
        workingPlatform = foundPlatform - 1;
    }

    private static void recursiveDiscovery(Location<World> block) {
        platform(block);
        ladder(block);
    }

    private static void ladderRecursion(Location<World> block) {
        recursiveDiscovery(block.add(Direction.UP.toVector3d()));
        platform(block.add(Direction.EAST.toVector3d()));
        platform(block.add(Direction.WEST.toVector3d()));
        platform(block.add(Direction.NORTH.toVector3d()));
        platform(block.add(Direction.SOUTH.toVector3d()));
    }

    private static void ladder(Location<World> block) {
        if (block.getBlockType() != CustomBlockTypes.MAGIC_LADDER) {
            return;
        }

        ++foundLadder;
        block.setBlockType(BlockTypes.AIR, Cause.source(SkreePlugin.container()).build());

        ladderRecursion(block);
    }

    private static void platformRecursion(Location<World> block) {
        ladder(block.add(Direction.UP.toVector3d()));
        recursiveDiscovery(block.add(Direction.EAST.toVector3d()));
        recursiveDiscovery(block.add(Direction.WEST.toVector3d()));
        recursiveDiscovery(block.add(Direction.NORTH.toVector3d()));
        recursiveDiscovery(block.add(Direction.SOUTH.toVector3d()));
    }

    private static void platform(Location<World> block) {
        if (block.getBlockType() != CustomBlockTypes.MAGIC_PLATFORM) {
            return;
        }

        ++foundPlatform;
        block.setBlockType(BlockTypes.AIR, Cause.source(SkreePlugin.container()).build());

        platformRecursion(block);
    }
}
