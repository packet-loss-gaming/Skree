/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.dropclear;

import com.flowpowered.math.vector.Vector3i;
import com.skelril.nitro.entity.EntityCleanupTask;
import com.skelril.nitro.time.TimedRunnable;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.DropClearService;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.sink.MessageSinks;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class DropClearServiceImpl implements DropClearService {

    private int autoAmt;
    private float panicMod;

    private Map<Extent, TimedRunnable<EntityCleanupTask>> timers = new HashMap<>();
    private Map<Extent, Map<Vector3i, ? extends DropClearStats>> lastClear = new HashMap<>();

    public DropClearServiceImpl(int autoAmt, float panicMod) {
        this.autoAmt = autoAmt;
        this.panicMod = panicMod;
    }

    @Override
    public boolean cleanup(Extent extent, int seconds) {
        return dropClear(extent, seconds, true);
    }

    @Override
    public boolean checkedCleanup(Extent extent) {
        CheckProfile profile = CheckProfile.createFor(extent, checkedEntities);
        int itemCount = profile.getEntities().size();
        if (itemCount >= autoAmt) {
            if (itemCount >= autoAmt * panicMod) {
                forceCleanup(extent);
            } else if (!isActiveFor(extent)) {
                cleanup(extent);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean isActiveFor(Extent extent) {
        return getActiveTask(extent) != null;
    }

    private TimedRunnable<EntityCleanupTask> getActiveTask(Extent extent) {
        TimedRunnable<EntityCleanupTask> runnable = timers.get(extent);

        // Check for old task, and overwrite if allowed
        if (runnable != null && !runnable.isComplete()) {
            return runnable;
        }
        return null;
    }

    @Override
    public void forceCleanup(Extent extent) {
        dropClear(extent, 0, true);
    }

    private static Set<EntityType> checkedEntities = new HashSet<>();

    static {
        checkedEntities.add(EntityTypes.ITEM);
        checkedEntities.add(EntityTypes.ARROW);
        checkedEntities.add(EntityTypes.EXPERIENCE_ORB);
    }

    private EntityCleanupTask pickDropClear(Extent extent) {
        return new EntityCleanupTask(extent, checkedEntities) {
            @Override
            public void notifyCleanProgress(int times) {
                extent.getEntities(input -> input instanceof Player).stream().map(p -> (Player) p).forEach(
                        player -> player.sendMessage(
                                ChatTypes.CHAT,
                                Texts.of(TextColors.RED, "Clearing drops in " + times + " seconds!")
                        )
                );
            }

            @Override
            public void notifyCleanBeginning() {
                extent.getEntities(input -> input instanceof Player).stream().map(p -> (Player) p).forEach(
                        player -> player.sendMessage(
                                ChatTypes.CHAT,
                                Texts.of(TextColors.RED, "Clearing drops!")
                        )
                );
            }

            @Override
            public void notifyCleanEnding() {
                extent.getEntities(input -> input instanceof Player).stream().map(p -> (Player) p).forEach(
                        player -> player.sendMessage(
                                ChatTypes.CHAT,
                                Texts.of(TextColors.GREEN, getLastProfile().getEntities().size() + " drops cleared!")
                        )
                );
            }
        };
    }

    private EntityCleanupTask pickDropClear(World world) {
        return new EntityCleanupTask(world, checkedEntities) {
            @Override
            public void notifyCleanProgress(int times) {
                MessageSinks.toAll().sendMessage(
                        Texts.of(
                                TextColors.RED,
                                "Clearing drops of " + world.getName() + " in " + times + " seconds!"
                        )
                );
            }

            @Override
            public void notifyCleanBeginning() {
                MessageSinks.toAll().sendMessage(
                        Texts.of(
                                TextColors.RED,
                                "Clearing drops of " + world.getName() + "!"
                        )
                );
            }

            @Override
            public void notifyCleanEnding() {
                MessageSinks.toAll().sendMessage(
                        Texts.of(
                                TextColors.GREEN,
                                getLastProfile().getEntities().size() + " drops cleared!"
                        )
                );
            }
        };
    }

    private boolean dropClear(Extent extent, int seconds, boolean overwrite) {
        TimedRunnable<EntityCleanupTask> runnable = getActiveTask(extent);

        // Check for old task, and overwrite if allowed
        if (runnable != null) {
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
                timers.remove(extent);
            }
        };

        // Offset this by one to prevent the drop clear from triggering twice
        Task task = Task.builder().execute(runnable).delayTicks(1).interval(
                1,
                TimeUnit.SECONDS
        ).submit(SkreePlugin.inst());

        runnable.setTask(task);
        timers.put(extent, runnable);
        return true;
    }
}
