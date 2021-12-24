package com.hoopawolf.mwaw.entities;

import com.hoopawolf.mwaw.client.animation.AnimationHelper;
import com.hoopawolf.mwaw.client.animation.PercentageRotation;
import com.hoopawolf.mwaw.entities.ai.MWAWMeleeAttackGoal;
import com.hoopawolf.mwaw.entities.ai.controller.MWAWMovementController;
import com.hoopawolf.mwaw.entities.ai.navigation.MWAWPathNavigateGround;
import com.hoopawolf.mwaw.entities.helper.EntityHelper;
import com.hoopawolf.mwaw.entities.helper.MathFuncHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Rotations;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

public class GiantEntity extends PathfinderMob
{
    private static final EntityDataAccessor<Boolean> ATTACKING_ARM = SynchedEntityData.defineId(GiantEntity.class, EntityDataSerializers.BOOLEAN); //true - left false - right
    private static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(GiantEntity.class, EntityDataSerializers.INT); //STATE: NORMAL MELEE, SLAM ATTACk
    private static final EntityDataAccessor<Float> SLAM_TIMER = SynchedEntityData.defineId(GiantEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> ATTACK_TIMER = SynchedEntityData.defineId(GiantEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> ANIMATION_SPEED = SynchedEntityData.defineId(GiantEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Rotations> HEAD_ROTATION = SynchedEntityData.defineId(GiantEntity.class, EntityDataSerializers.ROTATIONS);
    private static final EntityDataAccessor<Rotations> BODY_ROTATION = SynchedEntityData.defineId(GiantEntity.class, EntityDataSerializers.ROTATIONS);
    private static final EntityDataAccessor<Rotations> RIGHT_ARM_ROTATION = SynchedEntityData.defineId(GiantEntity.class, EntityDataSerializers.ROTATIONS);
    private static final EntityDataAccessor<Rotations> LEFT_ARM_ROTATION = SynchedEntityData.defineId(GiantEntity.class, EntityDataSerializers.ROTATIONS);
    private static final EntityDataAccessor<Rotations> RIGHT_LEG_ROTATION = SynchedEntityData.defineId(GiantEntity.class, EntityDataSerializers.ROTATIONS);
    private static final EntityDataAccessor<Rotations> LEFT_LEG_ROTATION = SynchedEntityData.defineId(GiantEntity.class, EntityDataSerializers.ROTATIONS);
    public final AnimationHelper animation = new AnimationHelper();
    private final float slamTimerMax = 20, attackTimerMax = 10;
    //STATE: NORMAL MELEE, SLAM ATTACk

    public GiantEntity(EntityType<? extends GiantEntity> type, Level worldIn)
    {
        super(type, worldIn);
        this.maxUpStep = 1.0F;

        animation.defineSynchedData(HEAD_ROTATION);
        animation.defineSynchedData(BODY_ROTATION);
        animation.defineSynchedData(RIGHT_ARM_ROTATION);
        animation.defineSynchedData(LEFT_ARM_ROTATION);
        animation.defineSynchedData(LEFT_LEG_ROTATION);
        animation.defineSynchedData(RIGHT_LEG_ROTATION);

        this.moveControl = new MWAWMovementController(this, 30);
    }

    public static AttributeSupplier.Builder func_234321_m_()
    {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 300.0D).add(Attributes.ATTACK_DAMAGE, 8.0D)
                .add(Attributes.FOLLOW_RANGE, 12.0D).add(Attributes.MOVEMENT_SPEED, 0.2D);
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(ATTACKING_ARM, false);
        this.entityData.define(STATE, 0);
        this.entityData.define(SLAM_TIMER, 0F);
        this.entityData.define(ATTACK_TIMER, 0F);
        this.entityData.define(ANIMATION_SPEED, 0F);

        this.entityData.define(HEAD_ROTATION, new Rotations(0, 0, 0));
        this.entityData.define(BODY_ROTATION, new Rotations(0, 0, 0));
        this.entityData.define(RIGHT_ARM_ROTATION, new Rotations(0, 0, 0));
        this.entityData.define(LEFT_ARM_ROTATION, new Rotations(0, 0, 0));
        this.entityData.define(LEFT_LEG_ROTATION, new Rotations(0, 0, 0));
        this.entityData.define(RIGHT_LEG_ROTATION, new Rotations(0, 0, 0));
    }

    @Override
    protected PathNavigation createNavigation(Level world)
    {
        return new MWAWPathNavigateGround(this, world);
    }

    public boolean getAttackingArm()
    {
        return entityData.get(ATTACKING_ARM);
    }

    public void setAttackingArm(boolean isLeft)
    {
        entityData.set(ATTACKING_ARM, isLeft);
    }

    public float getAnimationSpeed()
    {
        return entityData.get(ANIMATION_SPEED);
    }

    public void setAnimationSpeed(float speedIn)
    {
        entityData.set(ANIMATION_SPEED, speedIn);
    }

    public int getState()
    {
        return entityData.get(STATE);
    }

    public void setState(int state)
    {
        entityData.set(STATE, state);
    }

    public float getSlamTimer()
    {
        return entityData.get(SLAM_TIMER);
    }

    public void setSlamTimer(float timer)
    {
        entityData.set(SLAM_TIMER, timer);
    }

    public float getAttackTimer()
    {
        return entityData.get(ATTACK_TIMER);
    }

    public void setAttackTimer(float timer)
    {
        entityData.set(ATTACK_TIMER, timer);
    }

    public float getSlamTimerMax()
    {
        return slamTimerMax;
    }

    public Rotations getHeadRotation()
    {
        return entityData.get(HEAD_ROTATION);
    }

    public Rotations getBodyRotation()
    {
        return entityData.get(BODY_ROTATION);
    }

    public Rotations getRightArmRotation()
    {
        return entityData.get(RIGHT_ARM_ROTATION);
    }

    public Rotations getLeftArmRotation()
    {
        return entityData.get(LEFT_ARM_ROTATION);
    }

    public Rotations getRightLegRotation()
    {
        return entityData.get(RIGHT_LEG_ROTATION);
    }

    public Rotations getLeftLegRotation()
    {
        return entityData.get(LEFT_LEG_ROTATION);
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new GiantEntity.GiantGroundSlamGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 4.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, PathfinderMob.class, 4.0F));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PathfinderMob.class, 10, true, false, (p_213621_0_) ->
        {
            return !(p_213621_0_ instanceof GiantEntity);
        }));
    }

    @Override
    protected boolean shouldDespawnInPeaceful()
    {
        return true;
    }

    @Override
    public MobType getMobType()
    {
        return MobType.UNDEFINED;
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn)
    {
        return worldIn.canSeeSky(blockPosition()) && worldIn.getBlockState(this.getOnPos()).getBlock().equals(Blocks.GRASS_BLOCK);
    }

    @Override
    public void tick()
    {
        super.tick();

        if (!level.isClientSide)
        {
            switch (getState())
            {
                case 0:
                {
                    if (getAttackTimer() > 0)
                    {
                        setAttackTimer(getAttackTimer() - 1.0F);
                    }
                }
                break;
            }
        } else
        {
            switch (getState())
            {
                case 0:
                {
                    setAnimationSpeed(0.08F);

                    if (getAttackTimer() > attackTimerMax * 0.3F)
                    {
                        if (getAttackingArm())
                        {
                            animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(-95, -35, 0)));
                            animation.registerRotationPoints(BODY_ROTATION, new PercentageRotation(getBodyRotation(), new Rotations(-15, -11, 0)));
                        } else
                        {
                            animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(-95, 35, 0)));
                            animation.registerRotationPoints(BODY_ROTATION, new PercentageRotation(getBodyRotation(), new Rotations(-15, 11, 0)));
                        }
                    } else if (getAttackTimer() > 0)
                    {
                        if (getAttackingArm())
                        {
                            animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(-55, 25, 0)));
                            animation.registerRotationPoints(BODY_ROTATION, new PercentageRotation(getBodyRotation(), new Rotations(10, 22, -3)));
                        } else
                        {
                            animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(-55, -25, 0)));
                            animation.registerRotationPoints(BODY_ROTATION, new PercentageRotation(getBodyRotation(), new Rotations(10, -22, 3)));
                        }
                    } else
                    {
                        animation.registerRotationPoints(HEAD_ROTATION, new PercentageRotation(getHeadRotation(), new Rotations(0, 0, 0)));
                        animation.registerRotationPoints(BODY_ROTATION, new PercentageRotation(getBodyRotation(), new Rotations(0, 0, 0)));

                        animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(0, 0, 0)));

                        animation.registerRotationPoints(RIGHT_LEG_ROTATION, new PercentageRotation(getRightLegRotation(), new Rotations(0, 0, 0)));
                        animation.registerRotationPoints(LEFT_LEG_ROTATION, new PercentageRotation(getLeftLegRotation(), new Rotations(0, 0, 0)));
                    }
                }
                break;

                case 1:
                {

                }
                break;

                case 2:
                {
                    if (getSlamTimer() < slamTimerMax * 0.8F)
                    {
                        setAnimationSpeed(0.05F);

                        animation.registerRotationPoints(HEAD_ROTATION, new PercentageRotation(getHeadRotation(), new Rotations(-40, 0, 0)));
                        animation.registerRotationPoints(BODY_ROTATION, new PercentageRotation(getBodyRotation(), new Rotations(-25, 0, 0)));

                        animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(-140, 0, 30)));

                        animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(-140, 0, -30)));

                        animation.registerRotationPoints(RIGHT_LEG_ROTATION, new PercentageRotation(getRightLegRotation(), new Rotations(-15, 20, 0)));
                        animation.registerRotationPoints(LEFT_LEG_ROTATION, new PercentageRotation(getLeftLegRotation(), new Rotations(-15, -20, 0)));
                    } else if (getSlamTimer() < slamTimerMax)
                    {
                        setAnimationSpeed(0.09F);

                        animation.registerRotationPoints(HEAD_ROTATION, new PercentageRotation(getHeadRotation(), new Rotations(-55, 0, 0)));
                        animation.registerRotationPoints(BODY_ROTATION, new PercentageRotation(getBodyRotation(), new Rotations(85, 0, 0)));

                        animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(-55, -30, -5)));
                        animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(-55, 30, 5)));

                        animation.registerRotationPoints(RIGHT_LEG_ROTATION, new PercentageRotation(getRightLegRotation(), new Rotations(-15, 20, 0)));
                        animation.registerRotationPoints(LEFT_LEG_ROTATION, new PercentageRotation(getLeftLegRotation(), new Rotations(-15, -20, 0)));
                    }
                }
                break;
            }
        }
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn)
    {
        this.playSound(this.getStepSound(), 1.15F, 0.1F);
    }

    protected SoundEvent getStepSound()
    {
        return SoundEvents.STONE_BREAK;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR;
    }

    @Override
    public int getMaxSpawnClusterSize()
    {
        return 1;
    }

    @Override
    public boolean doHurtTarget(Entity entityIn)
    {
        this.setAttackTimer(attackTimerMax);
        setAttackingArm(random.nextInt(2) == 1);
        this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 0.1F);

        return super.doHurtTarget(entityIn);
    }

    private class GiantAttackGoal extends MWAWMeleeAttackGoal
    {
        GiantEntity host;
        LivingEntity livingentity;

        public GiantAttackGoal(GiantEntity creature, double speedIn, double runningSpeedIn, boolean useLongMemory)
        {
            super(creature, speedIn, runningSpeedIn, useLongMemory);
            host = creature;
        }

        @Override
        public boolean canUse()
        {
            return super.canUse() && host.getState() == 0;
        }

        @Override
        public void start()
        {
            super.start();
        }

        @Override
        public void stop()
        {
            super.stop();
        }

        @Override
        public void tick()
        {
            super.tick();
            livingentity = this.attacker.getTarget();
            double dist = this.attacker.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
        }
    }

    private class GiantGroundSlamGoal extends Goal
    {
        GiantEntity host;
        private int coolDown;

        public GiantGroundSlamGoal(GiantEntity creature)
        {
            host = creature;
            coolDown = 0;

            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        @Override
        public boolean canUse()
        {
            if (host.tickCount % 2 == 0)
            {
                --coolDown;
            }

            if (coolDown > 0)
            {
                return false;
            }

            return host.getTarget() != null && host.distanceTo(host.getTarget()) < 10 && host.getState() == 0 && random.nextInt(100) < 40;
        }

        @Override
        public boolean canContinueToUse()
        {
            return host.getSlamTimer() <= host.getSlamTimerMax();
        }

        @Override
        public void start()
        {
            host.getNavigation().stop();
            host.setSlamTimer(0.0F);
            host.setState(2);
        }

        @Override
        public void stop()
        {
            coolDown = 100;
            host.setState(0);
        }

        @Override
        public void tick()
        {
            if (host.getSlamTimer() <= host.getSlamTimerMax())
            {
                host.setSlamTimer(host.getSlamTimer() + 1.0F);

                if (!host.level.isClientSide)
                {
                    if (host.getSlamTimer() < host.getSlamTimerMax() * 0.1F)
                    {
                        host.playSound(SoundEvents.ILLUSIONER_PREPARE_MIRROR, 5.0F, 0.1F);
                        double d2 = host.getTarget().getX() - host.getX();
                        double d1 = host.getTarget().getZ() - host.getZ();
                        host.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
                        host.yBodyRot = host.getYRot();

                    } else if (host.getSlamTimer() >= host.getSlamTimerMax() * 0.9F)
                    {
                        List<LivingEntity> entities = EntityHelper.getEntityLivingBaseNearby(host, 30, 2, 30, 50);

                        host.playSound(SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, 5.0F, 0.1F);
                        float wide = 0;
                        Vec3 forward = new Vec3(MathFuncHelper.sign(host.getLookAngle().x()), 1, MathFuncHelper.sign(host.getLookAngle().z()));
                        Vec3 right = MathFuncHelper.crossProduct(forward, new Vec3(0, 1, 0));
                        for (int dist = 0; dist < 10; ++dist)
                        {
                            for (int i = -(int) wide; i <= (int) wide; ++i)
                            {
                                BlockPos pos = new BlockPos(host.getX() + (forward.x() * dist) + (right.x() * i), host.getY() - 1, host.getZ() + (forward.z() * dist) + (right.z() * i));
                                FallingBlockEntity fallingblockentity = new FallingBlockEntity(host.level, (double) pos.getX() + 0.5D, pos.getY(), (double) pos.getZ() + 0.5D, host.level.getBlockState(pos));
                                fallingblockentity.setDeltaMovement(0, dist * 0.1F, 0);
                                host.level.addFreshEntity(fallingblockentity);

                                for (LivingEntity entity : entities)
                                {
                                    if (!(entity instanceof Player && ((Player) entity).isCreative()) && !(entity instanceof DendroidElderEntity))
                                    {
                                        if (entity.getX() > pos.getX() - 1 && entity.getX() < pos.getX() + 1 &&
                                                entity.getZ() > pos.getZ() - 1 && entity.getZ() < pos.getZ() + 1)
                                        {
                                            entity.setDeltaMovement(entity.getDeltaMovement().x(), entity.getDeltaMovement().y() + (dist * 0.02F), entity.getDeltaMovement().z());
                                            entity.hurt(new DamageSource("slam"), (dist - 10) * 0.5F);
                                            entity.hurtMarked = true;
                                        }
                                    }
                                }
                            }
                            wide += 0.5F;
                        }
                    }
                }
            }
        }
    }
}
