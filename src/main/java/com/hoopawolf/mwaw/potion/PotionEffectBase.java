package com.hoopawolf.mwaw.potion;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class PotionEffectBase extends MobEffect
{
    public PotionEffectBase(MobEffectCategory typeIn, int liquidColorIn)
    {
        super(typeIn, liquidColorIn);
    }
}
