package com.ombremoon.sentinellib.mixin;

import com.ombremoon.sentinellib.api.box.SentinelBox;
import com.ombremoon.sentinellib.common.BoxInstanceManager;
import com.ombremoon.sentinellib.common.IPlayerSentinel;
import com.ombremoon.sentinellib.common.event.RegisterPlayerSentinelBoxEvent;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(value = LivingEntity.class, remap = false)
public class DuckPlayerMixin implements IPlayerSentinel {
    private final BoxInstanceManager manager = new BoxInstanceManager(this);
    private final List<SentinelBox> boxList = new ObjectArrayList<>();

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
