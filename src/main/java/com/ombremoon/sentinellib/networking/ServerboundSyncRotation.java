package com.ombremoon.sentinellib.networking;

import com.ombremoon.sentinellib.common.ISentinel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundSyncRotation {
    private final int entityID;
    private final float yRot;
    private final float yRot0;

    public ServerboundSyncRotation(int entityID, float yRot, float yRot0) {
        this.entityID = entityID;
        this.yRot = yRot;
        this.yRot0 = yRot0;
    }

    public ServerboundSyncRotation(final FriendlyByteBuf buf) {
        this.entityID = buf.readInt();
        this.yRot = buf.readFloat();
        this.yRot0 = buf.readFloat();
    }

    public void encode(final FriendlyByteBuf buf) {
        buf.writeInt(this.entityID);
        buf.writeFloat(this.yRot);
        buf.writeFloat(this.yRot0);
    }

    public static void handle(ServerboundSyncRotation packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            final var context = ctx.get();
            final var handler = context.getNetworkManager().getPacketListener();
            if (handler instanceof ServerGamePacketListenerImpl listener) {
                Level level = listener.player.serverLevel();
                Entity entity = level.getEntity(packet.entityID);

                if (entity == null)
                    return;

                if (entity instanceof ISentinel sentinel) {
                    sentinel.getBoxManager().setBoxRotation(packet.yRot, packet.yRot0);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
