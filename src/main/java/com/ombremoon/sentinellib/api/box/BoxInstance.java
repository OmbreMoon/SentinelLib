package com.ombremoon.sentinellib.api.box;

import com.ombremoon.sentinellib.Constants;
import com.ombremoon.sentinellib.api.BoxUtil;
import com.ombremoon.sentinellib.common.BoxInstanceManager;
import com.ombremoon.sentinellib.common.ISentinel;
import com.ombremoon.sentinellib.networking.ModNetworking;
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

/**
 * This class responsible for handling the instance-specific information of the {@link SentinelBox}, which includes its position in space, as well as the box's attack conditions and/or other functionalities.
 */
public class BoxInstance {
    private final SentinelBox sentinelBox;
    private final LivingEntity boxOwner;
    private boolean start = false;
    private boolean isActive;
    public int tickCount = 0;
    private float yRot;
    private float yRot0;
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

    /**
     * Returns the sentinel box being ticked
     * @return The sentinel box the instance is dependent on
     */
    @Nullable
    public SentinelBox getSentinelBox() {
        return this.sentinelBox;
    }

    /**
     * Returns the owner of the box instance
     * @return The sentinel that triggered the box instance
     */
    public LivingEntity getBoxOwner() {
        return this.boxOwner;
    }

    /**
     * Checks if the box should hurt colliding entities/function as intended
     * @return Whether the sentinel box is active
     */
    public boolean isActive() {
        return this.isActive;
    }

    /**
     * Gets the center of the sentinel box
     * @return A 3D vector of the center position of the sentinel box
     */
    public Vec3 getCenter() {
        return this.centerVec;
    }

    /**
     * Handles the operations of the box instance every tick
     */
    public void tick() {
        this.tickCount++;
        if (this.boxOwner == null) {
            Constants.LOG.warn("Sentinel box does not have an owner and will not function as intended");
            return;
        }

        if (this.sentinelBox.getStopPredicate().test(this.boxOwner))
            this.deactivateBox();

        if (this.boxOwner.level().isClientSide) {
            var rotation = sentinelBox.getProperRotation(this.boxOwner);
            float f0 = rotation.getFirst();
            float f1 = rotation.getSecond();
            ((ISentinel)this.boxOwner).getBoxManager().setBoxRotation(f0, f1);
            ModNetworking.syncRotation(this.boxOwner.getId(), f0, f1);
        }

        int duration = this.sentinelBox.getDuration();
        if (!this.sentinelBox.hasDuration() || this.tickCount <= duration) {
            Matrix4f matrix4f = this.sentinelBox.getMoverType().isDynamic() ? MatrixHelper.getTranslatedEntityMatrix(this.boxOwner, this, 1.0F) : MatrixHelper.getEntityMatrix(this.boxOwner, 1.0F);

            this.updatePositionAndRotation(matrix4f);

            if (this.sentinelBox.getActiveDuration().test(this.boxOwner, this.tickCount)) {
                this.isActive = true;
                this.checkEntityInside(this.boxOwner);
                this.sentinelBox.onActiveTick().accept(this.boxOwner);
            } else {
                this.isActive = false;
            }
        } else {
            this.deactivateBox();
        }
        this.sentinelBox.onBoxTick().accept(this.boxOwner);
    }

    /**
     * Checks to see if there are entities colliding with the sentinel box and performs its tasks if so.<br> This is only called when the box instance is active.
     * @param owner The owner of the box instance (will be ignored in the check)
     */
    public void checkEntityInside(LivingEntity owner) {
        if (!owner.level().isClientSide) {
            List<Entity> entityList = sentinelBox.getEntityCollisions(owner, this);
            for (Entity entity : entityList) {
                LivingEntity livingEntity = (LivingEntity) entity;
                if (sentinelBox.getAttackCondition().test(livingEntity)) {
                    if (!this.hurtEntities.contains(livingEntity)) {
                        ResourceKey<DamageType> damageType = sentinelBox.getDamageType();
                        if (damageType != null) {
                            livingEntity.hurt(BoxUtil.sentinelDamageSource(livingEntity.level(), damageType, owner), sentinelBox.getDamageAmount());
                            sentinelBox.onHurtTick().accept(this.boxOwner, livingEntity);
                        }

                        this.hurtEntities.add(livingEntity);
                    }
                }
            }
            this.hurtEntities.clear();
        }
    }

    /**
     * Deactivates the box instance and removes it from the Sentinel's {@link BoxInstanceManager}.
     */
    public void deactivateBox() {
        this.sentinelBox.onBoxStop().accept(this.boxOwner);
        ((ISentinel)this.boxOwner).getBoxManager().removeInstance(this);
    }

    /**
     * Updates the position and rotation of the box instance. This is called every tick.
     * @param matrix4f The 4x4 transformation matrix
     */
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
}
