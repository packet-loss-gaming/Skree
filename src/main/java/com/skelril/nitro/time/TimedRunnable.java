/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.skelril.nitro.time;

import org.spongepowered.api.scheduler.Task;

public class TimedRunnable<T extends IntegratedRunnable> implements Runnable {

    private Task task;
    private T action;

    private int times;
    private boolean done = false;

    public TimedRunnable(T action, int times) {
        this.action = action;
        this.times = times;
    }

    public T getBaseTask() {
        return action;
    }

    public boolean isComplete() {
        return done;
    }

    public void addTime(int times) {
        this.times += times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public int getTimes() {
        return times;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }

    @Override
    public void run() {
        if (times > 0) {
            boolean completed = true;
            try {
                completed = action.run(times);
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                if (completed) {
                    times--;
                }
            }
        } else {
            cancel(true);
        }

    }

    public void cancel() {
        cancel(false);
    }

    public void cancel(boolean withEnd) {

        if (done) return; // Task is done

        if (withEnd) action.end();
        task.cancel();
        done = true;
    }
}