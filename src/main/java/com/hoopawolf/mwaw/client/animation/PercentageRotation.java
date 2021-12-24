package com.hoopawolf.mwaw.client.animation;

import net.minecraft.core.Rotations;

public class PercentageRotation
{
    private final Rotations startRotation,
            endRotation;
    private float percentage;

    public PercentageRotation(Rotations startingRotationIn, Rotations endingRotationIn)
    {
        startRotation = startingRotationIn;
        endRotation = endingRotationIn;
        percentage = 0.0F;
    }

    public void increasePercentage(float incrementIn)
    {
        percentage += incrementIn;
    }

    public Rotations getStartingRotation()
    {
        return startRotation;
    }

    public Rotations getEndRotation()
    {
        return endRotation;
    }

    public float getPercentage()
    {
        return percentage;
    }
}
