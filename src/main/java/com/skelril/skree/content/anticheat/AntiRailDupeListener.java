/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.content.anticheat;

import com.skelril.skree.SkreePlugin;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.Piston;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.filter.cause.Named;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;

public class AntiRailDupeListener {
    private static final List<BlockType> railBlocks = new ArrayList<>();

    static {
        railBlocks.add(BlockTypes.RAIL);
        railBlocks.add(BlockTypes.ACTIVATOR_RAIL);
        railBlocks.add(BlockTypes.DETECTOR_RAIL);
        railBlocks.add(BlockTypes.GOLDEN_RAIL);
    }

    @Listener
    public void onPistonMove(ChangeBlockEvent event, @Named(value = NamedCause.SOURCE) Piston piston) {
        event.getTransactions().stream().map(Transaction::getFinal).forEach(block -> {
            BlockType finalType = block.getState().getType();
            if (railBlocks.contains(finalType)) {
                Location<World> location = block.getLocation().get();
                Task.builder().execute(() -> {
                    location.setBlockType(BlockTypes.AIR, Cause.source(SkreePlugin.container()).build());
                }).delayTicks(1).submit(SkreePlugin.inst());
            }
        });
    }
}
