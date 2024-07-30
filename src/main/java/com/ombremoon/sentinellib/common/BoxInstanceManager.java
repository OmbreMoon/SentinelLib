package com.ombremoon.sentinellib.common;

import com.ombremoon.sentinellib.api.box.BoxInstance;
import com.ombremoon.sentinellib.api.box.SentinelBox;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

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
     * @return Whether the manager should add the instance.
     */
    public boolean addInstance(SentinelBox sentinelBox) {
        BoxInstance instance = new BoxInstance(sentinelBox, this.sentinel.getSentinel());
        for (BoxInstance boxInstance : this.instances) {
            if (boxInstance.equals(instance)) return false;
        }
        this.instances.add(instance);
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

    /**
     * Returns a list of instances being run by the manager.
     * @return The list of box instances.
     */
    public List<BoxInstance> getInstances() {
        return this.instances;
    }
}
