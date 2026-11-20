package com.steve.catbomb.events.server;

import com.steve.catbomb.constants.ModConstants;
import com.steve.catbomb.network.NetworkPacketManager;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID)
public class NetworkSyncEvents {

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        var player = event.getEntity();
        NetworkPacketManager.syncMessage(player);
    }
}