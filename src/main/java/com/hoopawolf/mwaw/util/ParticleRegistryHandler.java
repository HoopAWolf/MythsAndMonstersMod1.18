package com.hoopawolf.mwaw.util;

import com.hoopawolf.mwaw.client.particles.*;
import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ParticleRegistryHandler
{
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Reference.MOD_ID);

    //PARTICLES
    public static final RegistryObject<SimpleParticleType> YELLOW_ORBITING_ENCHANTMENT_PARTICLE = PARTICLES.register("yelloworbitingenchantparticle", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> GREEN_SUCKING_ENCHANTMENT_PARTICLE = PARTICLES.register("yellowsuckingenchantparticle", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> NATURE_AURA_SUCKING_PARTICLE = PARTICLES.register("natureaurasuckingparticle", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> GREEN_FLAME_PARTICLE = PARTICLES.register("greenflameparticle", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> NATURE_AURA_PARTICLE = PARTICLES.register("natureauraparticle", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> YELLOW_ENCHANTMENT_PARTICLE = PARTICLES.register("yellowenchantmentparticle", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> FIRE_PARTICLE = PARTICLES.register("fireparticle", () -> new SimpleParticleType(true));
    public static final RegistryObject<SimpleParticleType> FIRE_SUCKING_PARTICLE = PARTICLES.register("firesuckingparticle", () -> new SimpleParticleType(true));

    @SubscribeEvent
    public static void registerFactories(ParticleFactoryRegisterEvent event)
    {
        ParticleEngine particles = Minecraft.getInstance().particleEngine;

        particles.register(YELLOW_ORBITING_ENCHANTMENT_PARTICLE.get(), YellowEnchantmentOrbitingParticle.Factory::new);
        particles.register(GREEN_SUCKING_ENCHANTMENT_PARTICLE.get(), GreenEnchantmentSuckingParticle.Factory::new);
        particles.register(GREEN_FLAME_PARTICLE.get(), GreenFlameParticle.Factory::new);
        particles.register(NATURE_AURA_PARTICLE.get(), NatureAuraParticle.Factory::new);
        particles.register(YELLOW_ENCHANTMENT_PARTICLE.get(), YellowEnchantmentParticle.Factory::new);
        particles.register(NATURE_AURA_SUCKING_PARTICLE.get(), NatureAuraSuckingParticle.Factory::new);
        particles.register(FIRE_PARTICLE.get(), FireParticle.FireSmokeFactory::new);
        particles.register(FIRE_SUCKING_PARTICLE.get(), FireSuckingParticle.Factory::new);
    }
}
