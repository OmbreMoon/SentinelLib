package com.ombremoon.sentinellib;

import com.ombremoon.sentinellib.common.BoxInstance;
import com.ombremoon.sentinellib.common.BoxInstanceManager;
import com.ombremoon.sentinellib.common.ISentinel;
import com.ombremoon.sentinellib.common.OBBSentinelBox;
import com.ombremoon.sentinellib.common.event.RegisterPlayerSentinelBoxEvent;
import com.ombremoon.sentinellib.networking.ModNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Constants.MOD_ID)
@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
public class SentinelLib {
    public static final OBBSentinelBox TEST = OBBSentinelBox.Builder.of("test")
            .sizeAndOffset(1, 0, 1, 1)
            .activeTicks((entity, integer) -> integer == 14)
            .typeDamage(DamageTypes.FREEZE, 15).build();

    public SentinelLib() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        CommonClass.init(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModNetworking::registerPackets);
    }

    @SubscribeEvent
    public static void renderSentinelBox(RenderLivingEvent.Pre<? extends LivingEntity, ? extends EntityModel<? extends LivingEntity>> event) {
        LivingEntity entity = event.getEntity();
        Minecraft minecraft = Minecraft.getInstance();

        if (entity.level() == null) {
            return;
        }

        if (minecraft.getEntityRenderDispatcher().shouldRenderHitBoxes() && !minecraft.showOnlyReducedInfo() && entity instanceof ISentinel sentinel) {
            BoxInstanceManager manager = sentinel.getBoxManager();
            for (BoxInstance instance : manager.getInstances()) {
                instance.getSentinelBox().renderBox(instance, entity, event.getPoseStack(), event.getMultiBufferSource().getBuffer(RenderType.lines()), event.getPartialTick(), instance.isActive() ? 0.0F : 1.0F);
            }
        }
    }

    @SubscribeEvent
    public static void registerSentinelBox(RegisterPlayerSentinelBoxEvent event) {
        event.getSentinelBoxEntry().add(TEST);
    }

    /*@SubscribeEvent
    public static void renderGeoSentinelBox(GeoRenderEvent.Entity.Post event) {
        Entity entity = event.getEntity();
        Minecraft minecraft = Minecraft.getInstance();

        if (!(entity instanceof LivingEntity livingEntity))
            return;

        if (entity.level() == null)
            return;

        if (minecraft.getEntityRenderDispatcher().shouldRenderHitBoxes() && !minecraft.showOnlyReducedInfo() && livingEntity instanceof ISentinel sentinel) {
            BoxInstanceManager manager = sentinel.getBoxManager();
            for (BoxInstance instance : manager.getInstances()) {
                instance.getSentinelBox().renderBox(instance, livingEntity, event.getPoseStack(), event.getBufferSource().getBuffer(RenderType.lines()), event.getPartialTick(), instance.isActive() ? 0.0F : 1.0F);
            }
        }
    }*/

}
