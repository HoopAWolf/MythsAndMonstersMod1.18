package com.hoopawolf.mwaw.entities.projectiles;

import com.hoopawolf.mwaw.entities.PyromancerEntity;
import com.hoopawolf.mwaw.network.MWAWPacketHandler;
import com.hoopawolf.mwaw.network.packets.client.SpawnParticleMessage;
import com.hoopawolf.mwaw.network.packets.client.SpawnSuckingParticleMessage;
import com.hoopawolf.mwaw.util.EntityRegistryHandler;
import com.hoopawolf.mwaw.util.ParticleRegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

public class SpiritBombEntity extends AbstractHurtingProjectile
{
    private static final EntityDataAccessor<Integer> CHARGE_TIMER = SynchedEntityData.defineId(SpiritBombEntity.class, EntityDataSerializers.INT);
    public int innerRotation;
    private LivingEntity owner;
    private Entity target;
    private Direction direction;
    private double targetDeltaX;
    private double targetDeltaY;
    private double targetDeltaZ;
    private boolean hasShot;

    public SpiritBombEntity(EntityType<? extends SpiritBombEntity> p_i50161_1_, Level p_i50161_2_)
    {
        super(p_i50161_1_, p_i50161_2_);
        this.setNoGravity(true);
        innerRotation = 0;
        hasShot = false;
    }

    @OnlyIn(Dist.CLIENT)
    public SpiritBombEntity(Level worldIn, double x, double y, double z, double motionXIn, double motionYIn, double motionZIn)
    {
        this(EntityRegistryHandler.SPIRIT_BOMB_ENTITY.get(), worldIn);
        this.moveTo(x, y, z, this.getYRot(), this.getXRot());
        this.setDeltaMovement(motionXIn, motionYIn, motionZIn);
    }

    public SpiritBombEntity(Level worldIn, LivingEntity ownerIn, Entity targetIn)
    {
        this(EntityRegistryHandler.SPIRIT_BOMB_ENTITY.get(), worldIn);
        this.owner = ownerIn;
        BlockPos blockpos = ownerIn.blockPosition();
        double d0 = (double) blockpos.getX() + 0.5D;
        double d1 = (double) blockpos.getY() + 0.5D;
        double d2 = (double) blockpos.getZ() + 0.5D;
        this.moveTo(d0, d1, d2, this.getYRot(), this.getXRot());
        this.target = targetIn;
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(CHARGE_TIMER, 0);
    }

    public int getChargeTimer()
    {
        return this.entityData.get(CHARGE_TIMER);
    }

    public void setChargeTimer(int timerIn)
    {
        this.entityData.set(CHARGE_TIMER, timerIn);
    }

    @Override
    public SoundSource getSoundSource()
    {
        return SoundSource.HOSTILE;
    }

    @Override
    public void checkDespawn()
    {
        if (this.level.getDifficulty() == Difficulty.PEACEFUL)
        {
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    public void tick()
    {
        super.tick();
        ++innerRotation;

        if (!level.isClientSide)
        {
            if (getChargeTimer() < 100)
            {
                if (!(owner instanceof PyromancerEntity && owner.isAlive()))
                {
                    removeAfterChangingDimensions();
                }
            }

            Vec3 _vec = new Vec3(this.getX(), this.getY(0.5D), this.getZ());
            SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(_vec, new Vec3(0, 0, 0), 1, 10, getBbWidth() * getCharge());
            MWAWPacketHandler.packetHandler.sendToDimension(this.level.dimension(), spawnParticleMessage);

            if (hasShot)
            {
                if (target != null)
                {
                    if (target instanceof LivingEntity)
                    {
                        float f = 0.01F;
                        double d1 = target.getX() - this.getX();
                        double d2 = target.getY(0.5D) - this.getY(0.5D);
                        double d3 = target.getZ() - this.getZ();
                        this.xPower = Mth.sign(d1) * (double) f;
                        this.yPower = Mth.sign(d2) * (double) f;
                        this.zPower = Mth.sign(d3) * (double) f;
                    } else
                    {
                        removeAfterChangingDimensions();
                    }
                } else
                {
                    removeAfterChangingDimensions();
                }
            }
        }
    }

    @Override
    protected float getInertia()
    {
        return 1.0F;
    }

    @Override
    public boolean isOnFire()
    {
        return false;
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance)
    {
        return distance < 16384.0D;
    }

    @Override
    public float getBrightness()
    {
        return 1.0F;
    }

    @Override
    protected ParticleOptions getTrailParticle()
    {
        return ParticleRegistryHandler.FIRE_PARTICLE.get();
    }

    private void setDeadEffect()
    {
        if (!level.isClientSide)
        {
            Explosion.BlockInteraction explosion$mode = net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.level, this.getOwner()) ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
            this.level.explode(this, this.getX(), this.getY(), this.getZ(), 10.0F * getCharge(), false, explosion$mode);
            this.remove(RemovalReason.KILLED);
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result)
    {
        super.onHitEntity(result);
        if (!this.level.isClientSide)
        {
            if (result.getType() == EntityHitResult.Type.ENTITY)
            {
                Entity entity = result.getEntity();
                if (!(entity instanceof PyromancerEntity))
                {
                    entity.hurt(DamageSource.MAGIC, 20.0F * getCharge());
                    this.setDeadEffect();
                }
            } else
            {
                this.setDeadEffect();
            }
        }
    }

    public void increaseCharge()
    {
        SpawnSuckingParticleMessage spawnParticleMessage = new SpawnSuckingParticleMessage(new Vec3(getX(), getY() + 1.0F, getZ()), new Vec3(0.1D, 0.1D, 0.1D), 10, 2, 0.5F);
        MWAWPacketHandler.packetHandler.sendToDimension(this.level.dimension(), spawnParticleMessage);
        setChargeTimer(Mth.clamp(getChargeTimer() + 1, 0, 100));
    }

    public void setHaveShot(boolean hasShotIn)
    {
        hasShot = hasShotIn;
    }

    public void setTarget(Entity targetIn)
    {
        target = targetIn;
    }

    public float getCharge()
    {
        return (float) getChargeTimer() / 100.0F;
    }

    @Override
    public boolean hurt(DamageSource source, float amount)
    {
        return false;
    }

    @Override
    protected boolean shouldBurn()
    {
        return false;
    }

    @Override
    public Packet<?> getAddEntityPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
