package com.ombremoon.sentinellib;

import com.ombremoon.sentinellib.api.Easing;
import com.ombremoon.sentinellib.api.box.BoxInstance;
import com.ombremoon.sentinellib.api.box.OBBSentinelBox;
import com.ombremoon.sentinellib.api.box.SentinelBox;
import com.ombremoon.sentinellib.common.BoxInstanceManager;
import com.ombremoon.sentinellib.common.IPlayerSentinel;
import com.ombremoon.sentinellib.common.ISentinel;
import com.ombremoon.sentinellib.common.event.RegisterPlayerSentinelBoxEvent;
import com.ombremoon.sentinellib.networking.ModNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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
import software.bernie.geckolib.event.GeoRenderEvent;

@Mod(Constants.MOD_ID)
@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
public class SentinelLib {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Constants.MOD_ID);

    public static final OBBSentinelBox TEST_ELASTIC = OBBSentinelBox.Builder.of("test")
            .sizeAndOffset(0.5F, 0.0F, 1, 1.0F)
            .activeTicks((entity, integer) -> integer > 0)
            .boxDuration(100)
            .moverType(SentinelBox.MoverType.CUSTOM_HEAD)
            .defineMovement(SentinelBox.MovementAxis.Z_TRANSLATION, (ticks, partialTicks) -> {
                return Easing.BOUNCE_OUT.easing(3.0F, (float) ticks / 100);
            })
            .typeDamage(DamageTypes.FREEZE, 15).build();

    public static final OBBSentinelBox TEST_CIRCLE = OBBSentinelBox.Builder.of("circle")
            .sizeAndOffset(0.5F, 0, 1, 0)
            .activeTicks((entity, integer) -> integer > 0)
            .noDuration(Entity::onGround)
            .moverType(SentinelBox.MoverType.CUSTOM)
            .circleMovement(2.0F, 0.15F)
            .defineMovement(SentinelBox.MovementAxis.Y_TRANSLATION, (ticks, partialTicks) -> 3 * (float) Math.sin(0.05F * ticks))
            .typeDamage(DamageTypes.FREEZE, 15).build();

    public static final OBBSentinelBox BEAM_BOX = OBBSentinelBox.Builder.of("beam")
            .sizeAndOffset(0.3F, 0.3F, 2, 0.0F, 1.7F, 3)
            .boxDuration(100)
            .activeTicks((entity, integer) -> integer > 45 && integer % 10 == 0)
            .typeDamage(DamageTypes.FREEZE, 15).build();

    public SentinelLib() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        renderGeckolibSentinelBoxes(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
        ITEMS.register(modEventBus);
        if (CommonClass.isDevEnv()) {
            ITEMS.register("debug", () -> new DebugItem(new Item.Properties()));
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

    private void renderGeckolibSentinelBoxes(IEventBus modEventBus) {
        if (ModList.get().isLoaded("geckolib")) {
            modEventBus.addListener(event -> {
                if (event instanceof GeoRenderEvent.Entity.Post renderEvent) {
                    Entity entity = renderEvent.getEntity();
                    Minecraft minecraft = Minecraft.getInstance();

                    if (!(entity instanceof LivingEntity livingEntity))
                        return;

                    if (entity.level() == null)
                        return;

                    if (minecraft.getEntityRenderDispatcher().shouldRenderHitBoxes() && !minecraft.showOnlyReducedInfo() && livingEntity instanceof ISentinel sentinel) {
                        BoxInstanceManager manager = sentinel.getBoxManager();
                        for (BoxInstance instance : manager.getInstances()) {
                            instance.getSentinelBox().renderBox(instance, livingEntity, renderEvent.getPoseStack(), renderEvent.getBufferSource().getBuffer(RenderType.lines()), renderEvent.getPartialTick(), instance.isActive() ? 0.0F : 1.0F);
                        }
                    }
                }
            });
        }
    }
}
