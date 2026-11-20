package com.steve.catbomb.network;

import com.steve.catbomb.MainClass;
import com.steve.catbomb.constants.NbtConstants;
import com.steve.catbomb.server.CatTntManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class NetworkPacketManager {
    public static void registerAll(SimpleChannel channel){
        MainClass.LOGGER.debug("registering network message, count '3'.");

        channel.registerMessage(0, TntNetworkPack.class,
                TntNetworkPack::encode,
                TntNetworkPack::decode,
                TntNetworkPack::handle);
        MainClass.LOGGER.debug("registered network msg 'TntSyncPacket'.");
    }
    public static void syncMessage(Player player){
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        var level = serverPlayer.serverLevel();

        for (Cat cat : level.getEntitiesOfClass(Cat.class, player.getBoundingBox().inflate(128))) {
            if (cat.getPersistentData().getBoolean(NbtConstants.CAT_TNT_TAG)) {
                var lit = cat.getPersistentData().getBoolean(NbtConstants.CAT_LIT_TAG);
                MainClass.CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer),
                        new TntNetworkPack(cat.getUUID(), CatTntManager.getTntCount(cat), lit));
            }
        }
    }
}
