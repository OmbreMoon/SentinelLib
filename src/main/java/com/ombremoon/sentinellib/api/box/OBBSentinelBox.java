package com.ombremoon.sentinellib.api.box;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ombremoon.sentinellib.api.Easing;
import com.ombremoon.sentinellib.common.ISentinel;
import com.ombremoon.sentinellib.util.MatrixHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import java.util.List;
import java.util.function.*;

/**
 * An OBB based sentinel box, used for more dynamic functionality.<br> Can be offset, translated, and rotated based on the {@link ISentinel Sentinels'} needs.
 */
public class OBBSentinelBox extends SentinelBox {
    public OBBSentinelBox(Builder builder) {
        super(builder);
    }

    @Override
    public AABB getSentinelBB(BoxInstance instance) {
        Vec3 center = instance.getCenter();
        return this.aabb.inflate(this.aabb.maxX - this.aabb.minX, this.aabb.maxY - this.aabb.minY, this.aabb.maxZ - this.aabb.minZ).move(-center.x, center.y, -center.z);
    }

    @Override
    public void renderBox(BoxInstance instance, Entity entity, PoseStack poseStack, VertexConsumer vertexConsumer, float partialTicks, float isRed) {
        MoverType moverType = instance.getSentinelBox().getMoverType();
        Matrix4f transpose = MatrixHelper.getMovementMatrix(entity, instance, partialTicks, moverType);
        poseStack.pushPose();
        if (moverType == MoverType.BONE) {
            float x = (float) (transpose.m30() - entity.position().x);
            float y = (float) (transpose.m31() - entity.position().y);
            float z = (float) (transpose.m32() - entity.position().z);
            poseStack.translate(x, y, z);
        }

        poseStack.mulPose(MatrixHelper.quaternion(transpose.invert()));
        Matrix4f matrix = poseStack.last().pose();
        if (moverType.isDefined()) {
            Vec3 vec3 = this.getBoxPath(instance, partialTicks);
            matrix.translate((float) vec3.x, (float) vec3.y, (float) vec3.z);
        }

        Matrix3f matrix3f = poseStack.last().normal();
        Vec3 scale = this.getScaleFactor(instance);
        Vec3 vertex = this.getVertexPos().multiply(scale.x, scale.y, scale.z);
        Vec3 offset = this.getBoxOffset();
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
    public List<Entity> getEntityCollisions(Entity owner, BoxInstance instance) {
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

    @Override
    public Vec3 getBoxPath(BoxInstance instance, float partialTicks) {
        float xMovement = this.getBoxMovement(SentinelBox.MovementAxis.X_TRANSLATION).apply(instance.tickCount, partialTicks);
        float yMovement = this.getBoxMovement(SentinelBox.MovementAxis.Y_TRANSLATION).apply(instance.tickCount, partialTicks);
        float zMovement = this.getBoxMovement(SentinelBox.MovementAxis.Z_TRANSLATION).apply(instance.tickCount, partialTicks);
        return new Vec3(xMovement, yMovement, zMovement);
    }

    @Override
    public Vec3 getScaleFactor(BoxInstance instance) {
        float xScale = this.getBoxScale(SentinelBox.MovementAxis.X_TRANSLATION).apply(instance.tickCount);
        float yScale = this.getBoxScale(SentinelBox.MovementAxis.Y_TRANSLATION).apply(instance.tickCount);
        float zScale = this.getBoxScale(SentinelBox.MovementAxis.Z_TRANSLATION).apply(instance.tickCount);
        xScale = this.getScaleDirection() == ScaleDirection.OUT ? xScale : (float) (1 - xScale / this.getVertexPos().x);
        yScale = this.getScaleDirection() == ScaleDirection.OUT ? yScale : (float) (1 - yScale / this.getVertexPos().y);
        zScale = this.getScaleDirection() == ScaleDirection.OUT ? zScale : (float) (1 - zScale / this.getVertexPos().z);
        return new Vec3(xScale, yScale, zScale);
    }

    /**
     * Builder pattern for OBB based sentinel boxes
     */
    public static class Builder extends SentinelBox.Builder {

        public Builder(String name) {
            super(name);
            this.boxMovement.put(0, (ticks, partialTicks) -> 0.0F);
            this.boxMovement.put(1, (ticks, partialTicks) -> 0.0F);
            this.boxMovement.put(2, (ticks, partialTicks) -> 0.0F);
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
         * Defines the size and offset of the sentinel box.
         * @param xVertex The x distance from the center of the box to the top right vertex
         * @param yVertex The y distance from the center of the box to the top right vertex
         * @param zVertex The z distance from the center of the box to the top right vertex
         * @param xOffset The x offset
         * @param yOffset The y offset
         * @param zOffset The z offset
         * @return The builder
         */
        public Builder sizeAndOffset(float xVertex, float yVertex, float zVertex, float xOffset, float yOffset, float zOffset) {
            double xSize = Math.abs(xVertex) + Math.abs(xOffset);
            double ySize = Math.abs(yVertex) + Math.abs(yOffset);
            double zSize = Math.abs(zVertex) + Math.abs(zOffset);
            double maxLength = Math.max(xSize, Math.max(ySize, zSize));
            this.vertexPos = new Vec3(xVertex, yVertex, zVertex);
            this.boxOffset = new Vec3(xOffset, yOffset, zOffset);
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

        public Builder moverType(MoverType moverType) {
            this.moverType = moverType;
            return this;
        }

        public Builder defineMovement(MovementAxis axis, BiFunction<Integer, Float, Float> boxMovement) {
            if (axis.ordinal() > 2) throw new IllegalArgumentException("Axis must be translational");
            this.boxMovement.put(axis.ordinal(), boxMovement);
            return this;
        }

        public Builder scaleOut(MovementAxis axis, Function<Integer, Float> boxScale) {
            if (axis.ordinal() > 2) throw new IllegalArgumentException("Axis must be translational");
            this.scaleDirection = ScaleDirection.OUT;
            this.boxScale.put(axis.ordinal(), boxScale);
            return this;
        }

        public Builder scaleIn(MovementAxis axis, Function<Integer, Float> boxScale) {
            if (axis.ordinal() > 2) throw new IllegalArgumentException("Axis must be translational");
            this.scaleDirection = ScaleDirection.IN;
            this.boxScale.put(axis.ordinal(), boxScale);
            return this;
        }

        public Builder scaleOut(Function<Integer, Float> boxScale) {
            for (int i = 0; i < 3; i++) {
                this.scaleDirection = ScaleDirection.OUT;
                this.boxScale.put(MovementAxis.values()[i].ordinal(), boxScale);
            }
            return this;
        }

        public Builder scaleIn(Function<Integer, Float> boxScale) {
            for (int i = 0; i < 3; i++) {
                this.scaleDirection = ScaleDirection.IN;
                this.boxScale.put(MovementAxis.values()[i].ordinal(), boxScale);
            }
            return this;
        }

        public Builder squareMovement(float length, int cycleSpeed) {
            int i1 = cycleSpeed * 40;
            int i2 = i1 / 4;
            this.boxMovement.put(0, (ticks, partialTicks) -> {
                if (ticks % i1 < i1 * 0.25F) {
                    return (length / i2) * (ticks % i2);
                } else if (ticks % i1 < i1 * 0.5F) {
                    return length;
                } else if (ticks % i1 < i1 * 0.75F) {
                    return length - (length / i2) * (ticks % i2);
                } else {
                    return 0.0F;
                }
            });
            this.boxMovement.put(2, (ticks, partialTicks) -> {
                if (ticks % i1 < i1 * 0.25F) {
                    return  0.0F;
                } else if (ticks % i1 < i1 * 0.5F) {
                    return (length / i2) * (ticks % i2);
                } else if (ticks % i1 < i1 * 0.75F) {
                    return length;
                } else {
                    return length - (length / i2) * (ticks % i2);
                }
            });
            return this;
        }

        public Builder circleMovement(float radius, float cycleSpeed) {
            this.boxMovement.put(0, (ticks, partialTicks) -> {
                return radius * (float) Math.sin(cycleSpeed * ticks);
            });
            this.boxMovement.put(2, (ticks, partialTicks) -> {
                return radius * (float) Math.cos(cycleSpeed * ticks);
            });
            return this;
        }

        /**
         * Builds the Sentinel Box
         * @return An OBBSentinelBox
         */
        public OBBSentinelBox build() {
            return new OBBSentinelBox(this);
        }
    }
}
