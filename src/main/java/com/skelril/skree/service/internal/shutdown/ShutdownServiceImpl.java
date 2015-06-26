/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.shutdown;

import com.google.common.base.Optional;
import com.skelril.nitro.text.PrettyText;
import com.skelril.nitro.time.IntegratedRunnable;
import com.skelril.nitro.time.TimeFilter;
import com.skelril.nitro.time.TimedRunnable;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.ShutdownService;
import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
import org.spongepowered.api.service.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.sink.MessageSinks;

import java.util.concurrent.TimeUnit;


public class ShutdownServiceImpl implements ShutdownService {

    private static final long DEFAULT_DOWNTIME = TimeUnit.SECONDS.toMillis(30);
    private static final Text DEFAULT_REASON = Texts.of("Shutting down!");

    private final SkreePlugin plugin;

    private final Game game;
    private final Server server;

    private Optional<TimedRunnable> runnable = Optional.absent();
    private String reopenDate;

    public ShutdownServiceImpl(SkreePlugin plugin, Game game) {
        this.plugin = plugin;
        this.game = game;
        this.server = game.getServer();
    }

    @Override
    public int getSecondsTilOffline() {
        if (isShuttingDown()) {
            return runnable.get().getTimes();
        }
        return -1;
    }

    @Override
    public boolean isShuttingDown() {
        return runnable.isPresent();
    }

    @Override
    public boolean shutdown(int seconds) {
        return shutdown(seconds, DEFAULT_DOWNTIME);
    }

    @Override
    public boolean shutdown(int seconds, long downtime) {
        return shutdown(seconds, DEFAULT_DOWNTIME, DEFAULT_REASON);
    }

    @Override
    public boolean shutdown(int seconds, Text message) {
        return shutdown(seconds, DEFAULT_DOWNTIME, message);
    }

    private static final TimeFilter filter = new TimeFilter(10, 5);

    @Override
    public boolean shutdown(int seconds, long downtime, Text message) {
        if (seconds < 1) {
            return false;
        }

        reopenDate = PrettyText.dateFromCur(System.currentTimeMillis() + downtime + (seconds * 1000));

        if (runnable.isPresent()) {
            runnable.get().setTimes(seconds);
            return true;
        }

        IntegratedRunnable shutdown = new IntegratedRunnable() {
            @Override
            public boolean run(int times) {
                if (filter.matchesFilter(times)) {
                    MessageSinks.toAll().sendMessage(
                            Texts.builder(
                                    "Sever shutting down in "
                                            + times + " seconds - for "
                                            + reopenDate + "."
                            ).color(TextColors.RED).build()
                    );
                }
                return true;
            }

            @Override
            public void end() {
                MessageSinks.toAll().sendMessage(Texts.builder("Server shutting down!").color(TextColors.RED).build());
                forceShutdown(message);
            }
        };

        TimedRunnable<IntegratedRunnable> runnable = new TimedRunnable<>(shutdown, seconds);
        Task task = game.getScheduler().getTaskBuilder().execute(runnable).interval(1, TimeUnit.SECONDS).submit(plugin);
        runnable.setTask(task);

        this.runnable = Optional.of(runnable);
        return true;
    }

    @Override
    public void forceShutdown() {
        forceShutdown(DEFAULT_REASON);
    }

    @Override
    public void forceShutdown(Text message) {
        server.shutdown(message);
    }

    @Override
    public void cancelShutdown() {
        if (runnable.isPresent()) {
            runnable.get().cancel();
            runnable = Optional.absent();
        }
    }
}
