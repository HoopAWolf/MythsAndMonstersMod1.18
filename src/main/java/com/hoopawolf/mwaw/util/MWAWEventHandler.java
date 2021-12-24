package com.hoopawolf.mwaw.util;

import com.hoopawolf.mwaw.entities.HunterEntity;
import com.hoopawolf.mwaw.entities.KitsuneEntity;
import com.hoopawolf.mwaw.entities.ai.AnimalMeleeAttackGoal;
import com.hoopawolf.mwaw.entities.helper.EntityHelper;
import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class MWAWEventHandler
{
    @SubscribeEvent
    public static void playerEntityInteractEvent(PlayerInteractEvent.EntityInteract event)
    {
        Player player = event.getPlayer();
        Entity entity = event.getTarget();

        if (entity instanceof Chicken)
        {
            if (entity.getVehicle() == null)
                entity.startRiding(player);
            else
            {
                if (player.isCrouching())
                    entity.stopRiding();
            }
        }
    }

    @SubscribeEvent
    public static void onLivingUpdateEvent(LivingEvent.LivingUpdateEvent event)
    {
        LivingEntity entity = event.getEntityLiving();

        if (entity.hasEffect(PotionRegistryHandler.CLAY_SLOW_EFFECT.get()))
        {
            int newAmp = (int) (6.0F * (1.0F - ((float) entity.getEffect(PotionRegistryHandler.CLAY_SLOW_EFFECT.get()).getDuration() / 2000.0F)));

            if (newAmp != entity.getEffect(PotionRegistryHandler.CLAY_SLOW_EFFECT.get()).getAmplifier())
            {
                entity.addEffect(new MobEffectInstance(PotionRegistryHandler.CLAY_SLOW_EFFECT.get(), entity.getEffect(PotionRegistryHandler.CLAY_SLOW_EFFECT.get()).getDuration(),
                        newAmp, false, true));
            }
        }
    }

    @SubscribeEvent
    public static void onLivingFallEvent(LivingEvent event)
    {
        LivingEntity entity = event.getEntityLiving();

        if (entity instanceof Player)
        {
            if (entity.getPassengers().size() > 0 && entity.getPassengers().get(0) instanceof Chicken)
            {
                Vec3 vec3d = entity.getDeltaMovement();
                entity.setDeltaMovement(vec3d.multiply(1.0D, 0.95D, 1.0D));
            }
        }
    }

    @SubscribeEvent
    public static void onLivingHurtEvent(LivingHurtEvent event)
    {
        LivingEntity entity = event.getEntityLiving();
        Level world = entity.level;

        if (!world.isClientSide)
        {
            if (entity.getPassengers().size() > 0 && entity.getPassengers().get(0) instanceof Chicken)
            {
                if (event.getSource() == DamageSource.FALL)
                {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onAttackedEvent(LivingDamageEvent event)
    {
        LivingEntity entity = event.getEntityLiving();
        Level world = entity.level;

        if (!world.isClientSide)
        {
            if (entity instanceof Fox)
            {
                List<KitsuneEntity> ent_list = EntityHelper.getEntitiesNearby(entity, KitsuneEntity.class, 16.0D);

                for (KitsuneEntity kit : ent_list)
                {
                    if (kit.getTarget() == null)
                    {
                        kit.setTarget((LivingEntity) event.getSource().getEntity());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onJoinWorld(EntityJoinWorldEvent event)
    {
        Entity entity = event.getEntity();

        Level world = entity.level;

        if (!world.isClientSide)
        {
            if (entity instanceof Zombie)
            {
                ((Zombie) entity).targetSelector.addGoal(2, new NearestAttackableTargetGoal<HunterEntity>((Zombie) entity, HunterEntity.class, true));
            }

            if (entity instanceof Pillager)
            {
                ((Pillager) entity).targetSelector.addGoal(2, new NearestAttackableTargetGoal<HunterEntity>((Pillager) entity, HunterEntity.class, true));
            }

            if (entity instanceof Cow || entity instanceof Rabbit || entity instanceof Sheep || entity instanceof Horse || entity instanceof Pig || entity instanceof Chicken)
            {
                ((Animal) entity).goalSelector.addGoal(1, new AnimalMeleeAttackGoal(((Animal) entity), 1.0D, true, 2, 1));
            }
        }
    }
}
