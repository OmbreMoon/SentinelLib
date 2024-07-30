package com.ombremoon.sentinellib.common.event;

import com.ombremoon.sentinellib.common.SentinelBox;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.List;

public class RegisterPlayerSentinelBoxEvent extends PlayerEvent {
    private final List<SentinelBox> boxList;

    public RegisterPlayerSentinelBoxEvent(Player player, List<SentinelBox> boxList) {
        super(player);
        this.boxList = boxList;
    }

    public List<SentinelBox> getSentinelBoxEntry() {
        return this.boxList;
    }

    public static List<SentinelBox> registerSentinelBox(Player player, List<SentinelBox> sentinelBoxes) {
        RegisterPlayerSentinelBoxEvent event = new RegisterPlayerSentinelBoxEvent(player, sentinelBoxes);
        if (MinecraftForge.EVENT_BUS.post(event)) return new ObjectArrayList<>();
        return event.getSentinelBoxEntry();
    }
}
