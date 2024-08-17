package com.ombremoon.sentinellib.util;

import com.ombremoon.sentinellib.Constants;
import com.ombremoon.sentinellib.api.box.BoxInstance;
import com.ombremoon.sentinellib.api.box.SentinelBox;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Utility class used primarily for OBB translations and rotations
 */
public class MatrixHelper {

    /**
     * Linear transformation of a 3D vector with a 4x4 matrix
     * @param matrix4f The transformation matrix
     * @param from The vector undergoing a transformation
     * @return The transformed matrix
     */
    public static Vec3 transform(Matrix4f matrix4f, Vec3 from) {
        double d0 = matrix4f.m00() * from.x + matrix4f.m10() * from.y + matrix4f.m20() * from.z + matrix4f.m30();
        double d1 = matrix4f.m01() * from.x + matrix4f.m11() * from.y + matrix4f.m21() * from.z + matrix4f.m31();
        double d2 = matrix4f.m02() * from.x + matrix4f.m12() * from.y + matrix4f.m22() * from.z + matrix4f.m32();
        return new Vec3(d0, d1, d2);
    }

    /**
     * Returns a 4x4 matrix representing the position in space of an entity
     * @param owner The entity providing the position
     * @param partialTicks The time between ticks
     * @return A 4x4 matrix translated and rotated to an entity's position
     */
    public static Matrix4f getEntityMatrix(LivingEntity owner, BoxInstance instance, float partialTicks) {
        Matrix4f matrix4f = new Matrix4f();
        Vec3 pos = owner.position();
        Matrix4f centerMatrix = new Matrix4f().translate((float) -pos.x, (float) pos.y, (float) -pos.z);
        matrix4f.mulLocal(centerMatrix.mul0(MatrixHelper.getEntityRotation(owner, instance, partialTicks)));
        return matrix4f;
    }

    public static Matrix4f getTranslatedEntityMatrix(LivingEntity owner, BoxInstance instance, float partialTicks) {
        SentinelBox box = instance.getSentinelBox();
        Vec3 pos = owner.position();
        Matrix4f matrix4f = new Matrix4f();
        Matrix4f centerMatrix = new Matrix4f().translate((float) -pos.x, (float) pos.y, (float) -pos.z);
        matrix4f.mulLocal(centerMatrix.mul0(getEntityRotation(owner, instance, partialTicks)));
        Vec3 path = box.getBoxPath(instance, partialTicks);
        matrix4f.translate((float) -path.x, (float) path.y, (float) -path.z);
        return matrix4f;
    }

    /**
     * Returns a 4x4 matrix representing the yaw of an entity
     * @param livingEntity The entity providing the look angle
     * @param partialTicks The time between ticks
     * @return An identity 4x4 matrix that has been rotated on the y-axis
     */
    private static Matrix4f getEntityRotation(LivingEntity livingEntity, BoxInstance instance, float partialTicks) {
        float yRot = livingEntity.level().isClientSide ? instance.getYRot() : -instance.getYRot();
        float yRot0 = livingEntity.level().isClientSide ? instance.getYRot0() : -instance.getYRot0();

        Matrix4f matrix4f = new Matrix4f();
        float yawAmount = Mth.clampedLerp(yRot0, yRot, partialTicks);
        return matrix4f.rotate(yawAmount * Mth.DEG_TO_RAD, new Vector3f(0.0F, 1.0F, 0.0F));
    }

    public static Matrix4f getRotatedEntityMatrix(LivingEntity livingEntity, BoxInstance instance, float partialTicks) {
        float yRot = livingEntity.level().isClientSide ? instance.dynamicYRot : -instance.dynamicYRot;
        float yRot0 = livingEntity.level().isClientSide ? instance.dynamicYRot0 : -instance.dynamicYRot0;

        Matrix4f matrix4f = new Matrix4f();
        float yRotation = Mth.clampedLerp(yRot0, yRot, partialTicks);
        matrix4f.rotate(yRotation * Mth.DEG_TO_RAD, new Vector3f(0.0F, 1.0F, 0.0F));
        return matrix4f;
    }

    /**
     * Returns a quaternion from a 4x4 matrix. Used for client rendering.
     * @param matrix The rotated 4x4 matrix
     * @return A quaternion used by the renderer
     */
    public static Quaternionf quaternion(Matrix4f matrix) {
        float trace = matrix.m00() + matrix.m11() + matrix.m22();
        float f;
        float w;
        float x;
        float y;
        float z;
        if (trace > 0) {
            f = (float) (Math.sqrt(trace + 1.0F) * 2.0F);
            w = 0.25F * f;
            x = (matrix.m21() - matrix.m12()) / f;
            y = (matrix.m02() - matrix.m20()) / f;
            z = (matrix.m10() - matrix.m01()) / f;
        } else if (matrix.m00() > matrix.m11() && matrix.m00() > matrix.m22()) {
            f = (float) (Math.sqrt(1.0F + matrix.m00() - matrix.m11() - matrix.m22()) * 2.0F);
            w = (matrix.m21() - matrix.m12()) / f;
            x = 0.25F * f;
            y = (matrix.m01() + matrix.m10()) / f;
            z = (matrix.m02() + matrix.m20()) / f;
        } else if (matrix.m11() > matrix.m22()) {
            f = (float) (Math.sqrt(1.0F + matrix.m11() - matrix.m00() - matrix.m22()) * 2.0F);
            w = (matrix.m02() - matrix.m20()) / f;
            x = (matrix.m01() + matrix.m10()) / f;
            y = 0.25F * f;
            z = (matrix.m12() + matrix.m21()) / f;
        } else {
            f = (float) (Math.sqrt(1.0F + matrix.m22() - matrix.m00() - matrix.m11()) * 2.0F);
            w = (matrix.m10() - matrix.m01()) / f;
            x = (matrix.m02() + matrix.m20()) / f;
            y = (matrix.m12() + matrix.m21()) / f;
            z = 0.25F * f;
        }
        Quaternionf quaternionf = new Quaternionf(x, y, z, w);
        return quaternionf.normalize();
    }

    public static Vec3 project(Vec3 startVec, Vec3 endVec) {
        double projection = (endVec.dot(startVec)) / (Mth.square(endVec.x) + Mth.square(endVec.y) + Mth.square(endVec.z));
        return endVec.scale(projection);
    }
}
