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

import java.util.Optional;

public class RunManager {
    private static ModifiedRunContext supervisor = new ModifiedRunContext();

    public static void runOperation(Operation operation, Runnable callBack) {
        runOperation(operation, callBack, supervisor);
    }

    private static void runOperation(Operation operation, Runnable callBack, ModifiedRunContext context) {
        context.resume();

        Optional<Operation> optNext = Optional.of(operation);
        while (optNext.isPresent() && context.shouldContinue()) {
            try {
                optNext = Optional.ofNullable(optNext.get().resume(context));
            } catch (WorldEditException e) {
                e.printStackTrace();
                return;
            }
        }

        if (optNext.isPresent()) {
            Operation next = optNext.get();
            Task.builder().execute(() -> {
                runOperation(next, callBack, context);
            }).delayTicks(10).submit(SkreePlugin.inst());
        } else {
            callBack.run();
        }
    }

    private static class ModifiedRunContext extends RunContext {
        private long curStart = System.currentTimeMillis();

        public void resume() {
            if (!shouldContinue()) {
                curStart = System.currentTimeMillis();
            }
        }

        @Override
        public boolean shouldContinue() {
            return System.currentTimeMillis() - curStart < 500;
        }
    }
}
