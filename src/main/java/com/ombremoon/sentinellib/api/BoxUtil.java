package com.ombremoon.sentinellib.api;

import com.ombremoon.sentinellib.api.box.SentinelBox;
import com.ombremoon.sentinellib.common.IPlayerSentinel;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class BoxUtil {

    /**
     * Triggers a sentinel box for the player
     * @param player
     * @param sentinelBox
     */
    public static void triggerPlayerBox(Player player, SentinelBox sentinelBox) {
        IPlayerSentinel sentinel = (IPlayerSentinel) player;
        sentinel.triggerSentinelBox(sentinelBox);
    }

    /**
     * Useful call for modded damage types
     * @param level
     * @param damageType
     * @param attackEntity
     * @return
     */
    public static DamageSource sentinelDamageSource(Level level, ResourceKey<DamageType> damageType, Entity attackEntity) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(damageType), attackEntity);
    }

    /*public static void registerSentinelModel(ISentinel sentinel, GeoModel<? extends ISentinel> model) {
        sentinel.getBoxManager().setModel(model);
    }*/
}
