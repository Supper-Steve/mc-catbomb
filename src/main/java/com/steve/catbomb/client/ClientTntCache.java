package com.steve.catbomb.client;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class ClientTntCache {
    private static final Map<UUID, Integer> TNT_COUNT = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> LIT_START_TIME = new ConcurrentHashMap<>();

    public static void put(UUID catId, int count, boolean lit) {
        if (count <= 0) {
            TNT_COUNT.remove(catId);
            LIT_START_TIME.remove(catId);
        } else {
            TNT_COUNT.put(catId, count);
            if (lit) {
                LIT_START_TIME.put(catId, System.currentTimeMillis());
            } else {
                LIT_START_TIME.remove(catId);
            }
        }
    }

    public static boolean hasTnt(UUID catId) {
        return TNT_COUNT.getOrDefault(catId, 0) > 0;
    }
    public static boolean isLit(UUID catId) {
        return LIT_START_TIME.containsKey(catId);
    }
    public static long getLitStartTime(UUID catId) {
        return LIT_START_TIME.getOrDefault(catId, 0L);
    }
}