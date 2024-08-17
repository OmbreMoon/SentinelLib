package com.ombremoon.sentinellib.example;

import com.ombremoon.sentinellib.CommonClass;
import com.ombremoon.sentinellib.Constants;
import com.ombremoon.sentinellib.SentinelLib;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
final class SLClient {
    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        if (CommonClass.isDevEnv())
            event.registerEntityRenderer(SentinelLib.MIST.get(), IceMistRenderer::new);
    }
}
