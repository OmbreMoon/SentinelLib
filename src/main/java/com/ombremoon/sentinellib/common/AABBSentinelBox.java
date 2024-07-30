package com.ombremoon.sentinellib.common;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class AABBSentinelBox extends SentinelBox {

    public AABBSentinelBox(Builder builder) {
        super(builder);
    }

    @Override
    public AABB getSentinelBB(BoxInstance instance) {
        Vec3 center = instance.getCenter();
        return this.aabb.move(-center.x, center.y, -center.z);
    }

    public Vec3 getBoxPosition(Entity entity, float partialTicks) {
        Vec3 pos = entity.getPosition(partialTicks);
        Vec3 vec3 = getProperOffset(entity);
        return new Vec3(pos.x + vec3.x, pos.y + vec3.y, pos.z + vec3.z);
    }

    public Vec3 getProperOffset(Entity entity) {
        Vec3 vec = this.getBoxOffset();
        float xLookAngle = (-entity.getYHeadRot() + 90) * Mth.DEG_TO_RAD;
        float zLookAngle = -entity.getYHeadRot() * Mth.DEG_TO_RAD;
        Vec3 f = new Vec3(vec.z * Math.sin(zLookAngle), 0.0, vec.z * Math.cos(zLookAngle));
        Vec3 f1 = new Vec3(vec.x * Math.sin(xLookAngle), 0.0, vec.x * Math.cos(xLookAngle));
        return new Vec3(f.x + f1.x, vec.y, f.z + f1.z);
    }

    public AABB getSentinelBB(Entity entity) {
        return getSentinelBB(entity, 0.1F);
    }

    public AABB getSentinelBB(Entity entity, float partialTicks) {
        return this.getAABB().move(this.getBoxPosition(entity, partialTicks));
    }

    public Vec3 getCenter(Entity entity, float partialTicks) {
        return this.getSentinelBB(entity, partialTicks).getCenter();
    }

    @Override
    public void renderBox(BoxInstance instance, LivingEntity entity, PoseStack poseStack, VertexConsumer vertexConsumer, float partialTicks, float isRed) {
        poseStack.pushPose();
        Vec3 center = this.getCenter(entity, partialTicks);
        Vec3 vec3 = this.getProperOffset(entity);
        LevelRenderer.renderLineBox(poseStack, vertexConsumer, this.getSentinelBB(entity, partialTicks).move(-center.x + vec3.x, -center.y + vec3.y, -center.z + vec3.z), 1.0F, isRed, isRed, 1.0F);
        poseStack.popPose();
    }

    @Override
    public List<Entity> getEntityCollisions(LivingEntity owner, BoxInstance instance) {
        return owner.level().getEntities(owner, this.getSentinelBB(owner), entity -> entity instanceof LivingEntity);

    }

    public static class Builder extends SentinelBox.Builder {
        public Builder(String name) {
            super(name);
        }

        public static Builder of(String name) { return new Builder(name); }

        public Builder sizeAndOffset(float xPos, float xOffset, float yOffset, float zOffset) {
            sizeAndOffset(xPos, xPos, xOffset, yOffset, zOffset);
            return this;
        }

        public Builder sizeAndOffset(float xPos, float yPos, float xOffset, float yOffset, float zOffset) {
            sizeAndOffset(xPos, yPos, xPos, xOffset, yOffset, zOffset);
            return this;
        }

        public Builder sizeAndOffset(float xPos, float yPos, float zPos, float xOffset, float yOffset, float zOffset) {
            double xSize = Math.abs(xPos);
            double ySize = Math.abs(yPos);
            double zSize = Math.abs(zPos);
            this.vertexPos = new Vec3(xSize, ySize, zSize);
            this.boxOffset = new Vec3(xOffset, yOffset, zOffset);
            this.aabb = new AABB(xSize, ySize, zSize, -xSize, -ySize, -zSize);
            return this;
        }

        public Builder boxDuration(int durationTicks) {
            this.duration = durationTicks;
            return this;
        }

        public Builder activeTicks(BiPredicate<Entity, Integer> activeDuration) {
            this.activeDuration = activeDuration;
            return this;
        }

        public Builder attackCondition(Predicate<LivingEntity> attackCondition) {
            this.attackCondition = attackCondition;
            return this;
        }

        public Builder attackConsumer(Consumer<LivingEntity> attackConsumer) {
            this.attackConsumer = attackConsumer;
            return this;
        }

        public Builder typeDamage(ResourceKey<DamageType> damageType, float damageAmount) {
            this.damageType = damageType;
            this.damageAmount = damageAmount;
            return this;
        }

        public AABBSentinelBox build() {
            return new AABBSentinelBox(this);
        }
    }
}
