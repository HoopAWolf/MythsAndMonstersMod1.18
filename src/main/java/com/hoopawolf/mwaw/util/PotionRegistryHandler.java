package com.hoopawolf.mwaw.util;

import com.hoopawolf.mwaw.potion.PotionEffectBase;
import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class PotionRegistryHandler
{
    public static final DeferredRegister<Potion> POTION = DeferredRegister.create(ForgeRegistries.POTIONS, Reference.MOD_ID);
    public static final DeferredRegister<MobEffect> POTION_EFFECT = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Reference.MOD_ID);

    //EFFECTS
    public static final RegistryObject<MobEffect> CLAY_SLOW_EFFECT = POTION_EFFECT.register("claysloweffect", () -> new PotionEffectBase(MobEffectCategory.HARMFUL, 0x9da4a6)
            .addAttributeModifier(Attributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", -0.10F, AttributeModifier.Operation.MULTIPLY_TOTAL));

    //POTION
    public static final RegistryObject<Potion> CLAY_SLOW_POTION = POTION.register("clayslowpotion", () -> new Potion(new MobEffectInstance(CLAY_SLOW_EFFECT.get(), 100)));

    public static void init(IEventBus _iEventBus)
    {
        POTION_EFFECT.register(_iEventBus);
        POTION.register(_iEventBus);
    }

}
