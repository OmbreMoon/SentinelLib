package com.ombremoon.sentinellib.common;

import com.ombremoon.sentinellib.api.box.BoxInstance;
import com.ombremoon.sentinellib.api.box.SentinelBox;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

/**
 * This class manages the {@link BoxInstance BoxInstances} triggered by a {@link ISentinel Sentinel}.
 */
public class BoxInstanceManager {
    private final List<BoxInstance> instances = new ObjectArrayList<>();
    private final ISentinel sentinel;

    public BoxInstanceManager(ISentinel sentinel) {
        this.sentinel = sentinel;
    }

    /**
     * Add a new box instance to the manager if it is not already running it.
     * @param sentinelBox The sentinel box to make an instance of.
     * @param entity The sentinel that triggered the box. Used to initialize box rotation
     * @return Whether the manager should add the instance.
     */
    public boolean addInstance(SentinelBox sentinelBox, LivingEntity entity) {
        BoxInstance instance = new BoxInstance(sentinelBox, this.sentinel.getSentinel());
        for (BoxInstance boxInstance : this.instances) {
            if (boxInstance.equals(instance)) return false;
        }
        this.instances.add(instance);
        var rotation = sentinelBox.getProperRotation(entity);
        float f0 = rotation.getFirst();
        float f1 = rotation.getSecond();
        instance.setBoxRotation(f0, f1);
        sentinelBox.onBoxTrigger().accept(entity);
        return true;
    }

    /**
     * Removes a box instance from the manager.
     * @param instance The instance to be removed.
     */
    public void removeInstance(BoxInstance instance) {
        this.instances.remove(instance);
    }

    /**
     * Removes a box instance from the manager dependent on the {@link SentinelBox} it is running.
     * @param sentinelBox The sentinel box held in the box instance.
     * @return Whether the manager contains and should remove the instance.
     */
    public boolean removeInstance(SentinelBox sentinelBox) {
        for (BoxInstance boxInstance : this.instances) {
            if (boxInstance.getSentinelBox() == sentinelBox) {
                this.instances.remove(boxInstance);
                return true;
            }
        }
        return false;
    }

    public BoxInstance getBoxInstance(String boxID) {
        for (BoxInstance instance : this.instances) {
            if (instance.getSentinelBox().getName().equalsIgnoreCase(boxID)) {
                return instance;
            }
        }
        return null;
    }

    /**
     * Returns a list of instances being run by the manager.
     * @return The list of box instances.
     */
    public List<BoxInstance> getInstances() {
        return this.instances;
    }
}
