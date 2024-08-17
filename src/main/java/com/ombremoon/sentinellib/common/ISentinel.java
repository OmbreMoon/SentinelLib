package com.ombremoon.sentinellib.common;

import com.ombremoon.sentinellib.api.box.BoxInstance;
import com.ombremoon.sentinellib.api.box.SentinelBox;
import com.ombremoon.sentinellib.networking.ModNetworking;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.util.List;


//TODO: ADD MORE DOCUMENTATION

/**
 * This interface is used to define a sentinel. For an entity to use sentinel boxes, it <b><u>MUST</u></b> implement this interface.
 */
public interface ISentinel {

    /**
     * All sentinels must return an instance of a {@link BoxInstanceManager}, which manages instance-specific sentinel box info. The instance manager takes in an {@link ISentinel} and should be stored in the sentinel instance.
     * @return An instance of a BoxInstanceManager
     */
    BoxInstanceManager getBoxManager();

    /**
     * Register your {@link SentinelBox Sentinel Boxes}. Override this method in your sentinel and return an {@link it.unimi.dsi.fastutil.objects.ObjectArrayList ObjectArrayList} of the sentinel boxes it should use.<br> You may add as many boxes as wanted.
     * @return A list of sentinel boxes the sentinel should use.
     */
    List<SentinelBox> getSentinelBoxes();

    /**
     * Trigger a sentinel box for this entity. Adds a {@link BoxInstance} to the BoxInstanceManager.<br> Entities can only run one instance of each box registered at a time.
     * @param sentinelBox The sentinel box that should be triggered.
     */
    default void triggerSentinelBox(SentinelBox sentinelBox) {
        Entity entity = getSentinel();
        if (entity.level().isClientSide) {
            getBoxManager().addInstance(sentinelBox, entity);
        } else {
            if (getBoxManager().addInstance(sentinelBox, entity)) {
                ModNetworking.triggerSentinelBox(entity.getId(), sentinelBox.getName());
            }
        }
    }

    /**
     * A kill-switch to end a BoxInstance before its intended duration.
     * @param sentinelBox The sentinel box that should be removed from the BoxInstanceManager
     */
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

    /**
     * Sentinels <b><u>MUST</u></b> call this from the entity's {@link LivingEntity#aiStep()}, or {@link Mob#aiStep()} if extending Mob.<br>
     * Sentinel boxes are ticked on both the server and the client.<br>
     * This method does not need to be overridden.
     */
    default void tickBoxes() {
        getBoxManager().getInstances().forEach(BoxInstance::tick);
    }

    /**
     * Should <b><u>NOT</u></b> be overridden. Returns a sentinel box that has been registered to the sentinel from its ID
     * @param boxID The sentinel box ID
     * @return The registered sentinel box
     */
    default SentinelBox getBoxFromID(String boxID) {
        for (SentinelBox sentinelBox : getSentinelBoxes()) {
            if (sentinelBox.getName().equalsIgnoreCase(boxID))
                return sentinelBox;
        }
        return null;
    }

    /**
     * Returns a living instance of a sentinel
     * @return The living entity
     */
    default Entity getSentinel() {
        return (Entity) this;
    }
}
