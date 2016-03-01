/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.shutdown;


import com.skelril.nitro.text.PrettyText;
import com.skelril.nitro.time.IntegratedRunnable;
import com.skelril.nitro.time.TimeFilter;
import com.skelril.nitro.time.TimedRunnable;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.ShutdownService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.format.TextColors;

import java.util.concurrent.TimeUnit;


public class ShutdownServiceImpl implements ShutdownService {

    private static final long DEFAULT_DOWNTIME = TimeUnit.SECONDS.toMillis(30);
    private static final Text DEFAULT_REASON = Text.of("Shutting down!");

    private TimedRunnable runnable = null;
    private String reopenDate;


    @Override
    public int getSecondsTilOffline() {
        if (isShuttingDown()) {
            return runnable.getTimes();
        }
        return -1;
    }

    @Override
    public boolean isShuttingDown() {
        return runnable != null;
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

        if (runnable != null) {
            runnable.setTimes(seconds);
            return true;
        }

        IntegratedRunnable shutdown = new IntegratedRunnable() {
            @Override
            public boolean run(int times) {
                if (filter.matchesFilter(times)) {
                    MessageChannel.TO_ALL.send(
                            Text.of(
                                    TextColors.RED,
                                    "Sever shutting down in " + times + " seconds - for " + reopenDate + "."
                            )
                    );
                }
                return true;
            }

            @Override
            public void end() {
                MessageChannel.TO_ALL.send(Text.of(TextColors.RED, "Server shutting down!"));
                forceShutdown(message);
            }
        };

        TimedRunnable<IntegratedRunnable> runnable = new TimedRunnable<>(shutdown, seconds);
        Task task = Task.builder().execute(runnable).interval(1, TimeUnit.SECONDS).submit(SkreePlugin.inst());
        runnable.setTask(task);

        this.runnable = runnable;
        return true;
    }

    @Override
    public void forceShutdown() {
        forceShutdown(DEFAULT_REASON);
    }

    @Override
    public void forceShutdown(Text message) {
        Sponge.getServer().shutdown(message);
    }

    @Override
    public void cancelShutdown() {
        if (runnable != null) {
            runnable.cancel();
            runnable = null;
        }
    }
}
