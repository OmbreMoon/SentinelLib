package com.ombremoon.sentinellib.example;

import com.ombremoon.sentinellib.CommonClass;
import com.ombremoon.sentinellib.api.BoxUtil;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class IceMistModel extends DefaultedEntityGeoModel<IceMist> {
    public IceMistModel() {
        super(CommonClass.customLocation("ice_mist"));
    }

    @Override
    public RenderType getRenderType(IceMist animatable, ResourceLocation texture) {
        return RenderType.entityTranslucentEmissive(getTextureResource(animatable));
    }
}
