package com.ombremoon.sentinellib.networking;

import com.ombremoon.sentinellib.common.ISentinel;
import com.ombremoon.sentinellib.common.SentinelBox;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundRemoveSentinelBox {
    private final int entityID;
    private final String boxID;

    public ClientboundRemoveSentinelBox(int entityID, String boxID) {
        this.entityID = entityID;
        this.boxID = boxID;
    }

    public ClientboundRemoveSentinelBox(final FriendlyByteBuf buf) {
        this.entityID = buf.readInt();
        this.boxID = buf.readUtf();
    }

    public void encode(final FriendlyByteBuf buf) {
        buf.writeInt(this.entityID);
        buf.writeUtf(this.boxID);
    }

    public static void handle(ClientboundRemoveSentinelBox packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            final var context = ctx.get();
            final var handler = context.getNetworkManager().getPacketListener();
            if (handler instanceof ClientGamePacketListener) {
                Level level = Minecraft.getInstance().level;
                Entity entity = level.getEntity(packet.entityID);

                if (entity == null)
                    return;

                if (entity instanceof ISentinel sentinel) {
                    SentinelBox sentinelBox = sentinel.getBoxFromID(packet.boxID);

                    if (sentinelBox == null)
                        return;

                    sentinel.removeSentinelInstance(sentinelBox);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
