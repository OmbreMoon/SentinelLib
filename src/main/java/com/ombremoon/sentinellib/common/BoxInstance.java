package com.ombremoon.sentinellib.common;

import com.ombremoon.sentinellib.Constants;
import com.ombremoon.sentinellib.util.BoxUtil;
import com.ombremoon.sentinellib.util.MatrixHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
import java.util.List;

public class BoxInstance {
    private final SentinelBox sentinelBox;
    private final LivingEntity boxOwner;
    private boolean isActive;
    private int tickCount = 0;
    private Vec3 centerVec;
    protected Vec3[] instanceVertices;
    protected Vec3[] instanceNormals;
    public final List<LivingEntity> hurtEntities = new ObjectArrayList<>();


    public BoxInstance(@Nullable SentinelBox sentinelBox, LivingEntity boxOwner) {
        this.sentinelBox = sentinelBox;
        this.boxOwner = boxOwner;
        this.centerVec = new Vec3(0.0F, 0.0F, 0.0F);
        this.instanceVertices = new Vec3[]{new Vec3(0.0F, 0.0F, 0.0F), new Vec3(0.0F, 0.0F, 0.0F), new Vec3(0.0F, 0.0F, 0.0F), new Vec3(0.0F, 0.0F, 0.0F)};
        this.instanceNormals = new Vec3[]{new Vec3(0.0F, 0.0F, 0.0F), new Vec3(0.0F, 0.0F, 0.0F), new Vec3(0.0F, 0.0F, 0.0F)};
    }

    public BoxInstance(LivingEntity entity) {
        this(null, entity);
        AABB aabb = entity.getBoundingBox();
        double xSize = (aabb.maxX - aabb.minX) / 2;
        double ySize = (aabb.maxY - aabb.minY) / 2;
        double zSize = (aabb.maxZ - aabb.minZ) / 2;
        this.centerVec = new Vec3(-((float)aabb.minX + xSize), (float)aabb.minY + ySize, -((float)aabb.minZ + zSize));
        this.instanceVertices = new Vec3[]{new Vec3(-xSize, ySize, -zSize), new Vec3(-xSize, ySize, zSize), new Vec3(xSize, ySize, zSize), new Vec3(xSize, ySize, -zSize)};
        this.instanceNormals = new Vec3[]{new Vec3(1.0F, 0.0F, 0.0F), new Vec3(0.0F, 1.0F, 0.0F), new Vec3(0.0F, 0.0F, 1.0F)};
    }

    @Nullable
    public SentinelBox getSentinelBox() {
        return this.sentinelBox;
    }

    public LivingEntity getBoxOwner() {
        return this.boxOwner;
    }

    public boolean isActive() {
        return this.isActive;
    }

    public Vec3 getCenter() {
        return this.centerVec;
    }

    public void tick() {
        this.tickCount++;
        if (this.boxOwner == null) {
            Constants.LOG.warn("Sentinel box does not have an owner and will not function as intended");
            return;
        }

        int duration = this.sentinelBox.getDuration();
        if (this.tickCount <= duration) {
            Matrix4f matrix4f = MatrixHelper.getEntityMatrix(this.boxOwner, 1.0F);
            this.updatePositionAndRotation(matrix4f);

            if (this.sentinelBox.getActiveDuration().test(this.boxOwner, this.tickCount)) {
                this.isActive = true;
                this.checkEntityInside(this.boxOwner);
            } else {
                this.isActive = false;
            }
        } else {
            this.deactivateBox();
        }
    }

    public void checkEntityInside(LivingEntity owner) {
        if (!owner.level().isClientSide) {
            List<Entity> entityList = sentinelBox.getEntityCollisions(owner, this);
            for (Entity entity : entityList) {
                LivingEntity livingEntity = (LivingEntity) entity;
                if (sentinelBox.getAttackCondition().test(livingEntity)) {
                    if (!this.hurtEntities.contains(livingEntity)) {
                        sentinelBox.getAttackConsumer().accept(livingEntity);
                        ResourceKey<DamageType> damageType = sentinelBox.getDamageType();
                        if (damageType != null)
                            livingEntity.hurt(BoxUtil.sentinelDamageSource(livingEntity.level(), damageType, owner), sentinelBox.getDamageAmount());

                        this.hurtEntities.add(livingEntity);
                    }
                }
            }
            this.hurtEntities.clear();
        }
    }

    public void deactivateBox() {
        ((ISentinel)this.boxOwner).getBoxManager().removeInstance(this);
    }

    public void updatePositionAndRotation(Matrix4f matrix4f) {
        Matrix4f correctMatrix = new Matrix4f(matrix4f);
        correctMatrix.setTranslation(0.0F, 0.0F, 0.0F);

        for (int i = 0; i < this.instanceVertices.length; i++) {
            this.instanceVertices[i] = MatrixHelper.transform(correctMatrix, sentinelBox.getVertex(i));
        }

        for (int i = 0; i < this.instanceNormals.length; i++) {
            this.instanceNormals[i] = MatrixHelper.transform(correctMatrix, sentinelBox.getNormal(i));
        }

        this.centerVec = MatrixHelper.transform(matrix4f, this.sentinelBox.getBoxOffset().multiply(-1.0F, 1.0F, -1.0F));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof BoxInstance boxInstance)) {
            return false;
        } else {
            return this.sentinelBox == boxInstance.sentinelBox && this.boxOwner.getId() == boxInstance.boxOwner.getId();
        }
    }

    private static void log(Object o) {
        Constants.LOG.info(String.valueOf(o));
    }
}
