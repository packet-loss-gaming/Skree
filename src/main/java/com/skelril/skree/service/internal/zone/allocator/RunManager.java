/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.skree.service.internal.zone.allocator;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.RunContext;
import com.skelril.skree.SkreePlugin;
import org.spongepowered.api.scheduler.Task;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class RunManager {
    private static final long MAX_TIME = 75;
    private static final int MAX_TASK = 5;

    private static SupervisingRunContext supervisor = new SupervisingRunContext();
    private static List<RunningOperation> runners = new ArrayList<>();

    static {
        Task.builder().execute(() -> {
            supervisor.resume();

            List<Runnable> callBacks = new ArrayList<>();

            while (!runners.isEmpty() && supervisor.shouldContinue()) {
                Iterator<RunningOperation> it = runners.iterator();

                while (it.hasNext()) {
                    TaskRunContext taskRunContext = new TaskRunContext();
                    RunningOperation next = it.next();
                    if (next.complete(taskRunContext)) {
                        // Queue up the call backs, they can add a new runner,
                        // which will cause a CME if executed here
                        callBacks.add(next.getCallBack());
                        it.remove();
                    }
                }
            }

            callBacks.forEach(Runnable::run);
        }).intervalTicks(10).submit(SkreePlugin.inst());
    }

    public static void runOperation(Operation operation, Runnable callBack) {
        runners.add(new RunningOperation(operation, callBack));
    }

    private static Operation runOperation(Operation operation, RunContext context) {
        Operation next = operation;
        while (next != null && context.shouldContinue()) {
            try {
                next = next.resume(context);
            } catch (WorldEditException e) {
                e.printStackTrace();
                return null;
            }
        }

        if (next != null) {
            return next;
        } else {
            return null;
        }
    }

    private static class RunningOperation {
        private final Runnable callBack;
        private Operation next;

        public RunningOperation(Operation next, Runnable callBack) {
            this.next = next;
            this.callBack = callBack;
        }

        public boolean complete(RunContext context) {
            return (next = runOperation(next, context)) == null;
        }

        public Runnable getCallBack() {
            return callBack;
        }
    }

    private static abstract class TimeContext extends RunContext {
        private long curStart = System.currentTimeMillis();

        public void resume() {
            if (!shouldContinue()) {
                curStart = System.currentTimeMillis();
            }
        }

        public boolean shouldContinue() {
            return System.currentTimeMillis() - curStart < getTimeRequirement();
        }

        public abstract long getTimeRequirement();
    }

    private static class SupervisingRunContext extends TimeContext {
        @Override
        public long getTimeRequirement() {
            return MAX_TIME;
        }
    }

    private static class TaskRunContext extends TimeContext {
        @Override
        public boolean shouldContinue() {
            return supervisor.shouldContinue() && super.shouldContinue();
        }

        @Override
        public long getTimeRequirement() {
            return MAX_TIME / Math.min(MAX_TASK, runners.size());
        }
    }
}
