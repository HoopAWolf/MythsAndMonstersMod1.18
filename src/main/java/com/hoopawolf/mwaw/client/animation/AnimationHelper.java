package com.hoopawolf.mwaw.client.animation;

import com.hoopawolf.mwaw.entities.helper.MathFuncHelper;
import net.minecraft.core.Rotations;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;

import java.util.HashMap;

public class AnimationHelper
{
    public HashMap<EntityDataAccessor<Rotations>, PercentageRotation> animation = new HashMap<>();

    public void defineSynchedData(EntityDataAccessor<Rotations> data)
    {
        animation.put(data, null);
    }

    public void registerRotationPoints(EntityDataAccessor<Rotations> data, PercentageRotation rotations)
    {
        if (animation.get(data) != null)
        {
            if (!animation.get(data).getEndRotation().equals(rotations.getEndRotation()))
            {
                animation.put(data, rotations);
            }
        } else
        {
            animation.put(data, rotations);
        }
    }

    public void animationTick(SynchedEntityData dataManager, float animationSpeed)
    {
        if (!animation.isEmpty())
        {
            for (EntityDataAccessor<Rotations> data : animation.keySet())
            {
                if (animation.get(data) != null)
                {
                    PercentageRotation stored_rotation = animation.get(data);

                    if (dataManager.get(data).equals(stored_rotation.getEndRotation()) || stored_rotation.getPercentage() >= 1)
                    {
                        animation.put(data, null);
                        stored_rotation = null;
                    }

                    if (stored_rotation != null)
                    {
                        dataManager.set(data, MathFuncHelper.lerp(stored_rotation.getStartingRotation(), stored_rotation.getEndRotation(), stored_rotation.getPercentage()));
                        stored_rotation.increasePercentage(animationSpeed);
                    }
                }
            }
        }
    }

    public boolean isAnimationDone(EntityDataAccessor<Rotations> data, SynchedEntityData dataManager)
    {
        return atDefaultRotation(dataManager.get(data).getX(), dataManager.get(data).getY(), dataManager.get(data).getZ());
    }

    public boolean atDefaultRotation(double x, double y, double z)
    {
        return x == 0 && y == 0 && z == 0;
    }

    public void resetRotation(SynchedEntityData dataManager, EntityDataAccessor<Rotations> data)
    {
        dataManager.set(data, new Rotations(0, 0, 0));
    }
}
