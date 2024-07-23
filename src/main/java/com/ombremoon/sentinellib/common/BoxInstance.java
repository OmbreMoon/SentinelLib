package com.ombremoon.tugkansem.common.sentinel;

import com.ombremoon.tugkansem.Constants;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class BoxInstance {
    private final SentinelBox sentinelBox;
    private final Entity boxOwner;
    private final List<LivingEntity> hurtEntities = new ObjectArrayList<>();
    private boolean isActive;
    private int tickCount = 0;

    public BoxInstance(SentinelBox sentinelBox, Entity boxOwner) {
        this.sentinelBox = sentinelBox;
        this.boxOwner = boxOwner;
    }

    public SentinelBox getSentinelBox() {
        return this.sentinelBox;
    }

    public Entity getBoxOwner() {
        return this.boxOwner;
    }

    public AABB getSentinelBB() {
        return getSentinelBB(0.1F);
    }

    public AABB getSentinelBB(float partialTicks) {
        return this.sentinelBox.getSentinelBox().move(getBoxPosition(this.boxOwner, partialTicks));
    }

    public boolean isActive() {
        return this.isActive;
    }

    public void tick() {
        this.tickCount++;
        if (this.boxOwner == null) {
            Constants.LOG.warn("Sentinel box does not have an owner and will not function as intended");
            return;
        }

        int duration = this.sentinelBox.getDuration().apply(this.boxOwner);
        if (this.tickCount <= duration) {
            if (this.sentinelBox.getActiveDuration().test(this.boxOwner, this.tickCount)) {
                this.isActive = true;
                this.checkEntityInside();
            } else {
                this.isActive = false;
            }
        } else {
            this.deactivateBox();
        }
    }

    private void checkEntityInside() {
        if (!this.boxOwner.level().isClientSide) {
            List<Entity> entityList = this.boxOwner.level().getEntities(boxOwner, this.getSentinelBB());
            for (Entity entity : entityList) {
                if (entity instanceof LivingEntity livingEntity) {
                    if (this.sentinelBox.getAttackCondition().test(livingEntity)) {
                        if (!hurtEntities.contains(livingEntity)) {
                            this.sentinelBox.getAttackConsumer().accept(livingEntity);
                            ResourceKey<DamageType> damageType = sentinelBox.getDamageType();
                            if (damageType != null)
                                livingEntity.hurt(sentinelDamageSource(livingEntity.level(), damageType, this.boxOwner), sentinelBox.getDamageAmount());
                            hurtEntities.add(livingEntity);
                        }
                    }
                }
            }
            this.hurtEntities.clear();
        }
    }

    private Vec3 getBoxPosition(Entity entity, float partialTicks) {
        Vec3 pos = entity.getPosition(partialTicks);
        Vec3 vec3 = getProperOffset(entity);
        return new Vec3(pos.x + vec3.x, pos.y + vec3.y, pos.z + vec3.z);
    }

    public Vec3 getProperOffset(Entity entity) {
        Vec3 vec = this.sentinelBox.getBoxOffset();
        float xLookAngle = (-entity.getYHeadRot() + 90) * Mth.DEG_TO_RAD;
        float zLookAngle = -entity.getYHeadRot() * Mth.DEG_TO_RAD;
        Vec3 f = new Vec3(vec.z * Math.sin(zLookAngle), 0.0, vec.z * Math.cos(zLookAngle));
        Vec3 f1 = new Vec3(vec.x * Math.sin(xLookAngle), 0.0, vec.x * Math.cos(xLookAngle));
        return new Vec3(f.x + f1.x, vec.y, f.z + f1.z);
    }

    public Vec3 getCenter(float partialTicks) {
        return this.getSentinelBB(partialTicks).getCenter();
    }

    public void deactivateBox() {
        ((ISentinel)this.boxOwner).getBoxManager().removeInstance(this);
    }

    public static DamageSource sentinelDamageSource(Level level, ResourceKey<DamageType> damageType, Entity attackEntity) {
        return new DamageSource(level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(damageType), attackEntity);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof BoxInstance)) {
            return false;
        } else {
            BoxInstance boxInstance = (BoxInstance) obj;
            return this.sentinelBox == boxInstance.sentinelBox && this.boxOwner.getId() == boxInstance.boxOwner.getId();
        }
    }

    private static void log(Object o) {
        Constants.LOG.info(String.valueOf(o));
    }
}
