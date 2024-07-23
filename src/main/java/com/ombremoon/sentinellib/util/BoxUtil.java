package com.ombremoon.sentinellib.util;

import com.ombremoon.sentinellib.common.IPlayerSentinel;
import com.ombremoon.sentinellib.common.SentinelBox;
import net.minecraft.world.entity.player.Player;

public class BoxUtil {

    public static void triggerPlayerBox(Player player, SentinelBox sentinelBox) {
        IPlayerSentinel sentinel = (IPlayerSentinel) player;
        sentinel.triggerSentinelBox(sentinelBox);
    }
}
