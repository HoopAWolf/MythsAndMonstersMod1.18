package com.hoopawolf.mwaw.entities;

import com.hoopawolf.mwaw.client.animation.AnimationHelper;
import com.hoopawolf.mwaw.client.animation.PercentageRotation;
import com.hoopawolf.mwaw.entities.ai.controller.MWAWMovementController;
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
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

public class DropBearEntity extends PathfinderMob implements Enemy
{
    private static final EntityDataAccessor<Boolean> TIRED = SynchedEntityData.defineId(DropBearEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> GRABBEDTARGET = SynchedEntityData.defineId(DropBearEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> HUGGING = SynchedEntityData.defineId(DropBearEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> ANIMATION_SPEED = SynchedEntityData.defineId(DropBearEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> HUG_DIR = SynchedEntityData.defineId(DropBearEntity.class, EntityDataSerializers.INT); //PZ, PX, NZ, NX

    private static final EntityDataAccessor<Float> GRABBEDTARGETX = SynchedEntityData.defineId(DropBearEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> GRABBEDTARGETY = SynchedEntityData.defineId(DropBearEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> GRABBEDTARGETZ = SynchedEntityData.defineId(DropBearEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> ATTACK_TIMER = SynchedEntityData.defineId(DendroidElderEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Rotations> HEAD_ROTATION = SynchedEntityData.defineId(DropBearEntity.class, EntityDataSerializers.ROTATIONS);
    private static final EntityDataAccessor<Rotations> BODY_ROTATION = SynchedEntityData.defineId(DropBearEntity.class, EntityDataSerializers.ROTATIONS);
    private static final EntityDataAccessor<Rotations> RIGHT_ARM_ROTATION = SynchedEntityData.defineId(DropBearEntity.class, EntityDataSerializers.ROTATIONS);
    private static final EntityDataAccessor<Rotations> LEFT_ARM_ROTATION = SynchedEntityData.defineId(DropBearEntity.class, EntityDataSerializers.ROTATIONS);
    private static final EntityDataAccessor<Rotations> RIGHT_LEG_ROTATION = SynchedEntityData.defineId(DropBearEntity.class, EntityDataSerializers.ROTATIONS);
    private static final EntityDataAccessor<Rotations> LEFT_LEG_ROTATION = SynchedEntityData.defineId(DropBearEntity.class, EntityDataSerializers.ROTATIONS);
    public final AnimationHelper animation = new AnimationHelper();

    private final Class[] grabTargets = {
            Player.class
    };
    private final float attackTimerMax = 10;
    private BlockPos huggingBlockPos;
    private Vec3 stayingPos;
    private LivingEntity grabbedEntity;

    public DropBearEntity(EntityType<? extends DropBearEntity> type, Level worldIn)
    {
        super(type, worldIn);
        this.setTired(false);

        animation.defineSynchedData(HEAD_ROTATION);
        animation.defineSynchedData(BODY_ROTATION);
        animation.defineSynchedData(RIGHT_ARM_ROTATION);
        animation.defineSynchedData(LEFT_ARM_ROTATION);
        animation.defineSynchedData(LEFT_LEG_ROTATION);
        animation.defineSynchedData(RIGHT_LEG_ROTATION);

        this.moveControl = new MWAWMovementController(this, 30);

        this.setPathfindingMalus(BlockPathTypes.LEAVES, -1.0F);

        huggingBlockPos = null;
    }

    public static AttributeSupplier.Builder func_234321_m_()
    {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 8.0D).add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.FOLLOW_RANGE, 12.0D).add(Attributes.MOVEMENT_SPEED, 0.2D);
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();

        this.entityData.define(TIRED, true);
        this.entityData.define(GRABBEDTARGET, false);
        this.entityData.define(HUGGING, false);
        this.entityData.define(ANIMATION_SPEED, 0F);
        this.entityData.define(HUG_DIR, 0);

        this.entityData.define(ATTACK_TIMER, 0F);
        this.entityData.define(GRABBEDTARGETX, 0F);
        this.entityData.define(GRABBEDTARGETY, 0F);
        this.entityData.define(GRABBEDTARGETZ, 0F);

        this.entityData.define(HEAD_ROTATION, new Rotations(0, 0, 0));
        this.entityData.define(BODY_ROTATION, new Rotations(0, 0, 0));
        this.entityData.define(RIGHT_ARM_ROTATION, new Rotations(0, 0, 0));
        this.entityData.define(LEFT_ARM_ROTATION, new Rotations(0, 0, 0));
        this.entityData.define(LEFT_LEG_ROTATION, new Rotations(0, 0, 0));
        this.entityData.define(RIGHT_LEG_ROTATION, new Rotations(0, 0, 0));
    }

    @Override
    protected PathNavigation createNavigation(Level worldIn)
    {
        return new WallClimberNavigation(this, worldIn);
    }

    @Override
    protected void registerGoals()
    {
        this.targetSelector.addGoal(2, (new HurtByTargetGoal(this)));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));

        this.goalSelector.addGoal(0, new DropBearSwim(this));
        this.goalSelector.addGoal(1, new JumpToHugGoal(this, 20));
        this.goalSelector.addGoal(2, new DropBearMelee(this, 1.0D, true));
        this.goalSelector.addGoal(3, new FindLogToHugGoal(this, 10));
        this.goalSelector.addGoal(12, new DropBearLookAt(this, Player.class, 20.0F));
    }

    @Override
    protected boolean shouldDespawnInPeaceful()
    {
        return true;
    }

    public boolean isTired()
    {
        return entityData.get(TIRED);
    }

    public void setTired(boolean tired)
    {
        entityData.set(TIRED, tired);
    }

    public boolean isHugging()
    {
        return entityData.get(HUGGING);
    }

    public void setHugging(boolean hugging)
    {
        entityData.set(HUGGING, hugging);
    }

    public boolean grabbedTarget()
    {
        return entityData.get(GRABBEDTARGET);
    }

    public void setGrabbedTarget(boolean grabbed)
    {
        entityData.set(GRABBEDTARGET, grabbed);
    }

    public float getAnimationSpeed()
    {
        return entityData.get(ANIMATION_SPEED);
    }

    public void setAnimationSpeed(float speedIn)
    {
        entityData.set(ANIMATION_SPEED, speedIn);
    }

    public int getHuggingDir()
    {
        return entityData.get(HUG_DIR);
    }

    public void setHuggingDir(int huggingDirIn)
    {
        entityData.set(HUG_DIR, huggingDirIn);
    }

    public float getAttackTimer()
    {
        return entityData.get(ATTACK_TIMER);
    }

    public void setAttackTimer(float timer)
    {
        entityData.set(ATTACK_TIMER, timer);
    }

    public Vec3 getTargetPos()
    {
        return new Vec3(entityData.get(GRABBEDTARGETX), entityData.get(GRABBEDTARGETY), entityData.get(GRABBEDTARGETZ));
    }

    public void setTargetPos(double xIn, double yIn, double zIn)
    {
        entityData.set(GRABBEDTARGETX, (float) xIn);
        entityData.set(GRABBEDTARGETY, (float) yIn);
        entityData.set(GRABBEDTARGETZ, (float) zIn);
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
    public void tick()
    {
        super.tick();

        if (isHugging())
        {
            yBodyRot = 0;
        }

        if (grabbedTarget())
        {
            this.setPos(getTargetPos().x(), getTargetPos().y(), getTargetPos().z());
        }

        if (!level.isClientSide)
        {
            if (isHugging())
            {
                setNoGravity(true);
                noPhysics = true;
                setDeltaMovement(Vec3.ZERO);
                this.setPos(stayingPos.x(), stayingPos.y(), stayingPos.z());

                if (level.isEmptyBlock(huggingBlockPos))
                {
                    huggingBlockPos = null;
                    setHugging(false);
                }
            } else
            {
                setNoGravity(false);

                if (grabbedEntity != null)
                {
                    if (grabbedEntity.isAlive())
                    {
                        noPhysics = true;
                        setGrabbedTarget(true);
                        setTargetPos(grabbedEntity.getX() + grabbedEntity.getLookAngle().x() * 0.2F, grabbedEntity.getEyeY() - 0.5D, grabbedEntity.getZ() + grabbedEntity.getLookAngle().z() * 0.2F);
                    } else
                    {
                        this.setDeltaMovement(getDeltaMovement().x(), 0.0D, getDeltaMovement().z());
                        this.setPos(grabbedEntity.getX() + grabbedEntity.getLookAngle().x() * 0.5F, grabbedEntity.getEyeY() - 0.5D, grabbedEntity.getZ() + grabbedEntity.getLookAngle().z() * 0.5F);
                        grabbedEntity = null;
                        noPhysics = false;
                        setGrabbedTarget(false);
                    }
                } else
                {
                    noPhysics = false;
                }

                if (getAttackTimer() > 0)
                {
                    setAttackTimer(getAttackTimer() - 1.0F);
                }
            }

            if (isHugging())
            {
                this.setTired(level.isDay());
            } else
            {
                this.setTired(false);
            }
        } else
        {
            setAnimationSpeed(0.08F);

            if (!isHugging())
            {
                if (getAttackTimer() > attackTimerMax * 0.3F)
                {
                    animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(-95, -35, 0)));
                    animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(-95, 35, 0)));
                } else if (getAttackTimer() > 0)
                {
                    animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(-55, 25, 0)));
                    animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(-55, -25, 0)));
                } else
                {
                    animation.registerRotationPoints(HEAD_ROTATION, new PercentageRotation(getHeadRotation(), new Rotations(0, 0, 0)));
                    animation.registerRotationPoints(BODY_ROTATION, new PercentageRotation(getBodyRotation(), new Rotations(0, 0, 0)));
                    animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(0, 0, 0)));
                    animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(0, 0, 0)));

                    animation.registerRotationPoints(RIGHT_LEG_ROTATION, new PercentageRotation(getRightLegRotation(), new Rotations(0, 0, 0)));
                    animation.registerRotationPoints(LEFT_LEG_ROTATION, new PercentageRotation(getLeftLegRotation(), new Rotations(0, 0, 0)));
                }
            } else
            {
                if (!isTired())
                {
                    animation.registerRotationPoints(HEAD_ROTATION, new PercentageRotation(getHeadRotation(), new Rotations(-30, 0, 0)));
                    animation.registerRotationPoints(BODY_ROTATION, new PercentageRotation(getBodyRotation(), new Rotations(90, 90 * this.getHuggingDir(), 0)));
                } else
                {
                    animation.registerRotationPoints(HEAD_ROTATION, new PercentageRotation(getHeadRotation(), new Rotations(75, 0, 0)));
                    animation.registerRotationPoints(BODY_ROTATION, new PercentageRotation(getBodyRotation(), new Rotations(-90, (90 * this.getHuggingDir()) - 180, 0)));
                }

                animation.registerRotationPoints(RIGHT_ARM_ROTATION, new PercentageRotation(getRightArmRotation(), new Rotations(-30, 0, 0)));
                animation.registerRotationPoints(LEFT_ARM_ROTATION, new PercentageRotation(getLeftArmRotation(), new Rotations(-30, 0, 0)));
                animation.registerRotationPoints(RIGHT_LEG_ROTATION, new PercentageRotation(getRightLegRotation(), new Rotations(20, 0, 0)));
                animation.registerRotationPoints(LEFT_LEG_ROTATION, new PercentageRotation(getLeftLegRotation(), new Rotations(20, 0, 0)));

            }
        }
    }

    @Override
    public boolean onClimbable()
    {
        return false;
    }

    @Override
    public boolean isPushable()
    {
        return !noPhysics;
    }

    @Override
    protected void doPush(Entity entityIn)
    {
        if (!level.isClientSide)
        {
            if (!noPhysics)
            {
                super.doPush(entityIn);
            }
        }
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos)
    {
    }

    @Override
    public float getVoicePitch()
    {
        return this.random.nextFloat() - this.random.nextFloat() * 0.2F + 1.9F;
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return (isTired() ? SoundEvents.FOX_SLEEP : null);
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.PANDA_HURT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.HUSK_DEATH;
    }

    @Override
    public boolean hurt(DamageSource source, float amount)
    {
        if (!level.isClientSide)
        {
            setHugging(false);
            huggingBlockPos = null;
        }

        return super.hurt(source, amount);
    }

    @Override
    public boolean doHurtTarget(Entity entityIn)
    {
        this.setAttackTimer(attackTimerMax);
        this.playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 0.5F, 0.8F);

        return super.doHurtTarget(entityIn);
    }

    private static class DropBearSwim extends FloatGoal
    {
        DropBearEntity host;

        public DropBearSwim(Mob entityIn)
        {
            super(entityIn);
            host = (DropBearEntity) entityIn;
        }

        @Override
        public void start()
        {
            super.start();
            host.setTired(false);
        }
    }

    private static class DropBearLookAt extends LookAtPlayerGoal
    {
        DropBearEntity host;

        public DropBearLookAt(Mob entityIn, Class<? extends LivingEntity> watchTargetClass, float maxDistance)
        {
            super(entityIn, watchTargetClass, maxDistance);
            host = (DropBearEntity) entityIn;
        }

        @Override
        public boolean canUse()
        {
            return super.canUse() && !host.isHugging();
        }
    }

    private static class DropBearMelee extends MeleeAttackGoal
    {
        DropBearEntity host;

        public DropBearMelee(PathfinderMob creature, double speedIn, boolean useLongMemory)
        {
            super(creature, speedIn, useLongMemory);
            host = (DropBearEntity) creature;
        }

        @Override
        public boolean canUse()
        {
            return super.canUse() && !host.isHugging();
        }
    }

    private static class JumpToHugGoal extends Goal
    {
        private final DropBearEntity dropBearEntity;
        private final float checkDist;
        private LivingEntity tryHugTarget;

        public JumpToHugGoal(DropBearEntity _entity, float checkDistIn)
        {
            dropBearEntity = _entity;
            checkDist = checkDistIn;


            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        @Override
        public boolean canUse()
        {
            if (!this.dropBearEntity.isTired() && this.dropBearEntity.grabbedEntity == null && this.dropBearEntity.isHugging())
            {
                for (Class entClass : this.dropBearEntity.grabTargets)
                {
                    List<LivingEntity> list = this.dropBearEntity.level.getEntitiesOfClass(entClass, (new AABB(this.dropBearEntity.getX(), this.dropBearEntity.getY(), this.dropBearEntity.getZ(), this.dropBearEntity.getX() + 1.0D, this.dropBearEntity.getY() + 1.0D, this.dropBearEntity.getZ() + 1.0D)).inflate(checkDist * 0.2F, checkDist, checkDist * 0.2F));
                    Iterator<LivingEntity> iterator = list.iterator();

                    while (iterator.hasNext())
                    {
                        LivingEntity _ent = iterator.next();

                        if (!dropBearEntity.hasLineOfSight(_ent) || (_ent instanceof Player && ((Player) _ent).isCreative()))
                            continue;

                        if (tryHugTarget != null)
                        {
                            if (this.dropBearEntity.distanceToSqr(tryHugTarget) > this.dropBearEntity.distanceToSqr(_ent))
                                tryHugTarget = _ent;
                        } else
                            tryHugTarget = _ent;
                    }
                }

                return tryHugTarget != null;
            }


            return false;
        }

        @Override
        public void stop()
        {
            tryHugTarget = null;
        }

        @Override
        public boolean canContinueToUse()
        {
            return this.dropBearEntity.grabbedEntity == null && tryHugTarget.distanceToSqr(this.dropBearEntity) < 220 && tryHugTarget.isAlive() && !dropBearEntity.isOnGround();
        }

        @Override
        public void tick()
        {
            if (tryHugTarget.distanceToSqr(this.dropBearEntity) < 220 && !dropBearEntity.isOnGround() && dropBearEntity.isHugging())
            {
                if (dropBearEntity.isHugging())
                {
                    dropBearEntity.setHugging(false);
                }

                Vec3 dir = tryHugTarget.position().subtract(this.dropBearEntity.position()).normalize();
                Vec3 motion = new Vec3(dir.x * 1.5F, Mth.sign(dir.y), dir.z * 1.5F);

                double d2 = tryHugTarget.getX() - dropBearEntity.getX();
                double d1 = tryHugTarget.getZ() - dropBearEntity.getZ();
                dropBearEntity.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
                dropBearEntity.yBodyRot = dropBearEntity.getYRot();

                this.dropBearEntity.setDeltaMovement(motion.x(), motion.y(), motion.z());
            }

            if (this.dropBearEntity.getBoundingBox().intersects(tryHugTarget.getBoundingBox().inflate(1.0D)) && !(tryHugTarget instanceof Player && !((Player) tryHugTarget).getInventory().armor.get(3).isEmpty()))
            {
                this.dropBearEntity.grabbedEntity = tryHugTarget;
                this.dropBearEntity.setTarget(tryHugTarget);
                this.dropBearEntity.playSound(SoundEvents.HUSK_DEATH, 1.0F, 0.1F);
            }
        }
    }

    private static class FindLogToHugGoal extends Goal
    {
        private final DropBearEntity dropBearEntity;
        private final int checkDist;
        private BlockPos tryHugTarget;
        private float timer = 0;

        public FindLogToHugGoal(DropBearEntity _entity, int checkDistIn)
        {
            dropBearEntity = _entity;
            checkDist = checkDistIn;

            this.setFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean canUse()
        {
            if (!this.dropBearEntity.isHugging() && this.dropBearEntity.isOnGround())
            {
                BlockPos highestBlock = null;
                for (int x = -checkDist; x < checkDist; ++x)
                {
                    for (int z = -checkDist; z < checkDist; ++z)
                    {
                        for (int y = 0; y < 16; ++y)
                        {
                            if (dropBearEntity.level.getBlockState(dropBearEntity.blockPosition().offset(x, y, z)).getMaterial().equals(Material.WOOD))
                            {
                                BlockPos currBlockPos = dropBearEntity.blockPosition().offset(x, y, z);

                                if (highestBlock != null)
                                {
                                    if (currBlockPos.getY() > highestBlock.getY())
                                    {
                                        highestBlock = dropBearEntity.blockPosition().offset(x, y, z);
                                    }
                                } else
                                {
                                    highestBlock = dropBearEntity.blockPosition().offset(x, y, z);
                                }
                            } else
                            {
                                if (y >= 3)
                                {
                                    break;
                                }
                            }
                        }
                    }
                }

                if (highestBlock != null)
                {
                    tryHugTarget = new BlockPos(highestBlock.getX() + 0.5F, highestBlock.getY(), highestBlock.getZ() + 0.5F);
                }

                return tryHugTarget != null;
            }


            return false;
        }

        @Override
        public void stop()
        {
            tryHugTarget = null;
            timer = 0.0F;
            this.dropBearEntity.navigation.stop();
        }

        @Override
        public boolean canContinueToUse()
        {
            return timer < 50 && tryHugTarget != null && dropBearEntity.level.getBlockState(tryHugTarget).getMaterial().equals(Material.WOOD) && !dropBearEntity.isHugging();
        }

        @Override
        public void tick()
        {
            if (this.dropBearEntity.tickCount % 8 == 0)
            {
                timer += 1.0F;
            }

            if (this.dropBearEntity.distanceToSqr(tryHugTarget.getX(), tryHugTarget.getY(), tryHugTarget.getZ()) < 2.0D || timer > 30)
            {
                if (this.dropBearEntity.level.getBlockState(new BlockPos(this.dropBearEntity.position().x + 1, this.dropBearEntity.position().y(), this.dropBearEntity.position().z())).getMaterial().equals(Material.WOOD) ||
                        this.dropBearEntity.level.getBlockState(new BlockPos(this.dropBearEntity.position().x - 1, this.dropBearEntity.position().y(), this.dropBearEntity.position().z())).getMaterial().equals(Material.WOOD) ||
                        this.dropBearEntity.level.getBlockState(new BlockPos(this.dropBearEntity.position().x, this.dropBearEntity.position().y(), this.dropBearEntity.position().z() + 1)).getMaterial().equals(Material.WOOD) ||
                        this.dropBearEntity.level.getBlockState(new BlockPos(this.dropBearEntity.position().x, this.dropBearEntity.position().y(), this.dropBearEntity.position().z() - 1)).getMaterial().equals(Material.WOOD))
                {
                    if (this.dropBearEntity.level.getBlockState(new BlockPos(this.dropBearEntity.position().x + 1, this.dropBearEntity.position().y(), this.dropBearEntity.position().z())).getMaterial().equals(Material.WOOD))
                    {
                        this.dropBearEntity.huggingBlockPos = new BlockPos(this.dropBearEntity.position().x() + 1, this.dropBearEntity.position().y(), this.dropBearEntity.position().z());
                        this.dropBearEntity.setHuggingDir(1);
                    } else if (this.dropBearEntity.level.getBlockState(new BlockPos(this.dropBearEntity.position().x - 1, this.dropBearEntity.position().y(), this.dropBearEntity.blockPosition().getZ())).getMaterial().equals(Material.WOOD))
                    {
                        this.dropBearEntity.huggingBlockPos = new BlockPos(this.dropBearEntity.position().x - 1, this.dropBearEntity.position().y(), this.dropBearEntity.blockPosition().getZ());
                        this.dropBearEntity.setHuggingDir(3);
                    } else if (this.dropBearEntity.level.getBlockState(new BlockPos(this.dropBearEntity.position().x, this.dropBearEntity.position().y(), this.dropBearEntity.blockPosition().getZ() + 1)).getMaterial().equals(Material.WOOD))
                    {
                        this.dropBearEntity.huggingBlockPos = new BlockPos(this.dropBearEntity.position().x, this.dropBearEntity.position().y(), this.dropBearEntity.blockPosition().getZ() + 1);
                        this.dropBearEntity.setHuggingDir(2);
                    } else if (this.dropBearEntity.level.getBlockState(new BlockPos(this.dropBearEntity.position().x, this.dropBearEntity.position().y(), this.dropBearEntity.blockPosition().getZ() - 1)).getMaterial().equals(Material.WOOD))
                    {
                        this.dropBearEntity.huggingBlockPos = new BlockPos(this.dropBearEntity.position().x, this.dropBearEntity.position().y(), this.dropBearEntity.blockPosition().getZ() - 1);
                        this.dropBearEntity.setHuggingDir(0);
                    }
                } else
                { //PZ, PX, NZ, NX
                    Vec3 dir = new Vec3(tryHugTarget.getX() - (int) this.dropBearEntity.getX(), 0, tryHugTarget.getZ() - (int) this.dropBearEntity.getZ());
                    if (dir.x() == 1)
                    {
                        this.dropBearEntity.setPos(tryHugTarget.getX() + 1, this.dropBearEntity.getY(), tryHugTarget.getZ());
                    } else if (dir.x() == -1)
                    {
                        this.dropBearEntity.setPos(tryHugTarget.getX() - 1, this.dropBearEntity.getY(), tryHugTarget.getZ());
                    } else if (dir.z() == -1)
                    {
                        this.dropBearEntity.setPos(tryHugTarget.getX(), this.dropBearEntity.getY(), tryHugTarget.getZ() + 1);
                    } else
                    {
                        this.dropBearEntity.setPos(tryHugTarget.getX(), this.dropBearEntity.getY(), tryHugTarget.getZ() - 1);
                    }
                }

                if (this.dropBearEntity.huggingBlockPos != null)
                {
                    this.dropBearEntity.setHugging(true);
                    this.dropBearEntity.setPos((float) this.dropBearEntity.position().x + 0.5F, this.dropBearEntity.position().y(), (float) this.dropBearEntity.position().z() + 0.5F);
                    this.dropBearEntity.stayingPos = new Vec3((float) this.dropBearEntity.position().x + 0.5F, this.dropBearEntity.position().y(), (float) this.dropBearEntity.position().z() + 0.5F);
                }
            } else
            {
                this.dropBearEntity.moveControl.setWantedPosition(tryHugTarget.getX(), tryHugTarget.getY(), tryHugTarget.getZ(), 1.0D);

                if (dropBearEntity.horizontalCollision)
                {
                    Vec3 dir = new Vec3(tryHugTarget.getX(), tryHugTarget.getY(), tryHugTarget.getZ()).subtract(this.dropBearEntity.position()).normalize();
                    Vec3 motion = new Vec3(0, Mth.sign(dir.y) * 0.1F, 0);

                    double d2 = tryHugTarget.getX() - dropBearEntity.getX();
                    double d1 = tryHugTarget.getZ() - dropBearEntity.getZ();
                    dropBearEntity.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
                    dropBearEntity.yBodyRot = dropBearEntity.getYRot();

                    this.dropBearEntity.setDeltaMovement(motion.x(), motion.y(), motion.z());
                }
            }
        }
    }
}
