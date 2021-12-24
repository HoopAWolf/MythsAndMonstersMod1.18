package com.hoopawolf.mwaw.entities.projectiles;

import com.hoopawolf.mwaw.network.MWAWPacketHandler;
import com.hoopawolf.mwaw.network.packets.client.SpawnSuckingParticleMessage;
import com.hoopawolf.mwaw.util.EntityRegistryHandler;
import com.hoopawolf.mwaw.util.ParticleRegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

public class FoxHeadEntity extends AbstractHurtingProjectile
{
    private LivingEntity owner;
    private Entity target;
    private float startTimer;

    public FoxHeadEntity(EntityType<? extends FoxHeadEntity> p_i50161_1_, Level p_i50161_2_)
    {
        super(p_i50161_1_, p_i50161_2_);
        this.noPhysics = true;
        this.setNoGravity(true);
        startTimer = 20.0F;
    }

    @OnlyIn(Dist.CLIENT)
    public FoxHeadEntity(Level worldIn, double x, double y, double z, double motionXIn, double motionYIn, double motionZIn)
    {
        this(EntityRegistryHandler.FOX_HEAD_ENTITY.get(), worldIn);
        this.moveTo(x, y, z, this.getYRot(), this.getXRot());
        this.setDeltaMovement(motionXIn, motionYIn, motionZIn);
    }

    public FoxHeadEntity(Level worldIn, LivingEntity ownerIn, Entity targetIn)
    {
        this(EntityRegistryHandler.FOX_HEAD_ENTITY.get(), worldIn);
        this.owner = ownerIn;
        BlockPos blockpos = ownerIn.blockPosition();
        double d0 = (double) blockpos.getX() + 0.5D;
        double d1 = (double) blockpos.getY() + 0.5D;
        double d2 = (double) blockpos.getZ() + 0.5D;
        this.moveTo(d0, d1, d2, this.getYRot(), this.getXRot());
        this.target = targetIn;
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
        if (startTimer <= 0)
        {
            startTimer = 0;

            if (tickCount > 400)
            {
                removeAfterChangingDimensions();
            }

            if (!level.isClientSide)
            {
                if (target != null && target.isAlive())
                {
                    Vec3 _dir = new Vec3(target.position().x(), target.position().y() + 0.25F, target.position().z()).subtract(this.position());
                    this.setDeltaMovement(Mth.sign(_dir.x()) * 0.25F, Mth.sign(_dir.y()) * 0.25F, Mth.sign(_dir.z()) * 0.25F);
                } else
                {
                    removeAfterChangingDimensions();
                }
            }
        } else
        {
            if (tickCount % 2 == 0)
                --startTimer;

            if (!level.isClientSide)
            {
                SpawnSuckingParticleMessage spawnParticleMessage = new SpawnSuckingParticleMessage(new Vec3(getX(), getY() + 0.5F, getZ()), new Vec3(0.1D, 0.1D, 0.1D), 10, 0, 0.5F);
                MWAWPacketHandler.packetHandler.sendToDimension(this.level.dimension(), spawnParticleMessage);
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
        return ParticleRegistryHandler.GREEN_FLAME_PARTICLE.get();
    }

    @Override
    protected void onHitEntity(EntityHitResult result)
    {
        super.onHitEntity(result);
        if (!this.level.isClientSide && startTimer <= 0)
        {
            if (result.getType() == EntityHitResult.Type.ENTITY)
            {
                Entity entity = result.getEntity();
                if (entity != this.owner)
                {
                    if (entity instanceof LivingEntity)
                    {
                        if (entity instanceof Player && !((Player) entity).isCreative())
                        {
                            ((Player) entity).drop(((Player) entity).getInventory().getItem(((Player) entity).getInventory().selected), true, false);
                            ((Player) entity).getInventory().removeItem(((Player) entity).getInventory().getSelected());
                        }

                        this.playSound(SoundEvents.FOX_SCREECH, 1.0F, 1.0F);

                        entity.hurt(new EntityDamageSource("foxhead", this.owner), 8.0F);

                        int i = 0;
                        if (this.level.getDifficulty() == Difficulty.NORMAL)
                        {
                            i = 10;
                        } else if (this.level.getDifficulty() == Difficulty.HARD)
                        {
                            i = 40;
                        }

                        if (i > 0)
                        {
                            ((LivingEntity) entity).addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20 * i, 1));
                        }

                        this.setDeadEffect();
                    }
                }
            }
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount)
    {
        if (!this.level.isClientSide)
        {
            this.playSound(SoundEvents.SHULKER_BULLET_HURT, 1.0F, 1.0F);
            ((ServerLevel) this.level).sendParticles(ParticleTypes.CRIT, this.getX(), this.getY(), this.getZ(), 15, 0.2D, 0.2D, 0.2D, 0.0D);
            this.remove(RemovalReason.KILLED);
        }

        return true;
    }

    private void setDeadEffect()
    {
        if (!level.isClientSide)
        {
            this.playSound(SoundEvents.ILLUSIONER_PREPARE_BLINDNESS, 1.0F, 1.0F);
            SpawnSuckingParticleMessage spawnParticleMessage = new SpawnSuckingParticleMessage(this.position(), new Vec3(0.1D, 0.1D, 0.1D), 10, 0, 0.5F);
            MWAWPacketHandler.packetHandler.sendToDimension(this.level.dimension(), spawnParticleMessage);
        }

        this.remove(RemovalReason.KILLED);
    }

    public float getSpawnPercentage()
    {
        return (100.0F - ((startTimer / 20.0F) * 100.0F)) * 0.01F;
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
