package com.ombremoon.sentinellib.common;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;
import java.util.Map;

public class BoxInstanceManager {
    private final Map<String, SentinelBox> boxCache = new Object2ObjectOpenHashMap<>();
    private final List<BoxInstance> instances = new ObjectArrayList<>();
    private final ISentinel sentinel;

    public BoxInstanceManager(ISentinel sentinel) {
        this.sentinel = sentinel;
    }

    public boolean addInstance(SentinelBox sentinelBox) {
        BoxInstance instance = new BoxInstance(sentinelBox, this.sentinel.getSentinel());
        for (BoxInstance boxInstance : this.instances) {
            if (boxInstance.equals(instance)) return false;
        }
        this.instances.add(instance);
        return true;
    }

    public void removeInstance(BoxInstance instance) {
        this.instances.remove(instance);
    }

    public boolean removeInstance(SentinelBox sentinelBox) {
        for (BoxInstance boxInstance : this.instances) {
            if (boxInstance.getSentinelBox() == sentinelBox) {
                this.instances.remove(boxInstance);
                return true;
            }
        }
        return false;
    }

    public List<BoxInstance> getInstances() {
        return this.instances;
    }
}
