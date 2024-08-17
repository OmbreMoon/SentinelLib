package com.ombremoon.sentinellib.example;

import com.ombremoon.sentinellib.Constants;
import com.ombremoon.sentinellib.SentinelLib;
import com.ombremoon.sentinellib.api.box.SentinelBox;
import com.ombremoon.sentinellib.common.BoxInstanceManager;
import com.ombremoon.sentinellib.common.ISentinel;
import com.ombremoon.sentinellib.compat.GeoBoneOBBSentinelBox;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.UUID;

public class IceMist extends Entity implements TraceableEntity, GeoEntity, ISentinel {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private final BoxInstanceManager manager = new BoxInstanceManager(this);
    protected static final RawAnimation MIST = RawAnimation.begin().thenPlay("mist");
    @Nullable
    private LivingEntity owner;
    @Nullable
    private UUID ownerUUID;

    public static final GeoBoneOBBSentinelBox CENTER = GeoBoneOBBSentinelBox.Builder.of("mist_center")
            .sizeAndOffset(2.5F)
            .noDuration(entity -> !entity.isRemoved()).build();

    public IceMist(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public IceMist(Level pLevel, double pX, double pY, double pZ, LivingEntity iceQueen) {
        this(SentinelLib.MIST.get(), pLevel);
        this.setOwner(iceQueen);
        this.setPos(pX, pY, pZ);
    }

    @Override
    public BoxInstanceManager getBoxManager() {
        return this.manager;
    }

    @Override
    public List<SentinelBox> getSentinelBoxes() {
        return ObjectArrayList.of(CENTER);
    }

    public void setOwner(@Nullable LivingEntity pOwner) {
        this.owner = pOwner;
        this.ownerUUID = pOwner == null ? null : pOwner.getUUID();
    }

    @Nullable
    public LivingEntity getOwner() {
        if (this.owner == null && this.ownerUUID != null && this.level() instanceof ServerLevel) {
            Entity entity = ((ServerLevel)this.level()).getEntity(this.ownerUUID);
            if (entity instanceof LivingEntity) {
                this.owner = (LivingEntity)entity;
            }
        }

        return this.owner;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        if (pCompound.hasUUID("Owner")) {
            this.ownerUUID = pCompound.getUUID("Owner");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        if (this.ownerUUID != null) {
            pCompound.putUUID("Owner", this.ownerUUID);
        }
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public void tick() {
        super.tick();
//        this.refreshDimensions();
        List<Entity> entityList = this.level().getEntities(this.getOwner(), this.getBoundingBox());
        for (Entity entity : entityList) {
            if (entity instanceof LivingEntity livingEntity && this.tickCount % 20 == 0) {
                livingEntity.hurt(this.level().damageSources().freeze(), 2);
                //TODO: CHECK
                livingEntity.setIsInPowderSnow(true);
            }
        }
        if (this.tickCount >= 400) {
            this.discard();
        }

        if (this.tickCount == 1 && this.level().isClientSide) {
            triggerSentinelBox(CENTER);
        }
    }

/*    @Override
    public EntityDimensions getDimensions(Pose pPose) {
        float width = super.getDimensions(pPose).width;
        float scaledWidth = this.tickCount < 320 ? width * (0.4F * (1 + this.tickCount / 80.0F)) : width * 2.0F;
        return super.getDimensions(pPose).scale(scaledWidth, 1.0F);
    }*/

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "Mist", 0, state -> state.setAndContinue(MIST)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}