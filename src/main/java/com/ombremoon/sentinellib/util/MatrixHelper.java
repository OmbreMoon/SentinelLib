package com.ombremoon.sentinellib.util;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class MatrixHelper {

    public static Vec3 transform(Matrix4f matrix4f, Vec3 from) {
        double d0 = matrix4f.m00() * from.x + matrix4f.m10() * from.y + matrix4f.m20() * from.z + matrix4f.m30();
        double d1 = matrix4f.m01() * from.x + matrix4f.m11() * from.y + matrix4f.m21() * from.z + matrix4f.m31();
        double d2 = matrix4f.m02() * from.x + matrix4f.m12() * from.y + matrix4f.m22() * from.z + matrix4f.m32();
        return new Vec3(d0, d1, d2);
    }

    public static Matrix4f getEntityMatrix(LivingEntity owner, float partialTicks) {
        Matrix4f matrix4f = new Matrix4f();
        Vec3 pos = owner.position();
        Matrix4f centerMatrix = new Matrix4f().translate((float) -pos.x, (float) pos.y, (float) -pos.z);
        matrix4f.mulLocal(centerMatrix.mul0(MatrixHelper.getEntityRotation(owner, partialTicks)));
        return matrix4f;
    }

    private static Matrix4f getEntityRotation(LivingEntity livingEntity, float partialTicks) {
        float yRot = livingEntity.level().isClientSide ? livingEntity.yHeadRot : -livingEntity.getYHeadRot();
        float yRot0 = livingEntity.level().isClientSide ? livingEntity.yHeadRotO : -livingEntity.getYHeadRot();

        Matrix4f matrix4f = new Matrix4f();
        float yawAmount = Mth.clampedLerp(yRot0, yRot, partialTicks);
        return matrix4f.rotate(yawAmount * Mth.DEG_TO_RAD, new Vector3f(0.0F, 1.0F, 0.0F));
    }

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
