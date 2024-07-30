package com.ombremoon.sentinellib.util;

import com.ombremoon.sentinellib.common.IPlayerSentinel;
import com.ombremoon.sentinellib.common.SentinelBox;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class BoxUtil {

    public static void triggerPlayerBox(Player player, SentinelBox sentinelBox) {
        IPlayerSentinel sentinel = (IPlayerSentinel) player;
        sentinel.triggerSentinelBox(sentinelBox);
    }

    public static DamageSource sentinelDamageSource(Level level, ResourceKey<DamageType> damageType, Entity attackEntity) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(damageType), attackEntity);
    }
}
