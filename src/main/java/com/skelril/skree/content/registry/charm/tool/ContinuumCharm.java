/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.registry.charm.tool;

import com.skelril.skree.content.registry.charm.CharmTools;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.entity.ArmorEquipable;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Map;
import java.util.Optional;

public class ContinuumCharm extends BlockPatternCharm {
    public ContinuumCharm() {
        super(1, "continuum", 9);
    }

    @Listener
    public void onBlockTouch(InteractBlockEvent event) {
        Optional<ArmorEquipable> optHolder = event.getCause().first(ArmorEquipable.class);
        if (optHolder.isPresent()) {
            ArmorEquipable holder = optHolder.get();
            Optional<ItemStack> optHeldItem = holder.getItemInHand();
            if (optHeldItem.isPresent()) {
                ItemStack heldItem = optHeldItem.get();
                Optional<Integer> optCharmLevel = CharmTools.getLevel(heldItem, this);
                if (optCharmLevel.isPresent()) {
                    int charmLevel = optCharmLevel.get();

                    Direction dir = event.getTargetSide();
                    Optional<Location<World>> optTargetBlockLoc = event.getTargetBlock().getLocation();

                    if (!optTargetBlockLoc.isPresent()) {
                        return;
                    }

                    Location<World> targetBlockLoc = optTargetBlockLoc.get();

                    if (!accepts(heldItem, targetBlockLoc.getBlock())) {
                        return;
                    }

                    process(holder, heldItem, targetBlockLoc, dir.getOpposite(), charmLevel);
                }
            }
        }
    }

    @Override
    protected void process(ArmorEquipable holder, ItemStack stack, Location<World> pos, Direction direction, int power) {
        final BlockType startType;
        final Map<BlockTrait<?>, ?> startTraits;

        BlockType curType = startType = pos.getBlockType();
        Map<BlockTrait<?>, ?> curTraits = startTraits = pos.getBlock().getTraitMap();

        for (int dist = power; dist > 0; --dist) {
            if (!startType.equals(curType) || !hasSameTraits(startTraits, curTraits)) {
                break;
            }

            if (!breakBlock(holder, pos)) {
                break;
            }

            pos = pos.add(direction.toVector3d());
            curType = pos.getBlockType();
            curTraits = pos.getBlock().getTraitMap();
        }
    }
}
