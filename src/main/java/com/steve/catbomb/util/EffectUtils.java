package com.steve.catbomb.util;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public final class EffectUtils {
    public static void apply(LivingEntity entity, MobEffect effect, float seconds, int amplifier) {
        apply(entity, effect, seconds, amplifier, false);
    }
    public static void apply(LivingEntity entity, MobEffect effect, float seconds, int amplifier, boolean ambient) {
        apply(entity, effect, seconds, amplifier, ambient, true, false);
    }
    public static void apply(LivingEntity entity, MobEffect effect, float seconds,
                             int amplifier, boolean ambient, boolean showParticles, boolean showIcon) {
        if (entity == null || effect == null) return;
        int durationTicks = Math.round(seconds * 20);
        MobEffectInstance instance = new MobEffectInstance(effect, durationTicks, amplifier,
                ambient, showParticles, showIcon);
        entity.addEffect(instance);
    }
}