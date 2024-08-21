package com.ombremoon.sentinellib;

import com.ombremoon.sentinellib.api.Easing;
import com.ombremoon.sentinellib.api.box.BoxInstance;
import com.ombremoon.sentinellib.api.box.OBBSentinelBox;
import com.ombremoon.sentinellib.api.box.SentinelBox;
import com.ombremoon.sentinellib.common.BoxInstanceManager;
import com.ombremoon.sentinellib.common.IPlayerSentinel;
import com.ombremoon.sentinellib.common.ISentinel;
import com.ombremoon.sentinellib.common.event.RegisterPlayerSentinelBoxEvent;
import com.ombremoon.sentinellib.compat.GeoEvents;
import com.ombremoon.sentinellib.example.IceMist;
import com.ombremoon.sentinellib.example.IceMistRenderer;
import com.ombremoon.sentinellib.networking.ModNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.function.Supplier;

@Mod(Constants.MOD_ID)
@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
public class SentinelLib {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Constants.MOD_ID);

    public static RegistryObject<EntityType<IceMist>> MIST;

    public static final OBBSentinelBox TEST_ELASTIC = OBBSentinelBox.Builder.of("test")
            .sizeAndOffset(2F, 0.0F, 1, 1.0F)
            .activeTicks((entity, integer) -> integer > 0)
            .boxDuration(100)
            .moverType(SentinelBox.MoverType.CUSTOM_HEAD)
            .scaleOut(SentinelBox.MovementAxis.X_TRANSLATION, ticks -> Easing.QUAD_IN.easing((float) ticks / 100))
            .scaleOut(SentinelBox.MovementAxis.Y_TRANSLATION, ticks -> Easing.QUAD_IN.easing((float) ticks / 100))
            .scaleOut(SentinelBox.MovementAxis.Z_TRANSLATION, ticks -> Easing.QUAD_IN.easing((float) ticks / 100))
            .defineMovement(SentinelBox.MovementAxis.Z_TRANSLATION, (ticks, partialTicks) -> {
                return /*Easing.BOUNCE_OUT.easing(3.0F, (float) ticks / 100)*/0F;
            })
            .typeDamage(DamageTypes.FREEZE, 15).build();

    public static final OBBSentinelBox TEST_CIRCLE = OBBSentinelBox.Builder.of("circle")
            .sizeAndOffset(0.5F, 0, 0.5F, 0)
            .activeTicks((entity, integer) -> integer > 0)
            .boxDuration(100)
            .moverType(SentinelBox.MoverType.CUSTOM_HEAD)
            .circleMovement(2.0F, 0.15F)
//            .defineMovement(SentinelBox.MovementAxis.X_TRANSLATION, (ticks, partialTicks) -> {
//                float f0 = Easing.QUART_IN_OUT.easing((float) (13.3F * Math.PI), (float) ticks / 100);
//                return 2 * (float) Math.sin(0.15F * f0);
//            })
//            .defineMovement(SentinelBox.MovementAxis.Z_TRANSLATION, (ticks, partialTicks) -> {
//                float f0 = Easing.QUART_IN_OUT.easing((float) (13.3F * Math.PI), (float) ticks / 100);
//                return 2 * (float) Math.cos(0.15F * f0);
//            })
            .typeDamage(DamageTypes.FREEZE, 15).build();

    public static final OBBSentinelBox BEAM_BOX = OBBSentinelBox.Builder.of("beam")
            .sizeAndOffset(0.3F, 0.3F, 3, 0.0F, 1.7F, 4)
            .boxDuration(100)
            .activeTicks((entity, integer) -> integer > 45 && integer % 10 == 0)
            .typeDamage(DamageTypes.FREEZE, 15).build();

    public SentinelLib() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        renderGeckolibSentinelBoxes();
        MinecraftForge.EVENT_BUS.register(this);
        ITEMS.register(modEventBus);
        ENTITY_TYPES.register(modEventBus);
        if (CommonClass.isDevEnv()) {
            ITEMS.register("debug", () -> new DebugItem(new Item.Properties()));
            MIST = ENTITY_TYPES.register("ice_mist", () -> EntityType.Builder.<IceMist>of(IceMist::new, MobCategory.MISC).sized(1, 1).clientTrackingRange(64).build("ice_mist"));
        }
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
    public static void tickPlayerBoxes(TickEvent.PlayerTickEvent event) {
        IPlayerSentinel sentinel = (IPlayerSentinel) event.player;
        sentinel.tickBoxes();
    }

    @SubscribeEvent
    public static void registerSentinelBox(RegisterPlayerSentinelBoxEvent event) {
        event.getSentinelBoxEntry().add(TEST_ELASTIC);
        event.getSentinelBoxEntry().add(TEST_CIRCLE);
        event.getSentinelBoxEntry().add(BEAM_BOX);
    }

    private void renderGeckolibSentinelBoxes() {
        if (ModList.get().isLoaded("geckolib")) {
            MinecraftForge.EVENT_BUS.register(GeoEvents.getInstance());
        }
    }

    public static <T extends Entity> List<Renderers> getRenderers() {
        return List.of(
                new Renderers(SentinelLib.MIST, IceMistRenderer::new)
        );
    }

    public record Renderers<T extends Entity>(Supplier<EntityType<T>> type, EntityRendererProvider<T> renderer) {
    }
}
