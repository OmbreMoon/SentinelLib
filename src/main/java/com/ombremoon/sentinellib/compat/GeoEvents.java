package com.ombremoon.sentinellib.compat;

import com.ombremoon.sentinellib.Constants;
import com.ombremoon.sentinellib.api.box.BoxInstance;
import com.ombremoon.sentinellib.common.BoxInstanceManager;
import com.ombremoon.sentinellib.common.ISentinel;
import com.ombremoon.sentinellib.common.ISentinelRenderer;
import com.ombremoon.sentinellib.example.IceMist;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import software.bernie.geckolib.event.GeoRenderEvent;

public class GeoEvents {

    private static GeoEvents instance;

    public static GeoEvents getInstance() {
        if (instance == null) {
            instance = new GeoEvents();
        }
        return instance;
    }

    @SubscribeEvent
    public void renderGeckolibSentinelBoxes(GeoRenderEvent.Entity.Post event) {
        Entity entity = event.getEntity();
        Minecraft minecraft = Minecraft.getInstance();

        if (entity.level() == null)
            return;

        if (entity instanceof ISentinel sentinel) {
            if (minecraft.getEntityRenderDispatcher().shouldRenderHitBoxes() && !minecraft.showOnlyReducedInfo()) {
                BoxInstanceManager manager = sentinel.getBoxManager();
                for (BoxInstance instance : manager.getInstances()) {
                    instance.getSentinelBox().renderBox(instance, entity, event.getPoseStack(), event.getBufferSource().getBuffer(RenderType.lines()), event.getPartialTick(), instance.isActive() ? 0.0F : 1.0F);
                }
            }

            if (event.getRenderer() instanceof ISentinelRenderer<?> renderer) {
                renderer.trackSentinelModel(event.getModel());
//                var bone = event.getModel().getBone("mist_center").get();
//                Constants.LOG.info(String.valueOf(bone.getWorldSpaceMatrix().m30()));

            }
        }
    }
}
