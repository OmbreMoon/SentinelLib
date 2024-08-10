package com.ombremoon.sentinellib;

import com.ombremoon.sentinellib.api.BoxUtil;
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
            if (pPlayer.isCrouching()) {
                BoxUtil.triggerPlayerBox(pPlayer, SentinelLib.TEST_CIRCLE);
            } else if (pPlayer.onGround()) {
                BoxUtil.triggerPlayerBox(pPlayer, SentinelLib.TEST_ELASTIC);
            } else {
                BoxUtil.triggerPlayerBox(pPlayer, SentinelLib.BEAM_BOX);
            }
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }
}
