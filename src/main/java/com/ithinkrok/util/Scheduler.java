package com.ithinkrok.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public interface Scheduler {

    <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit);

    default ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        return schedule(() -> {
            command.run();
            return true;
        }, delay, unit);
    }

    ScheduledFuture<?> scheduleRepeat(Runnable command, long initialDelay, long period, TimeUnit unit);

    <V> ScheduledFuture<V> scheduleAsync(Callable<V> callable, long delay, TimeUnit unit);

    default ScheduledFuture<?> scheduleAsync(Runnable command, long delay, TimeUnit unit) {
        return scheduleAsync(() -> {
            command.run();
            return true;
        }, delay, unit);
    }

    ScheduledFuture<?> scheduleRepeatAsync(Runnable command, long initialDelay, long period, TimeUnit unit);

}
