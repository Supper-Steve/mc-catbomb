package com.steve.catbomb.util;

import com.steve.catbomb.constants.ModConstants;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID)
public class Coroutine {

    private static final List<DelayedTask> tasks = new LinkedList<>();

    public static void delay(float seconds, Runnable action) {
        if (seconds <= 0f) {
            action.run();
            return;
        }
        float ticks = seconds * 20f;
        DelayedTask task = DelayedTaskPool.obtain(action, ticks);
        synchronized (tasks) {
            tasks.add(task);
        }
    }
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (tasks.isEmpty()) return;

        List<DelayedTask> toExecute = new ArrayList<>();

        synchronized (tasks) {
            Iterator<DelayedTask> it = tasks.iterator();
            while (it.hasNext()) {
                DelayedTask task = it.next();
                if (task.remainingTicks < 0f) {
                    it.remove();
                    DelayedTaskPool.recycle(task);
                    continue;
                }
                if (task.tick()) {
                    it.remove();
                    toExecute.add(task);
                }
            }
        }
        for (DelayedTask task : toExecute) {
            task.action.run();
            DelayedTaskPool.recycle(task);
        }
    }
}