package com.ombremoon.sentinellib;

import com.ombremoon.sentinellib.common.*;
import com.ombremoon.sentinellib.example.DebugItem;
import com.ombremoon.sentinellib.networking.ModNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.tslat.smartbrainlib.SBLConstants;
import software.bernie.geckolib.event.GeoRenderEvent;

@Mod(Constants.MOD_ID)
@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
public class SentinelLib {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Constants.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);

    public static RegistryObject<Item> DEBUG;

    public static final SentinelBox SCRATCH = SentinelBox.Builder.of("scratch")
            .sizeAndOffset(0.3F, 1, 0, 1, 1)
            .activeTicks((entity, integer) -> integer == 14)
            .typeDamage(DamageTypes.FREEZE, 15).build();

    public SentinelLib() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);

        if (CommonClass.isDevEnv())
            registerDebugItem(modEventBus);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModNetworking::registerPackets);
    }

    @SubscribeEvent
    public static void renderSentinelBox(RenderLivingEvent.Pre<? extends LivingEntity, ? extends EntityModel<? extends LivingEntity>> event) {
        Entity entity = event.getEntity();
        Minecraft minecraft = Minecraft.getInstance();

        if (entity.level() == null) {
            return;
        }

        if (minecraft.getEntityRenderDispatcher().shouldRenderHitBoxes() && !minecraft.showOnlyReducedInfo() && entity instanceof ISentinel sentinel) {
            BoxInstanceManager manager = sentinel.getBoxManager();
            for (BoxInstance instance : manager.getInstances()) {
                sentinel.renderBox(instance, entity, event.getPoseStack(), event.getMultiBufferSource().getBuffer(RenderType.lines()), event.getPartialTick());
            }
        }
    }

    @SubscribeEvent
    public static void registerSentinelBox(RegisterPlayerSentinelBoxEvent event) {
        event.getSentinelBoxEntry().add(SCRATCH);
    }

/*    @SubscribeEvent
    public static void renderGeoSentinelBox(GeoRenderEvent.Entity.Post event) {
        Entity entity = event.getEntity();
        Minecraft minecraft = Minecraft.getInstance();

        if (entity.level() == null) {
            return;
        }

        if (minecraft.getEntityRenderDispatcher().shouldRenderHitBoxes() && !minecraft.showOnlyReducedInfo() && entity instanceof ISentinel sentinel) {
            BoxInstanceManager manager = sentinel.getBoxManager();
            for (BoxInstance instance : manager.getInstances()) {
                sentinel.renderBox(instance, entity, event.getPoseStack(), event.getBufferSource().getBuffer(RenderType.lines()), event.getPartialTick());
            }
        }
    }*/

    private static void registerDebugItem(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
        DEBUG = ITEMS.register("debug", () -> new DebugItem(new Item.Properties()));
    }
}
