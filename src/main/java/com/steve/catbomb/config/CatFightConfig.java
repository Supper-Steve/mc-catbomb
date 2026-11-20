package com.steve.catbomb.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class CatFightConfig {
    public static final ForgeConfigSpec SPEC;
    public static final Config CONFIG;

    static {
        Pair<Config, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(Config::new);
        SPEC = pair.getRight();
        CONFIG = pair.getLeft();
    }

    public static class Config {
        private final ForgeConfigSpec.IntValue maxTntCount;
        private final ForgeConfigSpec.DoubleValue tntBaseExplosionPower;
        private final ForgeConfigSpec.DoubleValue tntPowerPerStack;
        private final ForgeConfigSpec.DoubleValue tntMaxExplosionPower;

        Config(ForgeConfigSpec.Builder builder) {
            builder.push("tnt");

            maxTntCount = builder
                    .comment("一只猫最多可以拥有的TNT数量")
                    .defineInRange("maxTntCount", 4, 1, 16);

            tntBaseExplosionPower = builder
                    .comment("单个TNT的基础爆炸威力 (默认4.0)")
                    .defineInRange("baseExplosionPower", 4.0, 1.0, 64.0);

            tntPowerPerStack = builder
                    .comment("每多一个TNT增加的威力")
                    .defineInRange("powerPerStack", 4.0, 0.5, 32.0);

            tntMaxExplosionPower = builder
                    .comment("爆炸威力上限")
                    .defineInRange("maxExplosionPower", 32.0, 4.0, 128.0);

            builder.pop();
        }

        public int getMaxTntCount() { return maxTntCount.get(); }

        public float getTntBaseExplosionPower() { return tntBaseExplosionPower.get().floatValue(); }

        public float getTntPowerPerStack() { return tntPowerPerStack.get().floatValue(); }

        public float getTntMaxExplosionPower() { return tntMaxExplosionPower.get().floatValue(); }
    }
}