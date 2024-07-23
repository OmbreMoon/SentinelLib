package com.ombremoon.tugkansem.common.sentinel;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.ombremoon.tugkansem.network.ModNetworking;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public interface ISentinel {

    BoxInstanceManager getBoxManager();

    List<Pair<String, SentinelBox>> registerBoxes();

    default void triggerSentinelBox(SentinelBox sentinelBox) {
        Entity entity = getSentinel();
        if (entity.level().isClientSide) {
            getBoxManager().addInstance(sentinelBox);
        } else {
            if (getBoxManager().addInstance(sentinelBox)) {
                ModNetworking.triggerSentinelBox(entity.getId(), getIDFromBox(sentinelBox));
            }
        }
    }

    default void removeSentinelInstance(SentinelBox sentinelBox) {
        Entity entity = getSentinel();
        if (entity.level().isClientSide) {
            getBoxManager().removeInstance(sentinelBox);
        } else {
            if (getBoxManager().removeInstance(sentinelBox)) {
                ModNetworking.removeSentinelBox(entity.getId(), getIDFromBox(sentinelBox));
            }
        }
    }

    default void tickBoxes() {
        getBoxManager().getInstances().forEach(BoxInstance::tick);
    }

    default SentinelBox getBoxFromID(String boxID) {
        for (var boxPair : registerBoxes()) {
            if (boxPair.getFirst().equalsIgnoreCase(boxID))
                return boxPair.getSecond();
        }
        return null;
    }

    default String getIDFromBox(SentinelBox sentinelBox) {
        for (var boxPair : registerBoxes()) {
            if (boxPair.getSecond() == sentinelBox)
                return boxPair.getFirst();
        }
        return "";
    }

    default Entity getSentinel() {
        return (Entity) this;
    }

    default void renderBox(BoxInstance instance, Entity entity, PoseStack poseStack, VertexConsumer vertexConsumer, float partialTicks) {
        poseStack.pushPose();
        float activeColor = instance.isActive() ? 0.0F : 1.0F;
        Vec3 center = instance.getCenter(partialTicks);
        Vec3 vec3 = instance.getProperOffset(entity);
        LevelRenderer.renderLineBox(poseStack, vertexConsumer, instance.getSentinelBB(partialTicks).move(-center.x + vec3.x, -center.y + vec3.y, -center.z + vec3.z), 1.0F, activeColor, activeColor, 1.0F);
        poseStack.popPose();
    }
}
