package com.ombremoon.sentinellib.compat;

import com.ombremoon.sentinellib.api.box.OBBSentinelBox;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class GeoBoneOBBSentinelBox extends OBBSentinelBox {
    public GeoBoneOBBSentinelBox(Builder builder) {
        super(builder);
    }

    public static class Builder extends OBBSentinelBox.Builder {

        public Builder(String name) {
            super(name);
            this.moverType = MoverType.BONE;
        }

        /**
         * Creates a new builder.<br> The name should be equal to the name of the bone the box should track.
         * @param name The name of the sentinel box
         * @return The builder
         */
        public static Builder of(String name) { return new Builder(name); }

        public Builder sizeAndOffset(float xVertex) {
            sizeAndOffset(xVertex, xVertex);
            return this;
        }

        public Builder sizeAndOffset(float xVertex, float yVertex) {
            sizeAndOffset(xVertex, yVertex, xVertex);
            return this;
        }

        /**
         * Defines the size and offset of the sentinel box.
         * @param xVertex The x distance from the center of the box to the top right vertex
         * @param yVertex The y distance from the center of the box to the top right vertex
         * @param zVertex The z distance from the center of the box to the top right vertex
         * @return The builder
         */
        public Builder sizeAndOffset(float xVertex, float yVertex, float zVertex) {
            double xSize = Math.abs(xVertex);
            double ySize = Math.abs(yVertex);
            double zSize = Math.abs(zVertex);
            double maxLength = Math.max(xSize, Math.max(ySize, zSize));
            this.vertexPos = new Vec3(xVertex, yVertex, zVertex);
            this.boxOffset = new Vec3(0, 0, 0);
            this.aabb = new AABB(maxLength, maxLength, maxLength, -maxLength, -maxLength, -maxLength);
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

        public GeoBoneOBBSentinelBox build() { return new GeoBoneOBBSentinelBox(this); }
    }
}
