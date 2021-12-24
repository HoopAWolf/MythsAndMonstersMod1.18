package com.hoopawolf.mwaw.entities.projectiles;

import com.hoopawolf.mwaw.entities.ClayGolemEntity;
import com.hoopawolf.mwaw.util.EntityRegistryHandler;
import com.hoopawolf.mwaw.util.PotionRegistryHandler;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

public class ClayEntity extends ThrowableItemProjectile
{
    public ClayEntity(EntityType<? extends ClayEntity> p_i50159_1_, Level p_i50159_2_)
    {
        super(p_i50159_1_, p_i50159_2_);
    }

    public ClayEntity(Level worldIn, LivingEntity throwerIn)
    {
        super(EntityRegistryHandler.CLAY_ENTITY.get(), throwerIn, worldIn);
    }

    public ClayEntity(Level worldIn, double x, double y, double z)
    {
        super(EntityRegistryHandler.CLAY_ENTITY.get(), x, y, z, worldIn);
    }

    @Override
    protected Item getDefaultItem()
    {
        return Items.CLAY_BALL;
    }

    private ParticleOptions createParticle()
    {
        ItemStack itemstack = this.getItemRaw();
        return itemstack.isEmpty() ? ParticleTypes.MYCELIUM : new ItemParticleOption(ParticleTypes.ITEM, itemstack);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleEntityEvent(byte id)
    {
        if (id == 3)
        {
            ParticleOptions iparticledata = this.createParticle();

            for (int i = 0; i < 8; ++i)
            {
                this.level.addParticle(iparticledata, this.getX(), this.getY(), this.getZ(), 0.0D, 0.0D, 0.0D);
            }
        }

    }

    @Override
    protected void onHitEntity(EntityHitResult result)
    {
        if (result.getType() == EntityHitResult.Type.ENTITY)
        {
            Entity entity = result.getEntity();
            entity.hurt(DamageSource.thrown(this, this.getOwner()), 1.0F);
            if (entity instanceof LivingEntity && !(entity instanceof ClayGolemEntity) && !((LivingEntity) entity).hasEffect(PotionRegistryHandler.CLAY_SLOW_EFFECT.get()))
            {
                MobEffectInstance effectinstance = new MobEffectInstance(PotionRegistryHandler.CLAY_SLOW_EFFECT.get(), 2000);
                ((LivingEntity) entity).addEffect(effectinstance);
            }
        }

        if (!this.level.isClientSide)
        {
            this.level.broadcastEntityEvent(this, (byte) 3);
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public Packet<?> getAddEntityPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}