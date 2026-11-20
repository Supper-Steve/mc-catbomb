package com.steve.catbomb;

import com.mojang.logging.LogUtils;
import com.steve.catbomb.config.CatFightConfig;
import com.steve.catbomb.constants.ModConstants;
import com.steve.catbomb.network.NetworkPacketManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.slf4j.Logger;

import static org.antlr.runtime.debug.DebugEventListener.PROTOCOL_VERSION;

@Mod(ModConstants.MOD_ID)
public class MainClass
{
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            createResource(),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    public MainClass(FMLJavaModLoadingContext context) {
        NetworkPacketManager.registerAll(CHANNEL);

        context.registerConfig(ModConfig.Type.COMMON, CatFightConfig.SPEC);
    }
    @SuppressWarnings("removal")
    public static ResourceLocation createResource(){
        try {
            try {
                return ResourceLocation.parse(ModConstants.MOD_ID + ":network_sync_channel");
            } catch (Exception exception) {
                return ResourceLocation.of(ModConstants.MOD_ID + ":network_sync_channel", '/');
            }
        }catch (Exception exception){
            LOGGER.error("catbomb ex ->", exception);
            return null;
        }
    }
}
