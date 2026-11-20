package com.steve.catbomb.events.client;

import com.steve.catbomb.client.ClientTntCache;
import com.steve.catbomb.constants.ModConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID, value = Dist.CLIENT)
public class CatTntParticleEvents {
    private static final int PARTICLE_INTERVAL = 10;

    private static final double SEARCH_RANGE = 16.0;
    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        ClientLevel level = mc.level;
        var player = mc.player;

        if (++tickCounter % PARTICLE_INTERVAL != 0) return;

        var range = AABB.ofSize(player.position(), SEARCH_RANGE, SEARCH_RANGE, SEARCH_RANGE);
        List<Cat> cats = level.getEntitiesOfClass(Cat.class, range, (cat) -> ClientTntCache.isLit(cat.getUUID()));

        var random = level.random;
        for (Cat cat : cats) {
            for (int i = 0; i < 2; i++) {
                double x = cat.getX() + (random.nextDouble() - 0.5) * 0.8;
                double y = cat.getY() + random.nextDouble() * 0.6 + 0.2;
                double z = cat.getZ() + (random.nextDouble() - 0.5) * 0.8;
                level.addParticle(ParticleTypes.SMOKE, x, y, z, 0, 0.1, 0);
                if (random.nextFloat() < 0.2) {
                    level.addParticle(ParticleTypes.FLAME, x, y, z, 0, 0.05, 0);
                }
            }
        }
    }
}