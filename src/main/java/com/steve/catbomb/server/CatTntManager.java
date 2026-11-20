package com.steve.catbomb.server;

import com.steve.catbomb.MainClass;
import com.steve.catbomb.config.CatFightConfig;
import com.steve.catbomb.constants.NbtConstants;
import com.steve.catbomb.network.TntNetworkPack;
import net.minecraft.world.entity.animal.Cat;
import net.minecraftforge.network.PacketDistributor;

public final class CatTntManager {

    public static int getTntCount(Cat cat) {
        return cat.getPersistentData().getInt(NbtConstants.CAT_TNT_COUNT_TAG);
    }

    public static void setTntCount(Cat cat, int count) {
        count = Math.max(0, count);
        cat.getPersistentData().putInt(NbtConstants.CAT_TNT_COUNT_TAG, count);
        if (count <= 0) {
            cat.getPersistentData().remove(NbtConstants.CAT_LIT_TAG);
        }
        boolean lit = cat.getPersistentData().getBoolean(NbtConstants.CAT_LIT_TAG);
        MainClass.CHANNEL.send(PacketDistributor.ALL.noArg(),
                new TntNetworkPack(cat.getUUID(), count, lit));
    }

    public static boolean addTnt(Cat cat, int amount) {
        if (amount <= 0) return false;
        int max = CatFightConfig.CONFIG.getMaxTntCount();
        int current = getTntCount(cat);
        int newCount = Math.min(current + amount, max);
        if (newCount == current) return false;
        setTntCount(cat, newCount);
        return true;
    }

    public static void removeAllTnt(Cat cat) {
        setTntCount(cat, 0);
    }

    public static boolean hasTnt(Cat cat) {
        return getTntCount(cat) > 0;
    }

    public static boolean hasLit(Cat cat) {
        return cat.getPersistentData().getBoolean(NbtConstants.CAT_LIT_TAG);
    }

    public static float getExplosionPower(Cat cat) {
        int count = getTntCount(cat);
        if (count <= 0) return 0f;

        float base = CatFightConfig.CONFIG.getTntBaseExplosionPower();
        float perStack = CatFightConfig.CONFIG.getTntPowerPerStack();
        float maxPower = CatFightConfig.CONFIG.getTntMaxExplosionPower();

        float power = base + (count - 1) * perStack;
        return Math.min(power, maxPower);
    }
}