package com.steve.catbomb.network;

import com.steve.catbomb.client.ClientTntCache;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class TntNetworkPack {
    private final UUID catId;
    private final int count;
    private final boolean lit;

    public TntNetworkPack(UUID catId, int count, boolean lit) {
        this.catId = catId;
        this.count = count;
        this.lit = lit;
    }

    public static void encode(TntNetworkPack msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.catId);
        buf.writeInt(msg.count);
        buf.writeBoolean(msg.lit);
    }

    public static TntNetworkPack decode(FriendlyByteBuf buf) {
        return new TntNetworkPack(buf.readUUID(), buf.readInt(), buf.readBoolean());
    }

    public static void handle(TntNetworkPack msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ClientTntCache.put(msg.catId, msg.count, msg.lit));
        ctx.get().setPacketHandled(true);
    }
}