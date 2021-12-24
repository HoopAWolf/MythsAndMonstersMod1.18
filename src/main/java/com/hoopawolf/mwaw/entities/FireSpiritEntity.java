package com.hoopawolf.mwaw.entities;

import com.hoopawolf.mwaw.network.MWAWPacketHandler;
import com.hoopawolf.mwaw.network.packets.client.SpawnParticleMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class FireSpiritEntity extends PathfinderMob
{
    protected static final EntityDataAccessor<Byte> CHARGE_FLAG = SynchedEntityData.defineId(FireSpiritEntity.class, EntityDataSerializers.BYTE);
    private LivingEntity owner;
    @Nullable
    private BlockPos boundOrigin;

    public FireSpiritEntity(EntityType<? extends FireSpiritEntity> p_i50190_1_, Level p_i50190_2_)
    {
        super(p_i50190_1_, p_i50190_2_);
        this.moveControl = new FireSpiritEntity.MoveHelperController(this);
    }

    public static AttributeSupplier.Builder func_234321_m_()
    {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 2.0D).add(Attributes.ATTACK_DAMAGE, 4.0D);
    }

    @Override
    public void move(MoverType typeIn, Vec3 pos)
    {
        super.move(typeIn, pos);
        this.checkInsideBlocks();
    }

    @Override
    public void tick()
    {
        this.noPhysics = true;
        super.tick();
        this.noPhysics = false;
        this.setNoGravity(true);

        if (!level.isClientSide)
        {
            SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(new Vec3(this.getX(), this.getY(), this.getZ()), new Vec3(0.0F, 0.0D, 0.0F), 2, 9, 0);
            MWAWPacketHandler.packetHandler.sendToDimension(this.level.dimension(), spawnParticleMessage);

            if (owner == null)
            {
                this.remove(RemovalReason.KILLED);
            }

            if (getTarget() != null)
            {
                if (!getTarget().isAlive())
                {
                    remove(RemovalReason.KILLED);
                }
            }
        }
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new FireSpiritEntity.ChargeAttackGoal());
        this.goalSelector.addGoal(8, new FireSpiritEntity.MoveRandomGoal());
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PathfinderMob.class, 10, true, false, (p_213621_0_) ->
        {
            return !(p_213621_0_ instanceof Pillager) && !(p_213621_0_ instanceof PyromancerEntity) && !(p_213621_0_ instanceof Blaze) && !(p_213621_0_ instanceof FireSpiritEntity);  //TODO future cult member
        }));
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(CHARGE_FLAG, (byte) 0);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readAdditionalSaveData(CompoundTag compound)
    {
        super.readAdditionalSaveData(compound);
        if (compound.contains("BoundX"))
        {
            this.boundOrigin = new BlockPos(compound.getInt("BoundX"), compound.getInt("BoundY"), compound.getInt("BoundZ"));
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound)
    {
        super.addAdditionalSaveData(compound);
        if (this.boundOrigin != null)
        {
            compound.putInt("BoundX", this.boundOrigin.getX());
            compound.putInt("BoundY", this.boundOrigin.getY());
            compound.putInt("BoundZ", this.boundOrigin.getZ());
        }
    }

    public LivingEntity getOwner()
    {
        return this.owner;
    }

    public void setOwner(LivingEntity ownerIn)
    {
        this.owner = ownerIn;
    }

    @Nullable
    public BlockPos getBoundOrigin()
    {
        return (this.owner != null ? this.owner.blockPosition() : null);
    }

    private boolean getChargeFlag(int mask)
    {
        int i = entityData.get(CHARGE_FLAG);
        return (i & mask) != 0;
    }

    private void setChargeFlag(int mask, boolean value)
    {
        int i = entityData.get(CHARGE_FLAG);
        if (value)
        {
            i = i | mask;
        } else
        {
            i = i & ~mask;
        }

        entityData.set(CHARGE_FLAG, (byte) (i & 255));
    }

    public boolean isCharging()
    {
        return this.getChargeFlag(1);
    }

    public void setCharging(boolean charging)
    {
        this.setChargeFlag(1, charging);
    }

    @Override
    public float getBrightness()
    {
        return 1.0F;
    }

    @Override
    public boolean hurt(DamageSource source, float amount)
    {
        if (!level.isClientSide)
        {
            if (source.getEntity() != null)
            {
                owner.hurt(source, amount * 0.2F);
                return super.hurt(source, amount);
            }
        }

        return false;
    }

    class ChargeAttackGoal extends Goal
    {
        public ChargeAttackGoal()
        {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse()
        {
            return FireSpiritEntity.this.getTarget() != null && FireSpiritEntity.this.random.nextInt(3) == 0;
        }

        @Override
        public boolean canContinueToUse()
        {
            return FireSpiritEntity.this.getMoveControl().hasWanted() && FireSpiritEntity.this.isCharging() && FireSpiritEntity.this.getTarget() != null && FireSpiritEntity.this.getTarget().isAlive();
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        @Override
        public void start()
        {
            LivingEntity livingentity = FireSpiritEntity.this.getTarget();
            Vec3 Vec3d = livingentity.getEyePosition(1.0F);
            FireSpiritEntity.this.moveControl.setWantedPosition(Vec3d.x, Vec3d.y, Vec3d.z, 1.0D);
            FireSpiritEntity.this.setCharging(true);
            FireSpiritEntity.this.playSound(SoundEvents.PUFFER_FISH_BLOW_OUT, 1.0F, 0.5F);
        }

        @Override
        public void stop()
        {
            FireSpiritEntity.this.setCharging(false);
        }

        @Override
        public void tick()
        {
            LivingEntity livingentity = FireSpiritEntity.this.getTarget();
            if (FireSpiritEntity.this.getBoundingBox().intersects(livingentity.getBoundingBox()))
            {
                FireSpiritEntity.this.doHurtTarget(livingentity);
                livingentity.setSecondsOnFire(100);
                FireSpiritEntity.this.setCharging(false);
            } else
            {
                Vec3 Vec3d = livingentity.getEyePosition(1.0F);
                FireSpiritEntity.this.moveControl.setWantedPosition(Vec3d.x, Vec3d.y, Vec3d.z, 1.0D);
            }

        }
    }

    class MoveHelperController extends MoveControl
    {
        public MoveHelperController(FireSpiritEntity vex)
        {
            super(vex);
        }

        @Override
        public void tick()
        {
            if (this.operation == MoveControl.Operation.MOVE_TO)
            {
                Vec3 Vec3d = new Vec3(this.getWantedX() - FireSpiritEntity.this.getX(), this.getWantedY() - FireSpiritEntity.this.getY(), this.getWantedZ() - FireSpiritEntity.this.getZ());
                double d0 = Vec3d.length();
                if (d0 < FireSpiritEntity.this.getBoundingBox().getSize())
                {
                    this.operation = MoveControl.Operation.WAIT;
                    FireSpiritEntity.this.setDeltaMovement(FireSpiritEntity.this.getDeltaMovement().scale(0.5D));
                } else
                {
                    FireSpiritEntity.this.setDeltaMovement(FireSpiritEntity.this.getDeltaMovement().add(Vec3d.scale(this.speedModifier * 0.05D / d0)));
                    if (FireSpiritEntity.this.getTarget() == null)
                    {
                        Vec3 Vec3d1 = FireSpiritEntity.this.getDeltaMovement();
                        FireSpiritEntity.this.setYRot(-((float) Mth.atan2(Vec3d1.x, Vec3d1.z)) * (180F / (float) Math.PI));
                    } else
                    {
                        double d2 = FireSpiritEntity.this.getTarget().getX() - FireSpiritEntity.this.getX();
                        double d1 = FireSpiritEntity.this.getTarget().getZ() - FireSpiritEntity.this.getZ();
                        FireSpiritEntity.this.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
                    }
                    FireSpiritEntity.this.yBodyRot = FireSpiritEntity.this.getYRot();
                }

            }
        }
    }

    class MoveRandomGoal extends Goal
    {
        public MoveRandomGoal()
        {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        /**
         * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
         * method as well.
         */
        @Override
        public boolean canUse()
        {
            return !FireSpiritEntity.this.getMoveControl().hasWanted() && FireSpiritEntity.this.random.nextInt(7) == 0;
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        @Override
        public boolean canContinueToUse()
        {
            return false;
        }

        /**
         * Keep ticking a continuous task that has already been started
         */
        @Override
        public void tick()
        {
            BlockPos blockpos = FireSpiritEntity.this.getBoundOrigin();
            if (blockpos == null)
            {
                blockpos = FireSpiritEntity.this.blockPosition();
            }

            for (int i = 0; i < 3; ++i)
            {
                BlockPos blockpos1 = blockpos.offset(FireSpiritEntity.this.random.nextInt(10) - 4, FireSpiritEntity.this.random.nextInt(10) - 4, FireSpiritEntity.this.random.nextInt(10) - 4);
                if (FireSpiritEntity.this.level.isEmptyBlock(blockpos1))
                {
                    FireSpiritEntity.this.moveControl.setWantedPosition((double) blockpos1.getX() + 0.5D, (double) blockpos1.getY() + 0.5D, (double) blockpos1.getZ() + 0.5D, 0.25D);
                    if (FireSpiritEntity.this.getTarget() == null)
                    {
                        FireSpiritEntity.this.getLookControl().setLookAt((double) blockpos1.getX() + 0.5D, (double) blockpos1.getY() + 0.5D, (double) blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
                    }
                    break;
                }
            }

        }
    }
}
