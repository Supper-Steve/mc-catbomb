package com.steve.catbomb.util;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public final class DelayedTaskPool {
    private static final int MAX_POOL_SIZE = 256;
    private static final Queue<DelayedTask> pool = new ConcurrentLinkedQueue<>();
    private DelayedTaskPool() {}

    public static DelayedTask obtain(Runnable action, float remainingTicks) {
        DelayedTask task = pool.poll();
        if (task == null) {
            task = new DelayedTask(action, remainingTicks);
        } else {
            task.action = action;
            task.remainingTicks = remainingTicks;
        }
        return task;
    }
    public static void recycle(DelayedTask task) {
        if (task != null && pool.size() < MAX_POOL_SIZE) {
            task.action = null;
            task.remainingTicks = -1f;
            pool.offer(task);
        }
    }
}