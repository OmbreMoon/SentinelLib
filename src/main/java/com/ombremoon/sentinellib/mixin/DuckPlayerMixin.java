package com.ombremoon.sentinellib.mixin;

import com.ombremoon.sentinellib.common.BoxInstanceManager;
import com.ombremoon.sentinellib.common.IPlayerSentinel;
import com.ombremoon.sentinellib.common.event.RegisterPlayerSentinelBoxEvent;
import com.ombremoon.sentinellib.common.SentinelBox;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Player.class)
public class DuckPlayerMixin implements IPlayerSentinel {
    private final BoxInstanceManager manager = new BoxInstanceManager(this);
    private final List<SentinelBox> boxList = new ObjectArrayList<>();

    @Inject(method = "aiStep", at = @At("TAIL"))
    private void aiStep(CallbackInfo info) {
        tickBoxes();
    }

    @Override
    public BoxInstanceManager getBoxManager() {
        return this.manager;
    }

    @Override
    public List<SentinelBox> getSentinelBoxes() {
        return RegisterPlayerSentinelBoxEvent.registerSentinelBox((Player) getSentinel(), this.boxList);
    }

    @Override
    public void registerBox(SentinelBox sentinelBox) {
        if (!boxList.contains(sentinelBox))
            boxList.add(sentinelBox);
    }
}
