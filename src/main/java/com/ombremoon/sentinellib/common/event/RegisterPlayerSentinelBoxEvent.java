package com.ombremoon.sentinellib.common.event;

import com.ombremoon.sentinellib.api.box.SentinelBox;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.List;

/**
 * Event class used to register sentinel boxes for the player.<br>
 * These are fired on the {@link net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus FORGE} mod bus
 */
public class RegisterPlayerSentinelBoxEvent extends PlayerEvent {
    private final List<SentinelBox> boxList;

    public RegisterPlayerSentinelBoxEvent(Player player, List<SentinelBox> boxList) {
        super(player);
        this.boxList = boxList;
    }

    /**
     * @return The list of registered sentinel boxes on the player
     */
    public List<SentinelBox> getSentinelBoxEntry() {
        return this.boxList;
    }

    public static List<SentinelBox> registerSentinelBox(Player player, List<SentinelBox> sentinelBoxes) {
        RegisterPlayerSentinelBoxEvent event = new RegisterPlayerSentinelBoxEvent(player, sentinelBoxes);
        if (MinecraftForge.EVENT_BUS.post(event)) return new ObjectArrayList<>();
        return event.getSentinelBoxEntry();
    }
}
