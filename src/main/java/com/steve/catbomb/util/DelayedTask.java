package com.steve.catbomb.util;

public  class DelayedTask {
    public Runnable action;
    public float remainingTicks;

    public DelayedTask(Runnable action, float ticks) {
        this.action = action;
        this.remainingTicks = ticks;
    }

    public boolean tick() {
        remainingTicks -= 1f;
        return remainingTicks <= 0f;
    }

    public void cancel() {
        remainingTicks = -1f;
    }

    public void reset(Runnable action, float ticks) {
        this.action = action;
        this.remainingTicks = ticks;
    }
}
