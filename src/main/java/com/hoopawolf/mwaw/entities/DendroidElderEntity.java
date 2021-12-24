package com.hoopawolf.mwaw.entities;

import com.hoopawolf.mwaw.client.animation.AnimationHelper;
import com.hoopawolf.mwaw.client.animation.PercentageRotation;
import com.hoopawolf.mwaw.entities.ai.controller.MWAWMovementController;
import com.hoopawolf.mwaw.entities.ai.navigation.MWAWPathNavigateGround;
import com.hoopawolf.mwaw.entities.helper.EntityHelper;
import com.hoopawolf.mwaw.entities.helper.MathFuncHelper;
import com.hoopawolf.mwaw.network.MWAWPacketHandler;
import com.hoopawolf.mwaw.network.packets.client.SpawnParticleMessage;
import com.hoopawolf.mwaw.network.packets.client.SpawnSuckingParticleMessage;
import com.hoopawolf.mwaw.util.ItemBlockRegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Rotations;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.IPlantable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class DendroidElderEntity extends PathfinderMob
{
    private static final EntityDataAccessor<Boolean> ATTACKING_ARM = SynchedEntityData.defineId(DendroidElderEntity.class, EntityDataSerializers.BOOLEAN); //true - left false - right
    private static final EntityDataAccessor<Boolean> DEFEND_MODE = SynchedEntityData.defineId(DendroidElderEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(DendroidElderEntity.class, EntityDataSerializers.INT); //STATE: NORMAL MELEE, RECOVERY, SLAM ATTACk
    private static final EntityDataAccessor<Float> ABSORB_TIMER = SynchedEntityData.defineId(DendroidElderEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> SLAM_TIMER = SynchedEntityData.defineId(DendroidElderEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> ATTACK_TIMER = SynchedEntityData.defineId(DendroidElderEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> ANIMATION_SPEED = SynchedEntityData.defineId(DendroidElderEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Rotations> HEAD_ROTATION = SynchedEntityData.defineId(DendroidElderEntity.class, EntityDataSerializers.ROTATIONS);
    private static final EntityDataAccessor<Rotations> BODY_ROTATION = SynchedEntityData.defineId(DendroidElderEntity.class, EntityDataSerializers.ROTATIONS);
    private static final EntityDataAccessor<Rotations> RIGHT_ARM_ROTATION = SynchedEntityData.defineId(DendroidElderEntity.class, EntityDataSerializers.ROTATIONS);
    private static final EntityDataAccessor<Rotations> RIGHT_JOINT_ROTATION = SynchedEntityData.defineId(DendroidElderEntity.class, EntityDataSerializers.ROTATIONS);
    private static final EntityDataAccessor<Rotations> LEFT_ARM_ROTATION = SynchedEntityData.defineId(DendroidElderEntity.class, EntityDataSerializers.ROTATIONS);
    private static final EntityDataAccessor<Rotations> LEFT_JOINT_ROTATION = SynchedEntityData.defineId(DendroidElderEntity.class, EntityDataSerializers.ROTATIONS);
    private static final EntityDataAccessor<Rotations> RIGHT_LEG_ROTATION = SynchedEntityData.defineId(DendroidElderEntity.class, EntityDataSerializers.ROTATIONS);
    private static final EntityDataAccessor<Rotations> RIGHT_FOOT_ROTATION = SynchedEntityData.defineId(DendroidElderEntity.class, EntityDataSerializers.ROTATIONS);
    private static final EntityDataAccessor<Rotations> LEFT_LEG_ROTATION = SynchedEntityData.defineId(DendroidElderEntity.class, EntityDataSerializers.ROTATIONS);
    private static final EntityDataAccessor<Rotations> LEFT_FOOT_ROTATION = SynchedEntityData.defineId(DendroidElderEntity.class, EntityDataSerializers.ROTATIONS);
    public final AnimationHelper animation = new AnimationHelper();
    private final float absorbTimerMax = 250, slamTimerMax = 20, attackTimerMax = 10;
    //STATE: NORMAL MELEE, RECOVERY, SLAM ATTACk
    private float defendTimer = 0;

    public DendroidElderEntity(EntityType<? extends DendroidElderEntity> type, Level worldIn)
    {
        super(type, worldIn);
        this.maxUpStep = 1.0F;

        animation.defineSynchedData(HEAD_ROTATION);
        animation.defineSynchedData(BODY_ROTATION);
        animation.defineSynchedData(RIGHT_ARM_ROTATION);
        animation.defineSynchedData(RIGHT_JOINT_ROTATION);
        animation.defineSynchedData(LEFT_ARM_ROTATION);
        animation.defineSynchedData(LEFT_JOINT_ROTATION);
        animation.defineSynchedData(LEFT_LEG_ROTATION);
        animation.defineSynchedData(LEFT_FOOT_ROTATION);
        animation.defineSynchedData(RIGHT_LEG_ROTATION);
        animation.defineSynchedData(RIGHT_FOOT_ROTATION);

        this.moveControl = new MWAWMovementController(this, 30);
    }

    public static AttributeSupplier.Builder func_234321_m_()
    {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 140.0D).add(Attributes.ATTACK_DAMAGE, 7.0D)
                .add(Attributes.FOLLOW_RANGE, 12.0D).add(Attributes.MOVEMENT_SPEED, 0.2D);
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(ATTACKING_ARM, false);
        this.entityData.define(DEFEND_MODE, false);
        this.entityData.define(STATE, 0);
        this.entityData.define(ABSORB_TIMER, 0F);
        this.entityData.define(SLAM_TIMER, 0F);
        this.entityData.define(ATTACK_TIMER, 0F);
        this.entityData.define(ANIMATION_SPEED, 0F);

        this.entityData.define(HEAD_ROTATION, new Rotations(0, 0, 0));
        this.entityData.define(BODY_ROTATION, new Rotations(0, 0, 0));
        this.entityData.define(RIGHT_ARM_ROTATION, new Rotations(0, 0, 0));
        this.entityData.define(LEFT_ARM_ROTATION, new Rotations(0, 0, 0));
        this.entityData.define(RIGHT_JOINT_ROTATION, new Rotations(0, 0, 0));
        this.entityData.define(LEFT_JOINT_ROTATION, new Rotations(0, 0, 0));
        this.entityData.define(LEFT_LEG_ROTATION, new Rotations(0, 0, 0));
        this.entityData.define(RIGHT_LEG_ROTATION, new Rotations(0, 0, 0));
        this.entityData.define(LEFT_FOOT_ROTATION, new Rotations(0, 0, 0));
        this.entityData.define(RIGHT_FOOT_ROTATION, new Rotations(0, 0, 0));
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level world)
    {
        return new MWAWPathNavigateGround(this, world);
    }

    public boolean getAttackingArm()
    {
        return this.entityData.get(ATTACKING_ARM);
    }

    public void setAttackingArm(boolean isLeft)
    {
        this.entityData.set(ATTACKING_ARM, isLeft);
    }

    public float getAnimationSpeed()
    {
        return this.entityData.get(ANIMATION_SPEED);
    }

    public void setAnimationSpeed(float speedIn)
    {
        this.entityData.set(ANIMATION_SPEED, speedIn);
    }

    public boolean isDefensiveMode()
    {
        return this.entityData.get(DEFEND_MODE);
    }

    public void setDefendMode(boolean isDefending)
    {
        this.entityData.set(DEFEND_MODE, isDefending);
    }

    public int getState()
    {
        return this.entityData.get(STATE);
    }

    public void setState(int state)
    {
        this.entityData.set(STATE, state);
    }

    public float getAbsorbTimer()
    {
        return this.entityData.get(ABSORB_TIMER);
    }

    public void setAbsorbTimer(float timer)
    {
        this.entityData.set(ABSORB_TIMER, timer);
    }

    public float getSlamTimer()
    {
        return this.entityData.get(SLAM_TIMER);
    }

    public void setSlamTimer(float timer)
    {
        this.entityData.set(SLAM_TIMER, timer);
    }

    public float getAttackTimer()
    {
        return this.entityData.get(ATTACK_TIMER);
    }

    public void setAttackTimer(float timer)
    {
        this.entityData.set(ATTACK_TIMER, timer);
    }

    public float getAbsorbTimerMax()
    {
        return absorbTimerMax;
    }

    public float getSlamTimerMax()
    {
        return slamTimerMax;
    }

    public Rotations getHeadRotation()
    {
        return this.entityData.get(HEAD_ROTATION);
    }

    public Rotations getBodyRotation()
    {
        return this.entityData.get(BODY_ROTATION);
    }

    public Rotations getRightArmRotation()
    {
        return this.entityData.get(RIGHT_ARM_ROTATION);
    }

    public Rotations getRightJointRotation()
    {
        return this.entityData.get(RIGHT_JOINT_ROTATION);
    }

    public Rotations getLeftArmRotation()
    {
        return this.entityData.get(LEFT_ARM_ROTATION);
    }

    public Rotations getLeftJointRotation()
    {
        return this.entityData.get(LEFT_JOINT_ROTATION);
    }

    public Rotations getRightLegRotation()
    {
        return this.entityData.get(RIGHT_LEG_ROTATION);
    }

    public Rotations getRightFootRotation()
    {
        return this.entityData.get(RIGHT_FOOT_ROTATION);
    }

    public Rotations getLeftLegRotation()
    {
        return this.entityData.get(LEFT_LEG_ROTATION);
    }

    public Rotations getLeftFootRotation()
    {
        return this.entityData.get(LEFT_FOOT_ROTATION);
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new ElderRecoveryGoal(this));
        this.goalSelector.addGoal(0, new ElderGroundSlamGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 4.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, PathfinderMob.class, 4.0F));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PathfinderMob.class, 10, true, false, (p_213621_0_) ->
        {
            return !(p_213621_0_ instanceof DendroidEntity) && !(p_213621_0_ instanceof DendroidElderEntity);
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
                    if (defendTimer > 0)
                    {
                        if (tickCount % 2 == 0)
                        {
                            --defendTimer;
                        }
                    } else if (isDefensiveMode())
                    {
                        setDefendMode(false);
                    }

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

                    if (isDefensiveMode())
                    {
                        animation.registerRotationPoints(RIGHT_JOINT_ROTATION, new PercentageRotation(getRightJointRotation(), new Rotations(-25, -45, -75)));
                        animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(-45, 0, 0)));
                    }

                    if (getAttackTimer() > attackTimerMax * 0.3F)
                    {
                        if (getAttackingArm())
                        {
                            animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(-95, -35, 0)));
                            animation.registerRotationPoints(LEFT_JOINT_ROTATION, new PercentageRotation(getLeftJointRotation(), new Rotations(-55, 0, 0)));
                            animation.registerRotationPoints(BODY_ROTATION, new PercentageRotation(getBodyRotation(), new Rotations(-15, -11, 0)));
                        } else
                        {
                            animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(-95, 35, 0)));
                            animation.registerRotationPoints(RIGHT_JOINT_ROTATION, new PercentageRotation(getRightJointRotation(), new Rotations(-55, 0, 0)));
                            animation.registerRotationPoints(BODY_ROTATION, new PercentageRotation(getBodyRotation(), new Rotations(-15, 11, 0)));
                        }
                    } else if (getAttackTimer() > 0)
                    {
                        if (getAttackingArm())
                        {
                            animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(-55, 25, 0)));
                            animation.registerRotationPoints(LEFT_JOINT_ROTATION, new PercentageRotation(getLeftJointRotation(), new Rotations(0, 0, 20)));
                            animation.registerRotationPoints(BODY_ROTATION, new PercentageRotation(getBodyRotation(), new Rotations(10, 22, -3)));
                        } else
                        {
                            animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(-55, -25, 0)));
                            animation.registerRotationPoints(RIGHT_JOINT_ROTATION, new PercentageRotation(getRightJointRotation(), new Rotations(0, 0, -20)));
                            animation.registerRotationPoints(BODY_ROTATION, new PercentageRotation(getBodyRotation(), new Rotations(10, -22, 3)));
                        }
                    } else
                    {
                        if (!isDefensiveMode())
                        {
                            animation.registerRotationPoints(RIGHT_JOINT_ROTATION, new PercentageRotation(getRightJointRotation(), new Rotations(0, 0, 0)));
                            animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(0, 0, 0)));
                        }

                        animation.registerRotationPoints(HEAD_ROTATION, new PercentageRotation(getHeadRotation(), new Rotations(0, 0, 0)));
                        animation.registerRotationPoints(BODY_ROTATION, new PercentageRotation(getBodyRotation(), new Rotations(0, 0, 0)));

                        animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(0, 0, 0)));
                        animation.registerRotationPoints(LEFT_JOINT_ROTATION, new PercentageRotation(getLeftJointRotation(), new Rotations(0, 0, 0)));

                        animation.registerRotationPoints(RIGHT_LEG_ROTATION, new PercentageRotation(getRightLegRotation(), new Rotations(0, 0, 0)));
                        animation.registerRotationPoints(LEFT_LEG_ROTATION, new PercentageRotation(getLeftLegRotation(), new Rotations(0, 0, 0)));
                        animation.registerRotationPoints(RIGHT_FOOT_ROTATION, new PercentageRotation(getRightFootRotation(), new Rotations(0, 0, 0)));
                        animation.registerRotationPoints(LEFT_FOOT_ROTATION, new PercentageRotation(getLeftFootRotation(), new Rotations(0, 0, 0)));
                    }
                }
                break;

                case 1:
                {
                    if (getAbsorbTimer() < absorbTimerMax * 0.90F)
                    {
                        setAnimationSpeed(0.05F);

                        animation.registerRotationPoints(HEAD_ROTATION, new PercentageRotation(getHeadRotation(), new Rotations(45, 0, 0)));
                        animation.registerRotationPoints(BODY_ROTATION, new PercentageRotation(getBodyRotation(), new Rotations(0, 0, 0)));
                        animation.registerRotationPoints(RIGHT_JOINT_ROTATION, new PercentageRotation(getRightJointRotation(), new Rotations(-25, -45, -75)));
                        animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(-45, 0, 0)));

                        animation.registerRotationPoints(LEFT_JOINT_ROTATION, new PercentageRotation(getLeftJointRotation(), new Rotations(-25, 45, 75)));
                        animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(-45, 0, 0)));

                        animation.registerRotationPoints(RIGHT_LEG_ROTATION, new PercentageRotation(getRightLegRotation(), new Rotations(-65, 0, 0)));
                        animation.registerRotationPoints(LEFT_LEG_ROTATION, new PercentageRotation(getLeftLegRotation(), new Rotations(-65, 0, 0)));

                        animation.registerRotationPoints(RIGHT_FOOT_ROTATION, new PercentageRotation(getRightFootRotation(), new Rotations(95, 0, 0)));
                        animation.registerRotationPoints(LEFT_FOOT_ROTATION, new PercentageRotation(getLeftFootRotation(), new Rotations(95, 0, 0)));
                    } else if (getAbsorbTimer() < absorbTimerMax)
                    {
                        setAnimationSpeed(0.07F);

                        animation.registerRotationPoints(HEAD_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(-45, 0, 0)));
                        animation.registerRotationPoints(BODY_ROTATION, new PercentageRotation(getBodyRotation(), new Rotations(0, 0, 0)));
                        animation.registerRotationPoints(RIGHT_JOINT_ROTATION, new PercentageRotation(getRightJointRotation(), new Rotations(60, 0, 0)));
                        animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(0, -105, 60)));

                        animation.registerRotationPoints(LEFT_JOINT_ROTATION, new PercentageRotation(getLeftJointRotation(), new Rotations(60, 0, 0)));
                        animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(0, 105, -60)));

                        animation.registerRotationPoints(RIGHT_LEG_ROTATION, new PercentageRotation(getRightLegRotation(), new Rotations(0, -25, 30)));
                        animation.registerRotationPoints(LEFT_LEG_ROTATION, new PercentageRotation(getLeftLegRotation(), new Rotations(0, 25, -30)));

                        animation.registerRotationPoints(RIGHT_FOOT_ROTATION, new PercentageRotation(getRightFootRotation(), new Rotations(25, 0, 0)));
                        animation.registerRotationPoints(LEFT_FOOT_ROTATION, new PercentageRotation(getLeftFootRotation(), new Rotations(25, 0, 0)));
                    }
                }
                break;

                case 2:
                {
                    if (getSlamTimer() < slamTimerMax * 0.8F)
                    {
                        setAnimationSpeed(0.05F);

                        animation.registerRotationPoints(HEAD_ROTATION, new PercentageRotation(getHeadRotation(), new Rotations(-40, 0, 0)));
                        animation.registerRotationPoints(BODY_ROTATION, new PercentageRotation(getBodyRotation(), new Rotations(-25, 0, 0)));

                        animation.registerRotationPoints(RIGHT_JOINT_ROTATION, new PercentageRotation(getRightJointRotation(), new Rotations(-75, 0, 0)));
                        animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(-140, 0, 30)));

                        animation.registerRotationPoints(LEFT_JOINT_ROTATION, new PercentageRotation(getLeftJointRotation(), new Rotations(-75, 0, 0)));
                        animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(-140, 0, -30)));

                        animation.registerRotationPoints(RIGHT_LEG_ROTATION, new PercentageRotation(getRightLegRotation(), new Rotations(-15, 20, 0)));
                        animation.registerRotationPoints(LEFT_LEG_ROTATION, new PercentageRotation(getLeftLegRotation(), new Rotations(-15, -20, 0)));
                        animation.registerRotationPoints(RIGHT_FOOT_ROTATION, new PercentageRotation(getRightFootRotation(), new Rotations(15, 0, 0)));
                        animation.registerRotationPoints(LEFT_FOOT_ROTATION, new PercentageRotation(getLeftFootRotation(), new Rotations(15, 0, 0)));
                    } else if (getSlamTimer() < slamTimerMax)
                    {
                        setAnimationSpeed(0.09F);

                        animation.registerRotationPoints(HEAD_ROTATION, new PercentageRotation(getHeadRotation(), new Rotations(-55, 0, 0)));
                        animation.registerRotationPoints(BODY_ROTATION, new PercentageRotation(getBodyRotation(), new Rotations(85, 0, 0)));

                        animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(-55, -30, -5)));
                        animation.registerRotationPoints(RIGHT_JOINT_ROTATION, new PercentageRotation(getRightJointRotation(), new Rotations(-85, 0, 20)));
                        animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(-55, 30, 5)));
                        animation.registerRotationPoints(LEFT_JOINT_ROTATION, new PercentageRotation(getLeftJointRotation(), new Rotations(-85, 0, -20)));

                        animation.registerRotationPoints(RIGHT_LEG_ROTATION, new PercentageRotation(getRightLegRotation(), new Rotations(-15, 20, 0)));
                        animation.registerRotationPoints(LEFT_LEG_ROTATION, new PercentageRotation(getLeftLegRotation(), new Rotations(-15, -20, 0)));
                        animation.registerRotationPoints(RIGHT_FOOT_ROTATION, new PercentageRotation(getRightFootRotation(), new Rotations(15, 0, 0)));
                        animation.registerRotationPoints(LEFT_FOOT_ROTATION, new PercentageRotation(getLeftFootRotation(), new Rotations(15, 0, 0)));
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
            if (source.getEntity() instanceof DendroidEntity || source.getEntity() instanceof DendroidElderEntity)
                return false;

            if (source.msgId.equals(DamageSource.ON_FIRE.msgId))
            {
                this.setSecondsOnFire(100);
            } else if (source.msgId.equals(DamageSource.DROWN.msgId) || source.msgId.equals(DamageSource.CACTUS.msgId))
            {
                return false;
            } else if (source.getEntity() instanceof LivingEntity && ((LivingEntity) source.getEntity()).getMainHandItem().getItem() instanceof AxeItem)
            {
                return super.hurt(source, amount * 2.0F);
            }

            if (getState() == 0 && source.getEntity() != null && !(source.getDirectEntity() instanceof LivingEntity))
            {
                Vec3 dir = this.position().subtract(source.getEntity().position());

                if (this.isDefensiveMode())
                {
                    if (Mth.sign(this.getLookAngle().x()) != Mth.sign(dir.x()) && Mth.sign(this.getLookAngle().z()) != Mth.sign(dir.z()))
                    {
                        this.playSound(SoundEvents.ARMOR_STAND_HIT, this.getSoundVolume(), this.getVoicePitch());
                        return false;
                    }
                } else
                {
                    if (this.level.random.nextInt(100) < 40)
                    {
                        this.setDefendMode(true);
                        this.defendTimer = 100 + random.nextInt(50);
                    }
                }
            }

            if (getState() == 1)
            {
                if (source.isProjectile() || source.getDirectEntity() instanceof LivingEntity)
                {
                    Vec3 dir = this.position().subtract(source.getDirectEntity().position());

                    if (Mth.sign(this.getLookAngle().x()) != Mth.sign(dir.x()) && Mth.sign(this.getLookAngle().z()) != Mth.sign(dir.z()))
                    {
                        this.playSound(SoundEvents.ARMOR_STAND_HIT, this.getSoundVolume(), this.getVoicePitch());
                        return false;
                    }
                }
            }

            return super.hurt(source, amount);
        }
    }

    @Override
    public void kill()
    {
        int radius = 5;

        if (!this.level.isClientSide)
        {
            for (float y = -radius; y < radius; ++y)
            {
                for (float x = -radius; x < radius; ++x)
                {
                    for (float z = -radius; z < radius; ++z)
                    {
                        BlockPos blockPos = new BlockPos(x + this.getX(), y + this.getY(), z + this.getZ());

                        if (level.random.nextInt(100) < 40)
                        {
                            if (this.level.getBlockState(blockPos).getBlock() instanceof SpreadingSnowyDirtBlock)
                            {
                                if (!this.level.getBlockState(new BlockPos(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ())).canOcclude())
                                {
                                    this.level.setBlockAndUpdate(new BlockPos(blockPos.getX(), blockPos.getY() + 1, blockPos.getZ()), ItemBlockRegistryHandler.DENDROID_ROOTS_BLOCK.get().defaultBlockState());
                                }
                            }
                        }
                    }
                }
            }
            super.kill();
        }
    }

    @Override
    public boolean doHurtTarget(Entity entityIn)
    {
        this.setAttackTimer(attackTimerMax);
        setAttackingArm((isDefensiveMode() || random.nextInt(2) == 1));
        this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 1.0F, 0.1F);

        return super.doHurtTarget(entityIn);
    }

    private class ElderRecoveryGoal extends Goal
    {
        private final float radius;
        private final ArrayList<BlockPos> natureBlocks;
        DendroidElderEntity host;
        private float coolDown;

        public ElderRecoveryGoal(DendroidElderEntity creature)
        {
            host = creature;
            coolDown = 0;
            radius = 5;
            natureBlocks = new ArrayList<>();
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
            return host.getState() == 0 && host.getHealth() < host.getMaxHealth() * 0.3F && random.nextInt(100) < 40;
        }

        @Override
        public boolean canContinueToUse()
        {
            return host.getAbsorbTimer() <= host.getAbsorbTimerMax();
        }

        @Override
        public void start()
        {
            natureBlocks.clear();
            host.getNavigation().stop();
            host.setAbsorbTimer(0.0F);
            host.setState(1);

            for (float y = -radius; y < radius; ++y)
            {
                for (float x = -radius; x < radius; ++x)
                {
                    for (float z = -radius; z < radius; ++z)
                    {
                        BlockPos blockPos = new BlockPos(x + host.getX(), y + host.getY(), z + host.getZ());

                        if (host.level.getBlockState(blockPos).getBlock() instanceof IPlantable || host.level.getBlockState(blockPos).getBlock() instanceof LeavesBlock ||
                                host.level.getBlockState(blockPos).getBlock() instanceof BonemealableBlock)
                        {
                            natureBlocks.add(blockPos);
                        }
                    }
                }
            }
        }

        @Override
        public void stop()
        {
            coolDown = 1000;
            host.setState(0);
        }

        @Override
        public void tick()
        {
            if (host.getAbsorbTimer() <= host.getAbsorbTimerMax())
            {
                host.setAbsorbTimer(host.getAbsorbTimer() + 1.0F);
                host.setDeltaMovement(Vec3.ZERO);

                if (!host.level.isClientSide)
                {
                    if (host.getAbsorbTimer() < host.getAbsorbTimerMax() * 0.90F)
                    {
                        if (host.tickCount % 2 == 0)
                        {
                            if (!natureBlocks.isEmpty())
                            {
                                int randomBlock = host.level.random.nextInt(natureBlocks.size());
                                BlockPos chosenBlock = natureBlocks.remove(randomBlock);

                                if (host.level.getBlockState(chosenBlock).getBlock() instanceof BonemealableBlock && !(host.level.getBlockState(chosenBlock).getBlock() instanceof IPlantable))
                                {
                                    host.level.setBlockAndUpdate(chosenBlock, Blocks.COARSE_DIRT.defaultBlockState());
                                } else
                                {
                                    host.level.setBlockAndUpdate(chosenBlock, Blocks.AIR.defaultBlockState());
                                }

                                SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(new Vec3(chosenBlock.getX() + 0.5F, chosenBlock.getY() + 1.0F, chosenBlock.getZ() + 0.5F), new Vec3(0.05D, -1.05D, 0.05D), 10, 8, 0.5F);
                                MWAWPacketHandler.packetHandler.sendToDimension(host.level.dimension(), spawnParticleMessage);

                                host.playSound(SoundEvents.GRASS_BREAK, 0.3F, 0.1F);

                                SpawnSuckingParticleMessage spawnSuckingParticleMessage = new SpawnSuckingParticleMessage(new Vec3(host.getX(), host.getY() + (host.getBbHeight() * 0.5F) + 1.0F, host.getZ()), new Vec3(0.05D, 0.05D, 0.05D), 3, 1, 0.5F);
                                MWAWPacketHandler.packetHandler.sendToDimension(host.level.dimension(), spawnSuckingParticleMessage);
                                host.heal(5);
                            } else
                            {
                                host.playSound(SoundEvents.EVOKER_PREPARE_SUMMON, 0.3F, 0.1F);
                            }
                        }
                    } else
                    {
                        for (int i = 1; i <= 180; ++i)
                        {
                            double yaw = i * 360 / 180;
                            double speed = 0.4;
                            double xSpeed = speed * Math.cos(Math.toRadians(yaw));
                            double zSpeed = speed * Math.sin(Math.toRadians(yaw));

                            SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(new Vec3(host.getX(), host.getY() + (host.getBbHeight() * 0.5F) + 1.0F, host.getZ()), new Vec3(xSpeed, 0.0D, zSpeed), 1, 7, 0.0F);
                            MWAWPacketHandler.packetHandler.sendToDimension(host.level.dimension(), spawnParticleMessage);
                        }

                        List<LivingEntity> entities = EntityHelper.getEntityLivingBaseNearby(host, 10, 3, 10, 10);
                        for (LivingEntity entity : entities)
                        {
                            if (!(entity instanceof Player && ((Player) entity).isCreative()) && !(entity instanceof DendroidElderEntity) && !(entity instanceof DendroidEntity))
                            {
                                double angle = (EntityHelper.getAngleBetweenEntities(host, entity) + 90) * Math.PI / 180;
                                double distance = distanceTo(entity);
                                entity.setDeltaMovement(
                                        entity.getDeltaMovement().x() + Math.min(1 / (distance * distance), 1) * -4 * Math.cos(angle),
                                        entity.getDeltaMovement().y(),
                                        entity.getDeltaMovement().z() + Math.min(1 / (distance * distance), 1) * -4 * Math.sin(angle));

                                if (entity instanceof Player)
                                {
                                    entity.hurtMarked = true;
                                }

                                int i = 0;
                                if (host.level.getDifficulty() == Difficulty.NORMAL)
                                {
                                    i = 5;
                                } else if (host.level.getDifficulty() == Difficulty.HARD)
                                {
                                    i = 10;
                                }

                                entity.addEffect(new MobEffectInstance(MobEffects.POISON, 20 * i, 1));
                                entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 20 * i, 1));
                            }
                        }

                        host.playSound(SoundEvents.EVOKER_CAST_SPELL, 5.0F, 0.1F);

                        if (host.getAbsorbTimer() == host.getAbsorbTimerMax())
                        {
                            if (natureBlocks.size() > 0)
                            {
                                for (BlockPos pos : natureBlocks)
                                {
                                    if (host.level.getBlockState(pos).getBlock() instanceof BonemealableBlock && !(host.level.getBlockState(pos).getBlock() instanceof IPlantable))
                                    {
                                        host.level.setBlockAndUpdate(new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ()), ItemBlockRegistryHandler.DENDROID_ROOTS_BLOCK.get().defaultBlockState());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private class ElderGroundSlamGoal extends Goal
    {
        DendroidElderEntity host;
        private int coolDown;

        public ElderGroundSlamGoal(DendroidElderEntity creature)
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