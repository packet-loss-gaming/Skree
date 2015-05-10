/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.shutdown;

import com.google.common.base.Optional;
import com.skelril.skree.SkreePlugin;
import com.skelril.skree.service.ShutdownService;
import com.skelril.nitro.text.PrettyText;
import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
import org.spongepowered.api.service.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;

import java.util.concurrent.TimeUnit;


public class ShutdownServiceImpl implements ShutdownService {

    private static final long DEFAULT_DOWNTIME = TimeUnit.SECONDS.toMillis(30);
    private static final Text DEFAULT_REASON = Texts.of("Shutting down!");

    private SkreePlugin plugin;

    private Game game;
    private Server server;

    private Optional<Task> task = Optional.absent();
    private String reopenDate;
    private int ticks = -1;

    public ShutdownServiceImpl(SkreePlugin plugin, Game game, Server server) {
        this.plugin = plugin;
        this.game = game;
        this.server = server;
    }

    @Override
    public int getTicksTilOffline() {
        return ticks;
    }

    @Override
    public boolean isShuttingDown() {
        return ticks != -1;
    }

    @Override
    public boolean shutdown(int ticks) {
        return shutdown(ticks, DEFAULT_DOWNTIME);
    }

    @Override
    public boolean shutdown(int ticks, long downtime) {
        return shutdown(ticks, DEFAULT_DOWNTIME, DEFAULT_REASON);
    }

    @Override
    public boolean shutdown(int ticks, Text message) {
        return shutdown(ticks, DEFAULT_DOWNTIME, message);
    }

    @Override
    public boolean shutdown(int ticks, long downtime, Text message) {
        if (ticks < 1) {
            return false;
        }

        reopenDate = PrettyText.dateFromCur(System.currentTimeMillis() + downtime + (ticks * 20 * 1000));

        this.ticks = ticks;

        if (!task.isPresent()) {
            task = game.getSyncScheduler().runRepeatingTask(plugin, () -> {
                int seconds = this.ticks / 20;
                if (this.ticks-- % 20 == 0 && (seconds > 0 && seconds % 5 == 0 || seconds <= 10 && seconds > 0)) {
                    server.broadcastMessage(
                            Texts.builder("Sever shutting down in "
                                            + seconds + " seconds - for "
                                            + reopenDate + ".").color(TextColors.RED).build()
                            );
                }
                if (this.ticks <= 0) {
                    server.broadcastMessage(Texts.builder("Server shutting down!").color(TextColors.RED).build());
                    forceShutdown(message);
                }
            }, 1);
        }
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
        ticks = -1;
        if (task.isPresent()) {
            task.get().cancel();
            task = Optional.absent();
        }
    }
}
