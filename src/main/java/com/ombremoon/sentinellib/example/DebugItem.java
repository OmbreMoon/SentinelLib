package com.ombremoon.sentinellib.example;

import com.ombremoon.sentinellib.SentinelLib;
import com.ombremoon.sentinellib.util.BoxUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DebugItem extends Item {

    public DebugItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack itemStack = pPlayer.getItemInHand(pUsedHand);
        if (!pLevel.isClientSide) {
            BoxUtil.triggerPlayerBox(pPlayer, SentinelLib.SCRATCH);
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }
}
