package com.hoopawolf.mwaw.items;

import com.hoopawolf.mwaw.util.ItemBlockRegistryHandler;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class BadAppleItem extends Item
{
    public BadAppleItem(Item.Properties _prop)
    {
        super(_prop);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving)
    {
        if (!worldIn.isClientSide)
        {
            worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), this.getEatingSound(), SoundSource.NEUTRAL, 1.0F, 1.0F + (worldIn.random.nextFloat() - worldIn.random.nextFloat()) * 0.4F);

            if (entityLiving instanceof Player && !((Player) entityLiving).getInventory().getArmor(3).isEmpty() && ((Player) entityLiving).getInventory().getArmor(3).getItem() == Items.DIAMOND_HELMET)
            {
                entityLiving.addEffect(new MobEffectInstance(new MobEffectInstance(MobEffects.ABSORPTION, 200, 1)));
                entityLiving.addEffect(new MobEffectInstance(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 1)));
                entityLiving.addEffect(new MobEffectInstance(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 1)));
                entityLiving.addEffect(new MobEffectInstance(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 200, 1)));
                entityLiving.addEffect(new MobEffectInstance(new MobEffectInstance(MobEffects.REGENERATION, 100, 1)));
                entityLiving.addEffect(new MobEffectInstance(new MobEffectInstance(MobEffects.SATURATION, 200, 1)));
            } else
            {
                entityLiving.addEffect(new MobEffectInstance(new MobEffectInstance(MobEffects.BLINDNESS, 200, 1)));
                entityLiving.addEffect(new MobEffectInstance(new MobEffectInstance(MobEffects.BAD_OMEN, 200, 1)));
                entityLiving.addEffect(new MobEffectInstance(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 1)));
                entityLiving.addEffect(new MobEffectInstance(new MobEffectInstance(MobEffects.WEAKNESS, 200, 1)));
                entityLiving.addEffect(new MobEffectInstance(new MobEffectInstance(MobEffects.WITHER, 100, 1)));
                entityLiving.addEffect(new MobEffectInstance(new MobEffectInstance(MobEffects.HUNGER, 200, 1)));
            }

            if (!(entityLiving instanceof Player) || !((Player) entityLiving).getAbilities().instabuild)
            {
                stack.shrink(1);
            }

            if (worldIn.random.nextInt(100) < 50)
                worldIn.addFreshEntity(new ItemEntity(worldIn, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), new ItemStack(ItemBlockRegistryHandler.TAINTED_SEED.get())));
        }

        return stack;
    }
}
