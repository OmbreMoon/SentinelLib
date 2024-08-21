package com.ombremoon.sentinellib.example;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ombremoon.sentinellib.common.ISentinel;
import com.ombremoon.sentinellib.common.ISentinelRenderer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.List;

public class IceMistRenderer extends GeoEntityRenderer<IceMist> implements ISentinelRenderer<IceMist> {
    public IceMistRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new IceMistModel());
    }

    @Override
    public void render(IceMist entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public IceMist getSentinel() {
        return this.animatable;
    }

    @Override
    public List<String> getSentinelBones() {
        return ObjectArrayList.of("mist_center", "mist_rot1");
    }
}
