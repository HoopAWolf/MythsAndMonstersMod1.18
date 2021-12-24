package com.hoopawolf.mwaw.entities.helper;

import net.minecraft.core.Rotations;
import net.minecraft.world.phys.Vec3;

public class MathFuncHelper
{
    public static Vec3 lerp(Vec3 start, Vec3 end, float percent)
    {
        return (start.add(end.subtract(start).multiply(percent, percent, percent)));
    }

    public static Rotations lerp(Rotations start, Rotations end, float percent)
    {
        Vec3 startingVec = new Vec3(start.getX(), start.getY(), start.getZ());
        Vec3 endingVec = new Vec3(end.getX(), end.getY(), end.getZ());

        Vec3 result = lerp(startingVec, endingVec, percent);

        return new Rotations((float) result.x(), (float) result.y(), (float) result.z());
    }

    public static Vec3 crossProduct(Vec3 vec_A, Vec3 vec_B)
    {
        return new Vec3(Math.signum((int) (vec_A.y() * vec_B.z() - vec_A.z() * vec_B.y())),
                Math.signum((int) (vec_A.z() * vec_B.x() - vec_A.x() * vec_B.z())),
                Math.signum((int) (vec_A.x() * vec_B.y() - vec_A.y() * vec_B.x())));
    }

    public static double sign(double d)
    {
        if (d > 0F && d <= 0.5F)
            return 0;
        else if (d > 0.5F)
            return 1;
        else if (d > -0.5F && d < 0F)
            return 0;
        else if (d < -0.5F)
            return -1;

        return 0;
    }
}
