package com.ombremoon.sentinellib.common;

import com.ombremoon.sentinellib.api.box.BoxInstance;
import com.ombremoon.sentinellib.api.box.SentinelBox;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.loading.json.raw.Bone;
import software.bernie.geckolib.model.GeoModel;

import java.util.List;
import java.util.Map;

/**
 * This class manages the {@link BoxInstance BoxInstances} triggered by a {@link ISentinel Sentinel}.
 */
public class BoxInstanceManager {
    private final List<BoxInstance> instances = new ObjectArrayList<>();
    private final ISentinel sentinel;
    private final Map<GeoBone, Matrix4f> modelMatrices = new Object2ObjectOpenHashMap<>();

    public BoxInstanceManager(ISentinel sentinel) {
        this.sentinel = sentinel;
    }

    /**
     * Add a new box instance to the manager if it is not already running it.
     * @param sentinelBox The sentinel box to make an instance of.
     * @param entity The sentinel that triggered the box. Used to initialize box rotation
     * @return Whether the manager should add the instance.
     */
    public boolean addInstance(SentinelBox sentinelBox, Entity entity) {
        BoxInstance instance = new BoxInstance(sentinelBox, this.sentinel.getSentinel());
        for (BoxInstance boxInstance : this.instances) {
            if (boxInstance.equals(instance)) return false;
        }
        this.instances.add(instance);
        var rotation = sentinelBox.getProperRotation(entity);
        float f0 = rotation.getFirst();
        float f1 = rotation.getSecond();
        instance.setYRotation(f0, f1);
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

    public void addBoneMatrix(GeoBone bone, Matrix4f matrix4f) {
        this.modelMatrices.put(bone, matrix4f);
    }

    public Map<GeoBone, Matrix4f> getBoneMatrix() {
        return this.modelMatrices;
    }
}
