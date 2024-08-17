package com.ombremoon.sentinellib.api.box;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ombremoon.sentinellib.common.ISentinel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * An AABB based sentinel box, similar to the vanilla bounding boxes.<br> Should be used for stationary boxes that are centered around the {@link ISentinel Sentinel}.<br> Offset in the X-/Z- directions at your own risk.
 */
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
        Vec3 vec3 = getAABBOffset(entity);
        return new Vec3(pos.x + vec3.x, pos.y + vec3.y, pos.z + vec3.z);
    }

    public Vec3 getAABBOffset(Entity entity) {
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
    public void renderBox(BoxInstance instance, Entity entity, PoseStack poseStack, VertexConsumer vertexConsumer, float partialTicks, float isRed) {
        poseStack.pushPose();
        Vec3 center = this.getCenter(entity, partialTicks);
        Vec3 vec3 = this.getAABBOffset(entity);
        LevelRenderer.renderLineBox(poseStack, vertexConsumer, this.getSentinelBB(entity, partialTicks).move(-center.x + vec3.x, -center.y + vec3.y, -center.z + vec3.z), 1.0F, isRed, isRed, 1.0F);
        poseStack.popPose();
    }

    @Override
    public List<Entity> getEntityCollisions(Entity owner, BoxInstance instance) {
        return owner.level().getEntities(owner, this.getSentinelBB(owner), entity -> entity instanceof LivingEntity);

    }

    /**
     * Builder pattern for AABB based sentinel boxes
     */
    public static class Builder extends SentinelBox.Builder {
        public Builder(String name) {
            super(name);
        }

        /**
         * Creates a new builder
         * @param name The name of the sentinel box
         * @return The builder
         */
        public static Builder of(String name) { return new Builder(name); }

        public Builder sizeAndOffset(float xSize, float xOffset, float yOffset, float zOffset) {
            sizeAndOffset(xSize, xSize, xOffset, yOffset, zOffset);
            return this;
        }

        public Builder sizeAndOffset(float xSize, float ySize, float xOffset, float yOffset, float zOffset) {
            sizeAndOffset(xSize, ySize, xSize, xOffset, yOffset, zOffset);
            return this;
        }

        /**
         * Defines the size and offset of the sentinel box.<br> It is highly recommended to <b><u>NOT</u></b> offset the x-/z- directions for AABB sentinel boxes.
         * @param xSize Half of the x size
         * @param ySize Half of the y size
         * @param zSize Half of the z size
         * @param xOffset The x offset
         * @param yOffset The y offset
         * @param zOffset The z offset
         * @return The builder
         */
        public Builder sizeAndOffset(float xSize, float ySize, float zSize, float xOffset, float yOffset, float zOffset) {
            double xVertex = Math.abs(xSize);
            double yVertex = Math.abs(ySize);
            double zVertex = Math.abs(zSize);
            this.vertexPos = new Vec3(xVertex, yVertex, zVertex);
            this.boxOffset = new Vec3(xOffset, yOffset, zOffset);
            this.aabb = new AABB(xVertex, yVertex, zVertex, -xVertex, -yVertex, -zVertex);
            return this;
        }

        /**
         * Defines the duration the box should tick.
         * @param durationTicks
         * @return The builder
         */
        public Builder boxDuration(int durationTicks) {
            this.duration = durationTicks;
            return this;
        }

        /**
         * Sets the box to have an infinite duration.<br> Must define a predicate for when the box should stop.
         * @param stopPredicate
         * @return The builder
         */
        public Builder noDuration(Predicate<Entity> stopPredicate) {
            this.hasDuration = false;
            this.stopPredicate = stopPredicate;
            return this;
        }

        /**
         * Callback to define when/if a box should stop before its intended duration.
         * @param stopPredicate
         * @return The builder
         */
        public Builder stopIf(Predicate<Entity> stopPredicate) {
            this.stopPredicate = stopPredicate;
            return this;
        }

        /**
         * Callback to define when the box should be active.
         * @param activeDuration
         * @return The builder
         */
        public Builder activeTicks(BiPredicate<Entity, Integer> activeDuration) {
            this.activeDuration = activeDuration;
            return this;
        }

        /**
         * Callback to determine what entities should be affected by the box
         * @param attackCondition
         * @return The builder
         */
        public Builder attackCondition(Predicate<Entity> attackCondition) {
            this.attackCondition = attackCondition;
            return this;
        }

        /**
         * Callback to add extra functionality to the sentinel box when triggered
         * @param startConsumer
         * @return
         */
        public Builder onBoxTrigger(Consumer<Entity> startConsumer) {
            this.boxStart = startConsumer;
            return this;
        }

        /**
         * Callback to add extra functionality to the sentinel box every tick
         * @param tickConsumer
         * @return
         */
        public Builder onBoxTick(Consumer<Entity> tickConsumer) {
            this.boxTick = tickConsumer;
            return this;
        }

        /**
         * Callback to add extra functionality to the sentinel box at the end of its duration
         * @param stopConsumer
         * @return
         */
        public Builder onBoxStop(Consumer<Entity> stopConsumer) {
            this.boxStop = stopConsumer;
            return this;
        }

        /**
         * Callback to add extra functionality to the sentinel box while active
         * @param activeConsumer
         * @return
         */
        public Builder onActiveTick(Consumer<Entity> activeConsumer) {
            this.boxActive = activeConsumer;
            return this;
        }

        /**
         * Callback to add extra functionality to the sentinel box when colliding with an entity
         * @param attackConsumer
         * @return
         */
        public Builder onHurtTick(BiConsumer<Entity, LivingEntity> attackConsumer) {
            this.boxHurt = attackConsumer;
            return this;
        }

        /**
         * Defines the damage type and amount the box causes while active
         * @param damageType
         * @param damageAmount
         * @return The builder
         */
        public Builder typeDamage(ResourceKey<DamageType> damageType, float damageAmount) {
            this.damageType = damageType;
            this.damageAmount = damageAmount;
            return this;
        }

        /**
         * Builds the Sentinel Box
         * @return An AABBSentinelBox
         */
        public AABBSentinelBox build() {
            return new AABBSentinelBox(this);
        }
    }
}
