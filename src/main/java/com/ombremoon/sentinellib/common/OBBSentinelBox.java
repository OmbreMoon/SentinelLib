package com.ombremoon.sentinellib.common;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ombremoon.sentinellib.Constants;
import com.ombremoon.sentinellib.util.MatrixHelper;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class OBBSentinelBox extends SentinelBox {
    public OBBSentinelBox(Builder builder) {
        super(builder);
    }

    @Override
    public AABB getSentinelBB(BoxInstance instance) {
        Vec3 center = instance.getCenter();
        AABB aabb1 = this.aabb.inflate(this.aabb.maxX - this.aabb.minX, this.aabb.maxY - this.aabb.minY, this.aabb.maxZ - this.aabb.minZ).move(-center.x, center.y, -center.z);
        return aabb1;
    }

    @Override
    public void renderBox(BoxInstance instance, LivingEntity entity, PoseStack poseStack, VertexConsumer vertexConsumer, float partialTicks, float isRed) {
        Matrix4f transpose = MatrixHelper.getEntityMatrix(entity, partialTicks);
        poseStack.pushPose();
        poseStack.mulPose(MatrixHelper.quaternion(transpose));
        Matrix4f matrix = poseStack.last().pose();
        Matrix3f matrix3f = poseStack.last().normal();
        Vec3 vertex = instance.getSentinelBox().getVertexPos();
        Vec3 offset = instance.getSentinelBox().getBoxOffset();
        float maxX = (float)(offset.x + vertex.x);
        float maxY = (float)(offset.y + vertex.y);
        float maxZ = (float)(offset.z + vertex.z);
        float minX = (float)(offset.x - vertex.x);
        float minY = (float)(offset.y - vertex.y);
        float minZ = (float)(offset.z - vertex.z);
        vertexConsumer.vertex(matrix, minX, minY, minZ).color(1.0F, isRed, isRed, 1.0F).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix, maxX, minY, minZ).color(1.0F, isRed, isRed, 1.0F).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix, minX, minY, minZ).color(1.0F, isRed, isRed, 1.0F).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix, minX, maxY, minZ).color(1.0F, isRed, isRed, 1.0F).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix, minX, minY, minZ).color(1.0F, isRed, isRed, 1.0F).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
        vertexConsumer.vertex(matrix, minX, minY, maxZ).color(1.0F, isRed, isRed, 1.0F).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
        vertexConsumer.vertex(matrix, maxX, minY, minZ).color(1.0F, isRed, isRed, 1.0F).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix, maxX, maxY, minZ).color(1.0F, isRed, isRed, 1.0F).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix, maxX, maxY, minZ).color(1.0F, isRed, isRed, 1.0F).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix, minX, maxY, minZ).color(1.0F, isRed, isRed, 1.0F).normal(matrix3f, -1.0F, 0.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix, minX, maxY, minZ).color(1.0F, isRed, isRed, 1.0F).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
        vertexConsumer.vertex(matrix, minX, maxY, maxZ).color(1.0F, isRed, isRed, 1.0F).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
        vertexConsumer.vertex(matrix, minX, maxY, maxZ).color(1.0F, isRed, isRed, 1.0F).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix, minX, minY, maxZ).color(1.0F, isRed, isRed, 1.0F).normal(matrix3f, 0.0F, -1.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix, minX, minY, maxZ).color(1.0F, isRed, isRed, 1.0F).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix, maxX, minY, maxZ).color(1.0F, isRed, isRed, 1.0F).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix, maxX, minY, maxZ).color(1.0F, isRed, isRed, 1.0F).normal(matrix3f, 0.0F, 0.0F, -1.0F).endVertex();
        vertexConsumer.vertex(matrix, maxX, minY, minZ).color(1.0F, isRed, isRed, 1.0F).normal(matrix3f, 0.0F, 0.0F, -1.0F).endVertex();
        vertexConsumer.vertex(matrix, minX, maxY, maxZ).color(1.0F, isRed, isRed, 1.0F).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix, maxX, maxY, maxZ).color(1.0F, isRed, isRed, 1.0F).normal(matrix3f, 1.0F, 0.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix, maxX, minY, maxZ).color(1.0F, isRed, isRed, 1.0F).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix, maxX, maxY, maxZ).color(1.0F, isRed, isRed, 1.0F).normal(matrix3f, 0.0F, 1.0F, 0.0F).endVertex();
        vertexConsumer.vertex(matrix, maxX, maxY, minZ).color(1.0F, isRed, isRed, 1.0F).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
        vertexConsumer.vertex(matrix, maxX, maxY, maxZ).color(1.0F, isRed, isRed, 1.0F).normal(matrix3f, 0.0F, 0.0F, 1.0F).endVertex();
        poseStack.popPose();
    }

    @Override
    public List<Entity> getEntityCollisions(LivingEntity owner, BoxInstance instance) {
        return owner.level().getEntities(owner, this.getSentinelBB(instance), entity -> {
            if (!(entity instanceof LivingEntity livingEntity))
                return false;

            return performSATTest(livingEntity, instance);
        });
    }

    private boolean performSATTest(LivingEntity target, BoxInstance instance) {
        BoxInstance targetBox = new BoxInstance(target);
        Vec3 direction = instance.getCenter().subtract(targetBox.getCenter());

        for (Vec3 normal : instance.instanceNormals) {
            if (!performSATTest(instance, targetBox, normal, direction))
                return false;
        }

        for (Vec3 normal : targetBox.instanceNormals) {
            if (!performSATTest(instance, targetBox, normal, direction))
                return false;
        }
        return true;
    }

    private boolean performSATTest(BoxInstance ownerBox, BoxInstance targetBox, Vec3 normal, Vec3 direction) {
        Vec3 maxProj1 = null, maxProj2 = null;
        double maxDot1 = -1, maxDot2 = -1;
        Vec3 distance = normal.dot(direction) > 0.0F ? direction : direction.scale(-1.0D);

        for (Vec3 vertexVector : ownerBox.instanceVertices) {
            Vec3 temp = normal.dot(vertexVector) > 0.0F ? vertexVector : vertexVector.scale(-1.0D);
            double dot = normal.dot(temp);

            if (dot > maxDot1) {
                maxDot1 = dot;
                maxProj1 = temp;
            }
        }

        for (Vec3 vertexVector : targetBox.instanceVertices) {
            Vec3 temp = normal.dot(vertexVector) > 0.0F ? vertexVector : vertexVector.scale(-1.0D);
            double dot = normal.dot(temp);

            if (dot > maxDot2) {
                maxDot2 = dot;
                maxProj2 = temp;
            }
        }

        return !(MatrixHelper.project(distance, normal).length() > MatrixHelper.project(maxProj1, normal).length() + MatrixHelper.project(maxProj2, normal).length());
    }

    public static class Builder extends SentinelBox.Builder {

        public Builder(String name) {
            super(name);
        }

        public static Builder of(String name) { return new Builder(name); }

        public Builder sizeAndOffset(float xSize, float xOffset, float yOffset, float zOffset) {
            sizeAndOffset(xSize, xSize, xOffset, yOffset, zOffset);
            return this;
        }

        public Builder sizeAndOffset(float xSize, float ySize, float xOffset, float yOffset, float zOffset) {
            sizeAndOffset(xSize, ySize, xSize, xOffset, yOffset, zOffset);
            return this;
        }

        public Builder sizeAndOffset(float xSize, float ySize, float zSize, float xOffset, float yOffset, float zOffset) {
            double xVertex = Math.abs(xSize);
            double yVertex = Math.abs(ySize);
            double zVertex = Math.abs(zSize);
            this.vertexPos = new Vec3(xSize, ySize, zSize);
            this.boxOffset = new Vec3(xOffset, yOffset, zOffset);
            this.aabb = new AABB(xVertex, yVertex, zVertex, -xVertex, -yVertex, -zVertex);
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

        public OBBSentinelBox build() {
            return new OBBSentinelBox(this);
        }
    }
}
