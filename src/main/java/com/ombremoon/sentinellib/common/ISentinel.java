package com.ombremoon.sentinellib.common;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ombremoon.sentinellib.networking.ModNetworking;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public interface ISentinel {

    BoxInstanceManager getBoxManager();

    List<SentinelBox> getSentinelBoxes();

    default void triggerSentinelBox(SentinelBox sentinelBox) {
        Entity entity = getSentinel();
        if (entity.level().isClientSide) {
            getBoxManager().addInstance(sentinelBox);
        } else {
            if (getBoxManager().addInstance(sentinelBox)) {
                ModNetworking.triggerSentinelBox(entity.getId(), sentinelBox.getName());
            }
        }
    }

    default void removeSentinelInstance(SentinelBox sentinelBox) {
        Entity entity = getSentinel();
        if (entity.level().isClientSide) {
            getBoxManager().removeInstance(sentinelBox);
        } else {
            if (getBoxManager().removeInstance(sentinelBox)) {
                ModNetworking.removeSentinelBox(entity.getId(), sentinelBox.getName());
            }
        }
    }

    default void tickBoxes() {
        getBoxManager().getInstances().forEach(BoxInstance::tick);
    }

    default SentinelBox getBoxFromID(String boxID) {
        for (SentinelBox sentinelBox : getSentinelBoxes()) {
            if (sentinelBox.getName().equalsIgnoreCase(boxID))
                return sentinelBox;
        }
        return null;
    }

/*    default String getIDFromBox(SentinelBox sentinelBox) {
        for (SentinelBox box : getSentinelBoxes()) {
            if (box== sentinelBox)
                return box.getName();
        }
        return "";
    }*/

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
