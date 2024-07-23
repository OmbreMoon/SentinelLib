package com.ombremoon.tugkansem.common.sentinel;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class SentinelBox {
    private final AABB aabb;
    private final Vec3 boxOffset;
    private final Function<Entity, Integer> duration;
    private final BiPredicate<Entity, Integer> activeDuration;
    private final Predicate<LivingEntity> attackCondition;
    private final Consumer<LivingEntity> attackConsumer;
    private final ResourceKey<DamageType> damageType;
    private final float damageAmount;

    public SentinelBox(AABB aabb, Vec3 boxOffset, Function<Entity, Integer> duration, BiPredicate<Entity, Integer> activeDuration, Predicate<LivingEntity> attackCondition, Consumer<LivingEntity> attackConsumer, ResourceKey<DamageType> damageType, float damageAmount) {
        this.aabb = aabb;
        this.boxOffset = boxOffset;
        this.duration = duration;
        this.activeDuration = activeDuration;
        this.attackCondition = attackCondition;
        this.attackConsumer = attackConsumer;
        this.damageType = damageType;
        this.damageAmount = damageAmount;
    }

    public AABB getSentinelBox() {
        return this.aabb;
    }

    public Vec3 getBoxOffset() {
        return this.boxOffset;
    }

    public Function<Entity, Integer> getDuration() {
        return this.duration;
    }

    public BiPredicate<Entity, Integer> getActiveDuration() {
        return this.activeDuration;
    }

    public Predicate<LivingEntity> getAttackCondition() {
        return this.attackCondition;
    }

    public Consumer<LivingEntity> getAttackConsumer() {
        return this.attackConsumer;
    }

    public ResourceKey<DamageType> getDamageType() {
        return this.damageType;
    }

    public float getDamageAmount() {
        return this.damageAmount;
    }

    public static class Builder {
        private AABB aabb;
        private Vec3 boxOffset;
        private Function<Entity, Integer> duration = entity -> 30;
        private BiPredicate<Entity, Integer> activeDuration = (entity, integer) -> true;
        private Predicate<LivingEntity> attackCondition = livingEntity -> true;
        private Consumer<LivingEntity> attackConsumer = livingEntity -> {};
        private ResourceKey<DamageType> damageType;
        private float damageAmount;

        public Builder() {

        }

        public static Builder of() { return new Builder(); }

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
            this.boxOffset = new Vec3(xOffset, yOffset, zOffset);
            this.aabb = new AABB(xSize, ySize, zSize, -xSize, -ySize, -zSize);
            return this;
        }

        public Builder boxDuration(Function<Entity, Integer> durationTicks) {
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

        public SentinelBox build() {
            return new SentinelBox(this.aabb, this.boxOffset, this.duration, this.activeDuration, this.attackCondition, this.attackConsumer, this.damageType, this.damageAmount);
        }
    }
}
