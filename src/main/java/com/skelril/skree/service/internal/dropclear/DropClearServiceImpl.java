/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.dropclear;

import com.google.common.base.Optional;
import com.skelril.nitro.entity.EntityCleanupTask;
import com.skelril.nitro.time.TimedRunnable;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.DropClearService;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.service.scheduler.Task;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import java.util.*;

public class DropClearServiceImpl implements DropClearService {

    private final SkreePlugin plugin;

    private final Game game;

    private Map<Extent, TimedRunnable<EntityCleanupTask>> timers = new HashMap<>();
    private Map<Extent, Collection<? extends DropClearStats>> lastClear = new HashMap<>();

    public DropClearServiceImpl(SkreePlugin plugin, Game game) {
        this.plugin = plugin;
        this.game = game;
    }

    @Override
    public boolean cleanup(Extent extent, int seconds) {
        return dropClear(extent, seconds, true);
    }

    @Override
    public boolean checkedCleanup(Extent extent) {
        CheckProfile profile;
        // TODO needs chunk entity API
        if (extent instanceof World && false) {
            profile = CheckProfile.createFor((World) extent, checkedEntities);
        } else {
            profile = CheckProfile.createFor(extent, checkedEntities);
        }

        int itemCount = profile.getEntities().size();
        // TODO configurable counts
        int count = 1000;
        if (itemCount >= count) {
            dropClear(extent, itemCount >= (count * 3) ? 0 : 10, false);
        }
        return false;
    }


    @Override
    public void forceCleanup(Extent extent) {
        dropClear(extent, 0, true);
    }

    private static Set<EntityType> checkedEntities = new HashSet<>();

    static {
        checkedEntities.add(EntityTypes.DROPPED_ITEM);
        checkedEntities.add(EntityTypes.ARROW);
        checkedEntities.add(EntityTypes.EXPERIENCE_ORB);
    }

    private EntityCleanupTask pickDropClear(Extent extent) {
        return new EntityCleanupTask(extent, checkedEntities) {
            @Override
            public void notifyCleanProgress(int times) {
                extent.getEntities(input -> input instanceof Player).stream().map(p -> (Player) p).forEach(
                        player -> player.sendMessage(
                                ChatTypes.SYSTEM,
                                Texts.builder().color(TextColors.RED).append(Texts.of("Clearing drops in " + times + " seconds!")).build()
                        )
                );
            }

            @Override
            public void notifyCleanBeginning() {
                extent.getEntities(input -> input instanceof Player).stream().map(p -> (Player) p).forEach(
                        player -> player.sendMessage(
                                ChatTypes.SYSTEM,
                                Texts.builder().color(TextColors.RED).append(Texts.of("Clearing drops!")).build()
                        )
                );
            }

            @Override
            public void notifyCleanEnding() {
                extent.getEntities(input -> input instanceof Player).stream().map(p -> (Player) p).forEach(
                        player -> player.sendMessage(
                                ChatTypes.SYSTEM,
                                Texts.builder().color(TextColors.GREEN).append(Texts.of(getLastProfile().getEntities().size() + " drops cleared!")).build()
                        )
                );
            }
        };
    }

    private EntityCleanupTask pickDropClear(World world) {
        return new EntityCleanupTask(world, checkedEntities) {
            @Override
            public void notifyCleanProgress(int times) {
                world.getEntities(input -> input instanceof Player).stream().map(p -> (Player) p).forEach(
                        player -> player.sendMessage(
                                ChatTypes.SYSTEM,
                                Texts.builder().color(TextColors.RED).append(Texts.of("Clearing drops of " + world.getName() + " in " + times + " seconds!")).build()
                        )
                );
            }

            @Override
            public void notifyCleanBeginning() {
                world.getEntities(input -> input instanceof Player).stream().map(p -> (Player) p).forEach(
                        player -> player.sendMessage(
                                ChatTypes.SYSTEM,
                                Texts.builder().color(TextColors.RED).append(Texts.of("Clearing drops of " + world.getName() + "!")).build()
                        )
                );
            }

            @Override
            public void notifyCleanEnding() {
                world.getEntities(input -> input instanceof Player).stream().map(p -> (Player) p).forEach(
                        player -> player.sendMessage(
                                ChatTypes.SYSTEM,
                                Texts.builder().color(TextColors.GREEN).append(Texts.of(getLastProfile().getEntities().size() + " drops cleared!")).build()
                        )
                );
            }
        };
    }

    private boolean dropClear(Extent extent, int seconds, boolean overwrite) {
        TimedRunnable<EntityCleanupTask> runnable = timers.get(extent);

        // Check for old task, and overwrite if allowed
        if (runnable != null && !runnable.isComplete()) {
            if (overwrite) {
                runnable.setTimes(seconds);
                return true;
            }
            return false;
        }

        EntityCleanupTask cleanupTask;
        if (extent instanceof World) {
            cleanupTask = pickDropClear((World) extent);
        } else {
            cleanupTask = pickDropClear(extent);
        }

        // Setup new task
        runnable = new TimedRunnable<EntityCleanupTask>(cleanupTask, seconds) {
            @Override
            public void cancel(boolean withEnd) {
                super.cancel(withEnd);
                if (withEnd) {
                    lastClear.put(extent, getBaseTask().getLastProfile().getStats());
                }
            }
        };

        // Offset this by one to prevent the drop clear from triggering twice
        Optional<Task> task = game.getSyncScheduler().runRepeatingTaskAfter(plugin, runnable, 20, 1);

        if (!task.isPresent()) {
            return false;
        }

        runnable.setTask(task.get());
        timers.put(extent, runnable);
        return true;
    }
}
