package com.ombremoon.tugkansem;

import com.ombremoon.tugkansem.common.init.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;

public class CommonClass {

    public static void init(IEventBus modEventBus) {
        ItemInit.register(modEventBus);
        BlockInit.register(modEventBus);
        EntityInit.register(modEventBus);
        ParticleInit.register(modEventBus);
        MemoryTypeInit.init();
    }

    public static ResourceLocation customLocation(String name) {
        return new ResourceLocation(Constants.MOD_ID, name);
    }
}
