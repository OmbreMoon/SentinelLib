package com.ombremoon.sentinellib.networking;

import com.ombremoon.sentinellib.common.IPlayerSentinel;
import com.ombremoon.sentinellib.common.ISentinel;
import com.ombremoon.sentinellib.common.SentinelBox;
import com.ombremoon.sentinellib.util.BoxUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundRegisterSentinelBox {
    private final int entityID;
    private final String boxID;

    public ClientboundRegisterSentinelBox(int entityID, String boxID) {
        this.entityID = entityID;
        this.boxID = boxID;
    }

    public ClientboundRegisterSentinelBox(final FriendlyByteBuf buf) {
        this.entityID = buf.readInt();
        this.boxID = buf.readUtf();
    }

    public void encode(final FriendlyByteBuf buf) {
        buf.writeInt(this.entityID);
        buf.writeUtf(this.boxID);
    }

    public static void handle(ClientboundRegisterSentinelBox packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            final var context = ctx.get();
            final var handler = context.getNetworkManager().getPacketListener();
            if (handler instanceof ClientGamePacketListener) {
                Level level = Minecraft.getInstance().level;
                Entity entity = level.getEntity(packet.entityID);

                if (entity == null)
                    return;

                if (entity instanceof IPlayerSentinel sentinel) {
                    SentinelBox sentinelBox = sentinel.getBoxFromID(packet.boxID);

                    if (sentinelBox == null)
                        return;

//                    BoxUtil.triggerPlayerBox((Player) sentinel.getSentinel(), sentinelBox, false);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
