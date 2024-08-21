package com.ombremoon.sentinellib.example;

import com.ombremoon.sentinellib.common.ISentinelRenderer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import java.util.List;

public class IceMistRenderer extends GeoEntityRenderer<IceMist> implements ISentinelRenderer<IceMist> {
    public IceMistRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new IceMistModel());
    }

    @Override
    public IceMist getSentinel() {
        return this.animatable;
    }

    @Override
    public List<String> getSentinelBones() {
        return ObjectArrayList.of("mist_center", "mist1");
    }

}
