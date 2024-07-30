package com.ombremoon.sentinellib.common;

import com.ombremoon.sentinellib.networking.ModNetworking;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

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

    default LivingEntity getSentinel() {
        return (LivingEntity) this;
    }
}
