package com.ombremoon.sentinellib.common;

import net.minecraft.world.entity.Entity;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

import java.util.List;
import java.util.Optional;

public interface ISentinelRenderer<T extends Entity & GeoAnimatable & ISentinel> {

    T getSentinel();

    List<String> getSentinelBones();

    default void trackSentinelModel(BakedGeoModel model) {
        for (String name : getSentinelBones()) {
            Optional<GeoBone> optional = model.getBone(name);
            if (optional.isPresent()) {
                GeoBone bone = optional.get();
                var spaceMatrix = bone.getWorldSpaceMatrix();
                float m00 = spaceMatrix.m00();
                float m01 = spaceMatrix.m01();
                float m02 = spaceMatrix.m02();
                float m03 = spaceMatrix.m03();
                float m10 = spaceMatrix.m10();
                float m11 = spaceMatrix.m11();
                float m12 = spaceMatrix.m12();
                float m13 = spaceMatrix.m13();
                float m20 = spaceMatrix.m20();
                float m21 = spaceMatrix.m21();
                float m22 = spaceMatrix.m22();
                float m23 = spaceMatrix.m23();
                float m30 = spaceMatrix.m30();
                float m31 = spaceMatrix.m31();
                float m32 = spaceMatrix.m32();
                float m33 = spaceMatrix.m33();
                Matrix4f matrix = new Matrix4f(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33);
                getSentinel().getBoxManager().addBoneMatrix(bone, matrix);
            }
        }
    }

    default void trackSentinelModel(GeoModel<T> model) {
        for (String name : getSentinelBones()) {
            Optional<GeoBone> optional = model.getBone(name);
            if (optional.isPresent()) {
                GeoBone bone = optional.get();
                var spaceMatrix = bone.getWorldSpaceMatrix();
                float m00 = spaceMatrix.m00();
                float m01 = spaceMatrix.m01();
                float m02 = spaceMatrix.m02();
                float m03 = spaceMatrix.m03();
                float m10 = spaceMatrix.m10();
                float m11 = spaceMatrix.m11();
                float m12 = spaceMatrix.m12();
                float m13 = spaceMatrix.m13();
                float m20 = spaceMatrix.m20();
                float m21 = spaceMatrix.m21();
                float m22 = spaceMatrix.m22();
                float m23 = spaceMatrix.m23();
                float m30 = spaceMatrix.m30();
                float m31 = spaceMatrix.m31();
                float m32 = spaceMatrix.m32();
                float m33 = spaceMatrix.m33();
                Matrix4f matrix = new Matrix4f(m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33);
                getSentinel().getBoxManager().addBoneMatrix(bone, matrix);
            }
        }
    }
}
