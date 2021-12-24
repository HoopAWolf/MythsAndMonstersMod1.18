package com.hoopawolf.mwaw.entities.projectiles;

import com.hoopawolf.mwaw.util.EntityRegistryHandler;
import com.hoopawolf.mwaw.util.ItemBlockRegistryHandler;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

public class SapEntity extends ThrowableItemProjectile
{
    public SapEntity(EntityType<? extends SapEntity> p_i50159_1_, Level p_i50159_2_)
    {
        super(p_i50159_1_, p_i50159_2_);
    }

    public SapEntity(Level worldIn, LivingEntity throwerIn)
    {
        super(EntityRegistryHandler.SAP_ENTITY.get(), throwerIn, worldIn);
    }

    public SapEntity(Level worldIn, double x, double y, double z)
    {
        super(EntityRegistryHandler.SAP_ENTITY.get(), x, y, z, worldIn);
    }

    @Override
    protected Item getDefaultItem()
    {
        return ItemBlockRegistryHandler.SAP.get();
    }

    private ParticleOptions createParticle()
    {
        ItemStack itemstack = this.getItemRaw();
        return itemstack.isEmpty() ? ParticleTypes.ITEM_SLIME : new ItemParticleOption(ParticleTypes.ITEM, itemstack);
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
            if (entity instanceof LivingEntity)
            {
                MobEffectInstance effectinstance = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200);
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