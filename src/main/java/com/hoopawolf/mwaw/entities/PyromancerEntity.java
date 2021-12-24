package com.hoopawolf.mwaw.entities;

import com.hoopawolf.mwaw.client.animation.AnimationHelper;
import com.hoopawolf.mwaw.client.animation.PercentageRotation;
import com.hoopawolf.mwaw.entities.ai.controller.MWAWMovementController;
import com.hoopawolf.mwaw.entities.ai.navigation.MWAWPathNavigateGround;
import com.hoopawolf.mwaw.entities.projectiles.SpiritBombEntity;
import com.hoopawolf.mwaw.network.MWAWPacketHandler;
import com.hoopawolf.mwaw.network.packets.client.SpawnParticleMessage;
import com.hoopawolf.mwaw.util.EntityRegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Rotations;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.EnumSet;

public class PyromancerEntity extends PathfinderMob implements RangedAttackMob
{
    private static final EntityDataAccessor<Boolean> IS_FLYING = SynchedEntityData.defineId(PyromancerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ATTACKING_ARM = SynchedEntityData.defineId(PyromancerEntity.class, EntityDataSerializers.BOOLEAN); //true - left false - right
    private static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(PyromancerEntity.class, EntityDataSerializers.INT); //STATE: NORMAL/EXPLOSIVE FIRE CHARGE, SPIRIT BOMB, FIRE RAIN, FIRE SPIRIT
    private static final EntityDataAccessor<Float> SHOOTING = SynchedEntityData.defineId(PyromancerEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> FIRE_CHARGE_RAIN = SynchedEntityData.defineId(PyromancerEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> SPIRIT_BOMB = SynchedEntityData.defineId(PyromancerEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> FIRE_SPIRIT = SynchedEntityData.defineId(PyromancerEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> EXPLOSIVE_FIRE_CHARGE = SynchedEntityData.defineId(PyromancerEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> ANIMATION_SPEED = SynchedEntityData.defineId(PyromancerEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Rotations> HEAD_ROTATION = SynchedEntityData.defineId(PyromancerEntity.class, EntityDataSerializers.ROTATIONS);
    private static final EntityDataAccessor<Rotations> BODY_ROTATION = SynchedEntityData.defineId(PyromancerEntity.class, EntityDataSerializers.ROTATIONS);
    private static final EntityDataAccessor<Rotations> RIGHT_ARM_ROTATION = SynchedEntityData.defineId(PyromancerEntity.class, EntityDataSerializers.ROTATIONS);
    private static final EntityDataAccessor<Rotations> LEFT_ARM_ROTATION = SynchedEntityData.defineId(PyromancerEntity.class, EntityDataSerializers.ROTATIONS);
    private static final EntityDataAccessor<Rotations> RIGHT_LEG_ROTATION = SynchedEntityData.defineId(PyromancerEntity.class, EntityDataSerializers.ROTATIONS);
    private static final EntityDataAccessor<Rotations> LEFT_LEG_ROTATION = SynchedEntityData.defineId(PyromancerEntity.class, EntityDataSerializers.ROTATIONS);
    public final AnimationHelper animation = new AnimationHelper();
    MoveControl groundController,
            airController;
    private ArrayList<FireSpiritEntity> spiritList = new ArrayList<>();

    public PyromancerEntity(EntityType<? extends PyromancerEntity> type, Level worldIn)
    {
        super(type, worldIn);

        animation.defineSynchedData(HEAD_ROTATION);
        animation.defineSynchedData(BODY_ROTATION);
        animation.defineSynchedData(RIGHT_ARM_ROTATION);
        animation.defineSynchedData(LEFT_ARM_ROTATION);
        animation.defineSynchedData(LEFT_LEG_ROTATION);
        animation.defineSynchedData(RIGHT_LEG_ROTATION);

        groundController = new MWAWMovementController(this, 30);
        airController = new PyromancerEntity.MoveHelperController(this);
        this.moveControl = groundController;
    }

    public static AttributeSupplier.Builder func_234321_m_()
    {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 100.0D).add(Attributes.FOLLOW_RANGE, 12.0D).add(Attributes.MOVEMENT_SPEED, 0.35D);
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(SHOOTING, 0F);
        this.entityData.define(ATTACKING_ARM, false);
        this.entityData.define(IS_FLYING, false);
        this.entityData.define(STATE, 0);
        this.entityData.define(FIRE_CHARGE_RAIN, 0F);
        this.entityData.define(SPIRIT_BOMB, 0F);
        this.entityData.define(FIRE_SPIRIT, 0F);
        this.entityData.define(EXPLOSIVE_FIRE_CHARGE, 0F);
        this.entityData.define(ANIMATION_SPEED, 0F);

        this.entityData.define(HEAD_ROTATION, new Rotations(0, 0, 0));
        this.entityData.define(BODY_ROTATION, new Rotations(0, 0, 0));
        this.entityData.define(RIGHT_ARM_ROTATION, new Rotations(0, 0, 0));
        this.entityData.define(LEFT_ARM_ROTATION, new Rotations(0, 0, 0));
        this.entityData.define(LEFT_LEG_ROTATION, new Rotations(0, 0, 0));
        this.entityData.define(RIGHT_LEG_ROTATION, new Rotations(0, 0, 0));
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SpiritSummoningGoal(this));
        this.goalSelector.addGoal(2, new SpiritBombGoal(this));
        this.goalSelector.addGoal(3, new PyroExplosiveRangedAttackGoal(this, 1.0D, 20.0F));
        this.goalSelector.addGoal(4, new PyroRangedAttackGoal(this, 1.0D, 10.0F));
        this.goalSelector.addGoal(5, new PyroWaterAvoidingRandomWalkingGoal(this, 1.0D));
        this.goalSelector.addGoal(8, new PyromancerEntity.MoveRandomGoal());
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 20.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, PathfinderMob.class, 20.0F));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PathfinderMob.class, 10, true, false, (p_213621_0_) ->
        {
            return !(p_213621_0_ instanceof Pillager) && !(p_213621_0_ instanceof PyromancerEntity) && !(p_213621_0_ instanceof Blaze) && !(p_213621_0_ instanceof FireSpiritEntity);  //TODO future cult member
        }));
    }

    @Override
    protected PathNavigation createNavigation(Level world)
    {
        return new MWAWPathNavigateGround(this, world);
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

    public boolean isFlying()
    {
        return entityData.get(IS_FLYING);
    }

    public void setFlying(boolean isFlying)
    {
        entityData.set(IS_FLYING, isFlying);
    }

    public int getState()
    {
        return entityData.get(STATE);
    }

    public void setState(int state)
    {
        entityData.set(STATE, state);
    }

    public float getShootingTimer()
    {
        return entityData.get(SHOOTING);
    }

    public void setShootingTimer(float _shootTimer)
    {
        entityData.set(SHOOTING, _shootTimer);
    }

    public float getFireChargeRainTimer()
    {
        return entityData.get(FIRE_CHARGE_RAIN);
    }

    public void setFireChargeRainTimer(float timer)
    {
        entityData.set(FIRE_CHARGE_RAIN, timer);
    }

    public float getSpiritBombTimer()
    {
        return entityData.get(SPIRIT_BOMB);
    }

    public void setSpiritBombTimer(float timer)
    {
        entityData.set(SPIRIT_BOMB, timer);
    }

    public float getFireSpiritTimer()
    {
        return entityData.get(FIRE_SPIRIT);
    }

    public void setFireSpiritTimer(float timer)
    {
        entityData.set(FIRE_SPIRIT, timer);
    }

    public float getExplosiveFireChargeTimer()
    {
        return entityData.get(EXPLOSIVE_FIRE_CHARGE);
    }

    public void setExplosiveFireChargeTimer(float timer)
    {
        entityData.set(EXPLOSIVE_FIRE_CHARGE, timer);
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
    public float getBrightness()
    {
        return 1.0F;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.BLAZE_DEATH;
    }

    @Override
    public void tick()
    {
        super.tick();

        if (!level.isClientSide)
        {
            Vec3 vec3d = this.getDeltaMovement();

            Vec3 _vec = new Vec3(this.getX(), this.getY(1.2D), this.getZ());
            SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(_vec, new Vec3(0, 0.1f, 0), 2, 10, getBbWidth() * 0.35F);
            MWAWPacketHandler.packetHandler.sendToDimension(this.level.dimension(), spawnParticleMessage);

            if (!this.onGround && vec3d.y < 0.0D)
            {
                this.setDeltaMovement(vec3d.multiply(1.0D, 0.6D, 1.0D));

                Vec3 leg_vec = new Vec3(this.getX(), this.getY(), this.getZ());
                SpawnParticleMessage spawnFlyingParticleMessage = new SpawnParticleMessage(leg_vec, new Vec3(0, -0.1f, 0), 2, 10, getBbWidth() * 0.35F);
                MWAWPacketHandler.packetHandler.sendToDimension(this.level.dimension(), spawnFlyingParticleMessage);
            }

            if (isOnFire())
            {
                clearFire();
            }

            setFlying(getTarget() != null && getTarget().isAlive());

            if (isFlying())
            {
                Vec3 leg_vec = new Vec3(this.getX(), this.getY(), this.getZ());
                SpawnParticleMessage spawnFlyingParticleMessage = new SpawnParticleMessage(leg_vec, new Vec3(0, -0.1f, 0), 2, 10, getBbWidth() * 0.35F);
                MWAWPacketHandler.packetHandler.sendToDimension(this.level.dimension(), spawnFlyingParticleMessage);

                if (!this.moveControl.equals(airController))
                {
                    this.moveControl = airController;
                    this.setNoGravity(true);
                }
            } else
            {
                if (!this.moveControl.equals(groundController))
                {
                    this.moveControl = groundController;
                    this.setNoGravity(false);
                }
            }
        } else
        {
            switch (getState())
            {
                case 0:
                {
                    if (getShootingTimer() > 0.5F)
                    {
                        setAnimationSpeed(0.05F);
                        if (getAttackingArm())
                        {
                            animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(-90, -15, 0)));
                        } else
                        {
                            animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(-90, 15, 0)));
                        }
                    } else if (getShootingTimer() > 0.1F)
                    {
                        setAnimationSpeed(0.1F);
                        if (getAttackingArm())
                        {
                            animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(-145, 30, 0)));
                        } else
                        {
                            animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(-145, -30, 0)));
                        }
                    } else if (getExplosiveFireChargeTimer() > 0.5F)
                    {
                        setAnimationSpeed(0.05F);
                        animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(-90, -15, 0)));
                        animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(-90, 15, 0)));
                    } else if (getExplosiveFireChargeTimer() > 0.1F)
                    {
                        setAnimationSpeed(0.1F);
                        animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(-145, 30, 0)));
                        animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(-145, -30, 0)));
                    } else
                    {
                        setAnimationSpeed(0.1F);
                        animation.registerRotationPoints(HEAD_ROTATION, new PercentageRotation(getHeadRotation(), new Rotations(0, 0, 0)));
                        animation.registerRotationPoints(BODY_ROTATION, new PercentageRotation(getBodyRotation(), new Rotations(0, 0, 0)));
                        animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(0, 0, 0)));
                        animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(0, 0, 0)));

                        animation.registerRotationPoints(RIGHT_LEG_ROTATION, new PercentageRotation(getRightLegRotation(), new Rotations(0, 0, 0)));
                        animation.registerRotationPoints(LEFT_LEG_ROTATION, new PercentageRotation(getLeftLegRotation(), new Rotations(0, 0, 0)));
                    }
                }
                break;

                case 1:
                case 2:
                {
                    if (getSpiritBombTimer() > 0.5F || getFireSpiritTimer() > 0.5F)
                    {
                        setAnimationSpeed(0.05F);
                        animation.registerRotationPoints(HEAD_ROTATION, new PercentageRotation(getHeadRotation(), new Rotations(-42.5F, 0, 0)));
                        animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(-145, 42.5F, 12.5F)));
                        animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(-145, -35, 12.5F)));
                    } else if (getSpiritBombTimer() > 0.1F || getFireSpiritTimer() > 0.1F)
                    {
                        setAnimationSpeed(0.2F);
                        animation.registerRotationPoints(HEAD_ROTATION, new PercentageRotation(getHeadRotation(), new Rotations(0, 0, 0)));
                        animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(-90, -15, 0)));
                        animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(-90, 15, 0)));
                    } else
                    {
                        setAnimationSpeed(0.1F);
                        animation.registerRotationPoints(HEAD_ROTATION, new PercentageRotation(getHeadRotation(), new Rotations(0, 0, 0)));
                        animation.registerRotationPoints(BODY_ROTATION, new PercentageRotation(getBodyRotation(), new Rotations(0, 0, 0)));
                        animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(0, 0, 0)));
                        animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(0, 0, 0)));

                        animation.registerRotationPoints(RIGHT_LEG_ROTATION, new PercentageRotation(getRightLegRotation(), new Rotations(0, 0, 0)));
                        animation.registerRotationPoints(LEFT_LEG_ROTATION, new PercentageRotation(getLeftLegRotation(), new Rotations(0, 0, 0)));
                    }
                }
                break;
            }
        }
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource p_148704_)
    {
        return false;
    }

    @Override
    public boolean hurt(DamageSource source, float amount)
    {
        if (!net.minecraftforge.common.ForgeHooks.onLivingAttack(this, source, amount)) return false;
        if (this.isInvulnerableTo(source))
        {
            return false;
        } else if (this.level.isClientSide)
        {
            return false;
        } else if (this.getHealth() <= 0.0F)
        {
            return false;
        } else if (source.isFire() && this.hasEffect(MobEffects.FIRE_RESISTANCE))
        {
            return false;
        } else
        {
            if (source.getEntity() instanceof Blaze)
            {
                return false;
            } else if (source.msgId.equals(DamageSource.ON_FIRE.msgId) || source.msgId.equals(DamageSource.IN_FIRE.msgId) || source.isExplosion() || source.getEntity() instanceof PyromancerEntity)
            {
                return false;
            }

            return super.hurt(source, amount);
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor)
    {
        double d0 = this.distanceToSqr(target);
        float f = Mth.sqrt(Mth.sqrt((float) d0)) * 0.5F;
        double d1 = target.getX() - this.getX();
        double d2 = target.getY(0.5D) - this.getY(0.5D);
        double d3 = target.getZ() - this.getZ();

        SmallFireball smallfireballentity = new SmallFireball(this.level, this, d1 + this.getRandom().nextGaussian() * (double) f, d2, d3 + this.getRandom().nextGaussian() * (double) f);
        smallfireballentity.setPos(smallfireballentity.getX(), this.getY(0.5D) + 0.5D, smallfireballentity.getZ());
        this.level.addFreshEntity(smallfireballentity);
        this.playSound(SoundEvents.BLAZE_SHOOT, 1.0F, 0.5F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
    }

    public void attackEntityWithExplosiveRangedAttack(LivingEntity target, float distanceFactor)
    {
        double d0 = this.distanceToSqr(target);
        float f = Mth.sqrt(Mth.sqrt((float) d0)) * 0.5F;
        double d1 = target.getX() - this.getX();
        double d2 = target.getY(0.5D) - this.getY(0.5D);
        double d3 = target.getZ() - this.getZ();

        LargeFireball smallfireballentity = new LargeFireball(this.level, this, d1 + this.getRandom().nextGaussian() * (double) f, d2, d3 + this.getRandom().nextGaussian() * (double) f, 1);
        smallfireballentity.setPos(smallfireballentity.getX(), this.getY(0.5D) + 0.5D, smallfireballentity.getZ());
        this.level.addFreshEntity(smallfireballentity);
        this.playSound(SoundEvents.BLAZE_SHOOT, 1.0F, 0.2F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
    }

    class MoveHelperController extends MoveControl
    {
        public MoveHelperController(PyromancerEntity vex)
        {
            super(vex);
        }

        @Override
        public void tick()
        {
            if (this.operation == MoveControl.Operation.MOVE_TO)
            {
                Vec3 vec3d = new Vec3(this.getWantedX() - PyromancerEntity.this.getX(), this.getWantedY() - PyromancerEntity.this.getY(), this.getWantedZ() - PyromancerEntity.this.getZ());
                double d0 = vec3d.length();
                if (d0 < PyromancerEntity.this.getBoundingBox().getSize())
                {
                    this.operation = MoveControl.Operation.WAIT;
                    PyromancerEntity.this.setDeltaMovement(PyromancerEntity.this.getDeltaMovement().scale(0.5D));
                } else
                {
                    PyromancerEntity.this.setDeltaMovement(PyromancerEntity.this.getDeltaMovement().add(vec3d.scale(this.speedModifier * 0.05D / d0)));
                    if (PyromancerEntity.this.getTarget() == null)
                    {
                        Vec3 vec3d1 = PyromancerEntity.this.getDeltaMovement();
                        PyromancerEntity.this.setYRot(-((float) Mth.atan2(vec3d1.x, vec3d1.z)) * (180F / (float) Math.PI));
                    } else
                    {
                        double d2 = PyromancerEntity.this.getTarget().getX() - PyromancerEntity.this.getX();
                        double d1 = PyromancerEntity.this.getTarget().getZ() - PyromancerEntity.this.getZ();
                        PyromancerEntity.this.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
                    }
                    PyromancerEntity.this.yBodyRot = PyromancerEntity.this.getYRot();
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

        @Override
        public boolean canUse()
        {
            return !PyromancerEntity.this.getMoveControl().hasWanted() && PyromancerEntity.this.isFlying() && PyromancerEntity.this.getTarget() != null && PyromancerEntity.this.getTarget().isAlive();
        }

        @Override
        public boolean canContinueToUse()
        {
            return PyromancerEntity.this.getTarget() != null && PyromancerEntity.this.getTarget().isAlive();
        }

        @Override
        public void stop()
        {
            PyromancerEntity.this.setFlying(false);
        }

        @Override
        public void tick()
        {
            BlockPos blockpos = PyromancerEntity.this.getTarget().blockPosition();
            if (blockpos == null)
            {
                blockpos = PyromancerEntity.this.blockPosition();
            }

            for (int i = 0; i < 3; ++i)
            {
                BlockPos blockpos1 = blockpos.offset(PyromancerEntity.this.random.nextInt(15) - 7, PyromancerEntity.this.random.nextInt(11) - 5, PyromancerEntity.this.random.nextInt(15) - 7);
                if (PyromancerEntity.this.level.isEmptyBlock(blockpos1))
                {
                    PyromancerEntity.this.moveControl.setWantedPosition((double) blockpos1.getX() + 0.5D, (double) blockpos1.getY() + 0.5D, (double) blockpos1.getZ() + 0.5D, 0.25D);
                    if (PyromancerEntity.this.getTarget() == null)
                    {
                        PyromancerEntity.this.getLookControl().setLookAt((double) blockpos1.getX() + 0.5D, (double) blockpos1.getY() + 0.5D, (double) blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
                    }
                    break;
                }
            }

        }
    }

    private class PyroRangedAttackGoal extends Goal
    {
        private final PyromancerEntity entityHost;
        private final RangedAttackMob rangedAttackEntityHost;
        private final double entityMoveSpeed;
        private final float attackRadius;
        private final float maxAttackDistance;
        private LivingEntity attackTarget;
        private int seeTime;
        private boolean hasShot;

        public PyroRangedAttackGoal(RangedAttackMob attacker, double movespeed, float maxAttackDistanceIn)
        {
            entityHost = (PyromancerEntity) attacker;
            rangedAttackEntityHost = attacker;
            this.entityMoveSpeed = movespeed;
            this.attackRadius = maxAttackDistanceIn;
            this.maxAttackDistance = maxAttackDistanceIn * maxAttackDistanceIn;
        }

        @Override
        public void start()
        {
            setAttackingArm(entityHost.level.random.nextBoolean());
            setShootingTimer(1.0F);
            hasShot = false;
        }

        @Override
        public boolean canUse()
        {
            LivingEntity livingentity = this.entityHost.getTarget();
            if (livingentity != null && livingentity.isAlive())
            {
                this.attackTarget = livingentity;
                return getState() == 0 && getExplosiveFireChargeTimer() == 0;
            }

            return false;
        }

        @Override
        public boolean canContinueToUse()
        {
            return getState() == 0 && getShootingTimer() > 0 && this.canUse();
        }

        @Override
        public void stop()
        {
            super.stop();
            this.attackTarget = null;
            setShootingTimer(0.0F);
        }

        @Override
        public void tick()
        {
            double d2 = entityHost.getTarget().getX() - entityHost.getX();
            double d1 = entityHost.getTarget().getZ() - entityHost.getZ();
            entityHost.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
            entityHost.yBodyRot = entityHost.getYRot();

            double d0 = this.entityHost.distanceToSqr(this.attackTarget.getX(), this.attackTarget.getY(), this.attackTarget.getZ());
            boolean flag = this.entityHost.getSensing().hasLineOfSight(this.attackTarget);
            if (flag)
            {
                ++this.seeTime;
            } else
            {
                this.seeTime = 0;
            }

            if (!(d0 > (double) this.maxAttackDistance) && this.seeTime >= 5)
            {
                this.entityHost.getNavigation().stop();
            } else
            {
                this.entityHost.getNavigation().moveTo(this.attackTarget, this.entityMoveSpeed);
            }

            if (entityHost.tickCount % 2 == 0)
            {
                if (getShootingTimer() > 0)
                {
                    setShootingTimer(getShootingTimer() - 0.1F);
                }
            }

            if (getShootingTimer() <= 0.5F && !hasShot)
            {
                Vec3 _vec = new Vec3(entityHost.getX() + entityHost.getForward().x(), entityHost.getY(0.5D), entityHost.getZ() + entityHost.getForward().z());
                SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(_vec, new Vec3(0, 0.1f, 0), 2, 10, getBbWidth() * 0.35F);
                MWAWPacketHandler.packetHandler.sendToDimension(entityHost.level.dimension(), spawnParticleMessage);

                float f = Mth.sqrt((float) d0) / this.attackRadius;
                float lvt_5_1_ = Mth.clamp(f, 0.1F, 1.0F);
                this.rangedAttackEntityHost.performRangedAttack(this.attackTarget, lvt_5_1_);
                hasShot = true;
            }
        }
    }

    private class PyroExplosiveRangedAttackGoal extends Goal
    {
        private final PyromancerEntity entityHost;
        private final double entityMoveSpeed;
        private final float attackRadius;
        private final float maxAttackDistance;
        private LivingEntity attackTarget;
        private int seeTime;
        private boolean hasShot;

        public PyroExplosiveRangedAttackGoal(RangedAttackMob attacker, double movespeed, float maxAttackDistanceIn)
        {
            entityHost = (PyromancerEntity) attacker;
            this.entityMoveSpeed = movespeed;
            this.attackRadius = maxAttackDistanceIn;
            this.maxAttackDistance = maxAttackDistanceIn * maxAttackDistanceIn;
        }

        @Override
        public void start()
        {
            setExplosiveFireChargeTimer(1.0F);
            hasShot = false;
        }

        @Override
        public boolean canUse()
        {
            LivingEntity livingentity = this.entityHost.getTarget();
            if (livingentity != null && livingentity.isAlive())
            {
                this.attackTarget = livingentity;
                return getState() == 0 && entityHost.random.nextInt(100) < 20 && getShootingTimer() == 0;
            }

            return false;
        }

        @Override
        public boolean canContinueToUse()
        {
            return getState() == 0 && getExplosiveFireChargeTimer() > 0 && this.entityHost.getTarget() != null && this.entityHost.getTarget().isAlive();
        }

        @Override
        public void stop()
        {
            super.stop();
            this.attackTarget = null;
            setExplosiveFireChargeTimer(0.0F);
        }

        @Override
        public void tick()
        {
            double d2 = entityHost.getTarget().getX() - entityHost.getX();
            double d1 = entityHost.getTarget().getZ() - entityHost.getZ();
            entityHost.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
            entityHost.yBodyRot = entityHost.getYRot();

            double d0 = this.entityHost.distanceToSqr(this.attackTarget.getX(), this.attackTarget.getY(), this.attackTarget.getZ());
            boolean flag = this.entityHost.getSensing().hasLineOfSight(this.attackTarget);
            if (flag)
            {
                ++this.seeTime;
            } else
            {
                this.seeTime = 0;
            }

            if (!(d0 > (double) this.maxAttackDistance) && this.seeTime >= 5)
            {
                this.entityHost.getNavigation().stop();
            } else
            {
                this.entityHost.getNavigation().moveTo(this.attackTarget, this.entityMoveSpeed);
            }

            if (entityHost.tickCount % 5 == 0)
            {
                if (getExplosiveFireChargeTimer() > 0)
                {
                    setExplosiveFireChargeTimer(getExplosiveFireChargeTimer() - 0.1F);
                }
            }

            if (getExplosiveFireChargeTimer() > 0.5F)
            {
                Vec3 _vec = new Vec3(entityHost.getX() + entityHost.getForward().x(), entityHost.getY(0.5D), entityHost.getZ() + entityHost.getForward().z());
                SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(_vec, new Vec3(0, 0.1f, 0), 2, 10, getBbWidth() * 0.35F);
                MWAWPacketHandler.packetHandler.sendToDimension(entityHost.level.dimension(), spawnParticleMessage);
            } else if (getExplosiveFireChargeTimer() <= 0.5F && !hasShot)
            {
                float f = Mth.sqrt((float) d0) / this.attackRadius;
                float lvt_5_1_ = Mth.clamp(f, 0.1F, 1.0F);
                entityHost.attackEntityWithExplosiveRangedAttack(this.attackTarget, lvt_5_1_);
                hasShot = true;
            }
        }
    }

    private class SpiritBombGoal extends Goal
    {
        private final PyromancerEntity entityHost;
        private SpiritBombEntity bomb;
        private int coolDown;
        private LivingEntity attackTarget;
        private boolean hasShot;
        private float startHealth;

        public SpiritBombGoal(PyromancerEntity attacker)
        {
            entityHost = attacker;
            coolDown = 400;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Flag.LOOK));
        }

        @Override
        public void start()
        {
            setSpiritBombTimer(10.0F);
            hasShot = false;
            setState(1);
            entityHost.getNavigation().stop();
            bomb = new SpiritBombEntity(entityHost.level, entityHost, null);
            bomb.setPos(entityHost.getX(), entityHost.getY(0.5D) + 3.5D, entityHost.getZ());
            entityHost.level.addFreshEntity(bomb);
            startHealth = entityHost.getHealth();
        }

        @Override
        public boolean canUse()
        {
            if (getHealth() > 50)
            {
                return false;
            }

            LivingEntity livingentity = this.entityHost.getTarget();
            if (livingentity != null && livingentity.isAlive())
            {
                if (coolDown > 0)
                {
                    if (entityHost.tickCount % 2 == 0)
                    {
                        --coolDown;
                    }
                    return false;
                }

                this.attackTarget = livingentity;
                return getState() == 0 && entityHost.random.nextInt(100) < 20 && attackTarget != null;
            }

            return false;
        }

        @Override
        public boolean canContinueToUse()
        {
            return getState() == 1 && getSpiritBombTimer() > 0;
        }

        @Override
        public void stop()
        {
            super.stop();
            this.attackTarget = null;
            setState(0);
            coolDown = 400;
        }

        @Override
        public void tick()
        {
            if (entityHost.getTarget() != null && entityHost.getTarget().isAlive())
            {
                double d2 = entityHost.getTarget().getX() - entityHost.getX();
                double d1 = entityHost.getTarget().getZ() - entityHost.getZ();
                entityHost.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
                entityHost.yBodyRot = entityHost.getYRot();
            }

            entityHost.setDeltaMovement(0.0D, 0.0D, 0.0D);

            if (getSpiritBombTimer() > 0.5F)
            {
                bomb.increaseCharge();
                bomb.setPos(entityHost.getX(), entityHost.getY(0.5D) + 3.5D, entityHost.getZ());
                bomb.xPower = 0;
                bomb.yPower = 0;
                bomb.zPower = 0;
                bomb.setDeltaMovement(0.0D, 0.0D, 0.0D);

                if (bomb.getChargeTimer() >= 100 || (entityHost.getHealth() / startHealth) * 100F < 80)
                {
                    setSpiritBombTimer(0.5F);
                }
            } else if (getSpiritBombTimer() <= 0.5F)
            {
                if (entityHost.tickCount % 3 == 0)
                {
                    if (getSpiritBombTimer() > 0)
                    {
                        setSpiritBombTimer(getSpiritBombTimer() - 0.1F);
                    }
                }

                if (!hasShot)
                {
                    bomb.setHaveShot(true);
                    bomb.setTarget(getTarget());
                    hasShot = true;
                    entityHost.playSound(SoundEvents.BLAZE_SHOOT, 1.0F, 0.5F / (entityHost.getRandom().nextFloat() * 0.4F + 0.8F));
                }
            }
        }
    }

    private class SpiritSummoningGoal extends Goal
    {
        private final PyromancerEntity entityHost;
        private int coolDown;
        private LivingEntity attackTarget;

        public SpiritSummoningGoal(PyromancerEntity attacker)
        {
            entityHost = attacker;
            coolDown = 200;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Flag.LOOK));
        }

        @Override
        public void start()
        {
            setFireSpiritTimer(1.0F);
            setState(2);
            entityHost.getNavigation().stop();

            ArrayList<FireSpiritEntity> temp = new ArrayList<>();
            for (FireSpiritEntity entity : spiritList)
            {
                if (entity.isAlive())
                {
                    temp.add(entity);
                }
            }

            spiritList = temp;
        }

        @Override
        public boolean canUse()
        {
            LivingEntity livingentity = this.entityHost.getTarget();
            if (livingentity != null && livingentity.isAlive())
            {
                if (coolDown > 0)
                {
                    if (entityHost.tickCount % 2 == 0)
                    {
                        --coolDown;
                    }
                    return false;
                }

                this.attackTarget = livingentity;
                return getState() == 0 && entityHost.random.nextInt(100) < 20 && attackTarget != null;
            }

            return false;
        }

        @Override
        public boolean canContinueToUse()
        {
            return getState() == 2 && getFireSpiritTimer() > 0 && entityHost.getTarget() != null && entityHost.getTarget().isAlive();
        }

        @Override
        public void stop()
        {
            super.stop();
            this.attackTarget = null;
            setState(0);
            coolDown = 200;
        }

        @Override
        public void tick()
        {
            if (entityHost.getTarget() != null && entityHost.getTarget().isAlive())
            {
                double d2 = entityHost.getTarget().getX() - entityHost.getX();
                double d1 = entityHost.getTarget().getZ() - entityHost.getZ();
                entityHost.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
                entityHost.yBodyRot = entityHost.getYRot();
            }

            entityHost.setDeltaMovement(0.0D, 0.0D, 0.0D);

            if (spiritList.size() < 3)
            {
                if (entityHost.tickCount % 10 == 0)
                {
                    FireSpiritEntity entity = EntityRegistryHandler.FIRE_SPIRIT_ENTITY.get().create(entityHost.level);
                    entity.setOwner(entityHost);
                    spiritList.add(entity);

                    entity.setPos(entityHost.getX(), entityHost.getY(0.5D) + 3.5D, entityHost.getZ());
                    entityHost.level.addFreshEntity(entity);

                    entityHost.playSound(SoundEvents.BLAZE_SHOOT, 1.0F, 0.5F / (entityHost.getRandom().nextFloat() * 0.4F + 0.8F));
                }
            } else
            {
                setFireSpiritTimer(0.0F);
            }
        }
    }

    private class PyroWaterAvoidingRandomWalkingGoal extends WaterAvoidingRandomStrollGoal
    {
        PyromancerEntity creature;

        public PyroWaterAvoidingRandomWalkingGoal(PyromancerEntity creatureIn, double speedIn)
        {
            super(creatureIn, speedIn);
            creature = creatureIn;
        }

        @Override
        public boolean canUse()
        {
            return super.canUse() && !creature.isFlying();
        }
    }
}
