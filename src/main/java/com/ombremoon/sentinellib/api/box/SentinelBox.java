package com.ombremoon.sentinellib.api.box;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * The base sentinel box class responsible for defining the hitbox and attack conditions. This class is abstracted and not intended for direct use. See either {@link AABBSentinelBox} or {@link OBBSentinelBox}.
 */
public abstract class SentinelBox {
    protected final AABB aabb;
    private final String name;
    private final Vec3 vertexPos;
    private final Vec3 boxOffset;
    private final int duration;
    private final BiPredicate<Entity, Integer> activeDuration;
    private final Predicate<LivingEntity> attackCondition;
    private final BiConsumer<LivingEntity, LivingEntity> attackConsumer;
    private final ResourceKey<DamageType> damageType;
    private final float damageAmount;
    private final MoverType moverType;
    private final boolean followsBody;
    private final Vec3[] vertices;
    private final Vec3[] normals;

    public SentinelBox(Builder builder) {
        this.aabb = builder.aabb;
        this.name = builder.name;
        this.vertexPos = builder.vertexPos;
        this.boxOffset = builder.boxOffset;
        this.duration = builder.duration;
        this.activeDuration = builder.activeDuration;
        this.attackCondition = builder.attackCondition;
        this.attackConsumer = builder.attackConsumer;
        this.damageType = builder.damageType;
        this.damageAmount = builder.damageAmount;
        this.moverType = builder.moverType;
        this.followsBody = builder.followsBody;
        this.vertices = new Vec3[]{new Vec3(vertexPos.x, vertexPos.y, -vertexPos.z), new Vec3(vertexPos.x, vertexPos.y, vertexPos.z), new Vec3(-vertexPos.x, vertexPos.y, vertexPos.z), new Vec3(-vertexPos.x, vertexPos.y, -vertexPos.z)};
        this.normals = new Vec3[]{new Vec3(1.0F, 0.0F, 0.0F), new Vec3(0.0F, 1.0F, 0.0F), new Vec3(0.0F, 0.0F, -1.0F)};
    }

    public abstract AABB getSentinelBB(BoxInstance instance);

    public abstract void renderBox(BoxInstance instance, LivingEntity entity, PoseStack poseStack, VertexConsumer vertexConsumer, float partialTicks, float isRed);

    public abstract List<Entity> getEntityCollisions(LivingEntity owner, BoxInstance instance);

    public Vec3 getVertex(int index) {
        return this.vertices[index];
    }

    public Vec3 getNormal(int index) {
        return this.normals[index];
    }

    public String getName() { return this.name; }

    public AABB getAABB() {
        return this.aabb;
    }

    public Vec3 getVertexPos() {
        return this.vertexPos;
    }

    public Vec3 getBoxOffset() {
        return this.boxOffset;
    }

    public int getDuration() {
        return this.duration;
    }

    public BiPredicate<Entity, Integer> getActiveDuration() {
        return this.activeDuration;
    }

    public Predicate<LivingEntity> getAttackCondition() {
        return this.attackCondition;
    }

    public BiConsumer<LivingEntity, LivingEntity> getAttackConsumer() {
        return this.attackConsumer;
    }

    public ResourceKey<DamageType> getDamageType() {
        return this.damageType;
    }

    public float getDamageAmount() {
        return this.damageAmount;
    }

    public MoverType getMoverType() {
        return this.moverType;
    }

    public boolean isFollowsBody() {
        return this.followsBody;
    }

    public enum MoverType {
        HEAD,
        BODY,
        CUSTOM
    }

    protected static class Builder {
        protected final String name;
        protected AABB aabb;
        protected Vec3 boxOffset;
        protected Vec3 vertexPos;
        protected int duration = 30;
        protected BiPredicate<Entity, Integer> activeDuration = (entity, integer) -> integer % 10 == 0;
        protected Predicate<LivingEntity> attackCondition = livingEntity -> true;
        protected BiConsumer<LivingEntity, LivingEntity> attackConsumer = (attacker, target) -> {};
        protected ResourceKey<DamageType> damageType;
        protected float damageAmount;
        protected MoverType moverType = MoverType.HEAD;
        protected boolean followsBody;

        protected Builder(String name) {
            this.name = name;
        }
    }
}
