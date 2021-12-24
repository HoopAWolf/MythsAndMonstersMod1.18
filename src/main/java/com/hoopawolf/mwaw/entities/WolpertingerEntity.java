package com.hoopawolf.mwaw.entities;

import com.hoopawolf.mwaw.entities.ai.AnimalMeleeAttackGoal;
import com.hoopawolf.mwaw.network.MWAWPacketHandler;
import com.hoopawolf.mwaw.network.packets.client.SpawnParticleMessage;
import com.hoopawolf.mwaw.util.EntityRegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

public class WolpertingerEntity extends Animal
{
    private static final EntityDataAccessor<Integer> TYPE = SynchedEntityData.defineId(WolpertingerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> ANGRY = SynchedEntityData.defineId(WolpertingerEntity.class, EntityDataSerializers.BOOLEAN);
    private final Class[] grabTargets = {
            FairyEntity.class
    };

    MoveControl flyingController = new FlyingMoveControl(this, 20, true);
    MoveControl landController = new WolpertingerEntity.MoveHelperController(this);
    private int jumpTicks;
    private int jumpDuration;
    private boolean wasOnGround;
    private int currentMoveTypeDuration;
    private float moving_timer;
    private boolean isScared;
    private float scared_timer;
    private float drop_timer;
    private float pickup_timer;
    private Entity grabbedEntity;
    private FlyingAvoidEntityGoal flyingavoidgoal;

    public WolpertingerEntity(EntityType<? extends WolpertingerEntity> type, Level worldIn)
    {
        super(type, worldIn);
        this.jumpControl = new WolpertingerEntity.JumpHelperController(this);
        this.moveControl = landController;
        this.setMovementSpeed(0.0D);
        moving_timer = 0.0F;
        isScared = false;
        scared_timer = 0.0F;
        grabbedEntity = null;
        this.ejectPassengers();
    }

    public static AttributeSupplier.Builder func_234321_m_()
    {
        return Monster.createMonsterAttributes().add(Attributes.FLYING_SPEED, 1.2D).add(Attributes.MAX_HEALTH, 5.0D)
                .add(Attributes.FOLLOW_RANGE, 12.0D).add(Attributes.MOVEMENT_SPEED, 0.35D);
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader worldIn)
    {
        return worldIn.isEmptyBlock(pos) ? 10.0F : 0.0F;
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(TYPE, 0);
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new AnimalMeleeAttackGoal(this, 1.0D, true, 2, 1));
        this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
        this.goalSelector.addGoal(4, new TemptGoal(this, 1.0D, Ingredient.of(Items.GOLDEN_CARROT), false));
        this.goalSelector.addGoal(5, new RandomWalkingWithRidden(this, 1.0D));
        this.goalSelector.addGoal(6, new JumpToGrabGoal(this, 10));
        this.goalSelector.addGoal(8, new LookAtWithPassenger(this, Player.class, 4.0F));
        this.goalSelector.addGoal(8, new LookAtWithPassenger(this, PathfinderMob.class, 4.0F));
    }

    private void MoveToPos(BlockPos p_226433_1_)
    {
        if (p_226433_1_ != null)
        {
            this.navigation.moveTo(p_226433_1_.getX(), p_226433_1_.getY(), p_226433_1_.getZ(), 1.0D);

            if (tickCount % 5 == 0 && !this.level.isClientSide)
            {
                Vec3 _vec = new Vec3(this.getX() - (double) 0.3F, this.getY(0.5D), this.getZ() + (double) 0.3F);
                SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(_vec, new Vec3(0, 0, 0), 1, 3, getBbWidth());
                MWAWPacketHandler.packetHandler.sendToDimension(this.level.dimension(), spawnParticleMessage);
            }
        }
    }

    @Override
    protected float getJumpPower()
    {
        if (!this.horizontalCollision && (!this.moveControl.hasWanted() || !(this.moveControl.getWantedY() > this.getY() + 0.5D)))
        {
            Path path = this.navigation.getPath();
            if (path != null && path.getNextNodeIndex() < path.getNodeCount())
            {
                Vec3 vec3d = path.getNextEntityPos(this);
                if (vec3d.y > this.getY() + 0.5D)
                {
                    return 0.5F;
                }
            }

            return this.moveControl.getSpeedModifier() <= 0.6D ? 0.2F : 0.3F;
        } else
        {
            return 0.5F;
        }
    }

    @Override
    protected void jumpFromGround()
    {
        if (!isScared)
        {
            super.jumpFromGround();
            ++moving_timer;

            if (moving_timer >= 5)
            {
                this.navigation.stop();
                this.setDeltaMovement(random.nextDouble() - random.nextDouble(), 0.0D, random.nextDouble() - random.nextDouble());
                moving_timer = 0.0F;
            }

            double d0 = this.moveControl.getSpeedModifier();
            if (d0 > 0.0D)
            {
                double d1 = this.getDeltaMovement().horizontalDistanceSqr();
                if (d1 < 0.01D)
                {
                    this.moveRelative(0.1F, new Vec3(0.0D, 0.0D, 1.0D));
                }
            }

            if (!this.level.isClientSide)
            {
                this.level.broadcastEntityEvent(this, (byte) 1);
            }
        }
    }

    @Override
    protected PathNavigation createNavigation(Level worldIn)
    {
        if (isScared)
        {
            FlyingPathNavigation flyingpathnavigator = new FlyingPathNavigation(this, worldIn)
            {
                @Override
                public boolean isStableDestination(BlockPos pos)
                {
                    return !this.level.isEmptyBlock(pos.below());
                }

            };
            flyingpathnavigator.setCanOpenDoors(false);
            flyingpathnavigator.setCanFloat(false);
            flyingpathnavigator.setCanPassDoors(true);
            return flyingpathnavigator;
        } else
        {
            return super.createNavigation(worldIn);
        }
    }

    public float getJumpCompletion(float p_175521_1_)
    {
        return this.jumpDuration == 0 ? 0.0F : ((float) this.jumpTicks + p_175521_1_) / (float) this.jumpDuration;
    }

    public void setMovementSpeed(double newSpeed)
    {
        this.getNavigation().setSpeedModifier(newSpeed);
        this.moveControl.setWantedPosition(this.moveControl.getWantedX(), this.moveControl.getWantedY(), this.moveControl.getWantedZ(), newSpeed);
    }

    public void startJumping()
    {
        this.setJumping(true);
        this.jumpDuration = 10;
        this.jumpTicks = 0;
    }

    protected SoundEvent getJumpSound()
    {
        return SoundEvents.RABBIT_JUMP;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.RABBIT_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.RABBIT_HURT;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound)
    {
        super.addAdditionalSaveData(compound);
        compound.putInt("WolpertingerType", this.getWolpertingerType());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound)
    {
        super.readAdditionalSaveData(compound);
        if(compound.contains("WolpertingerType"))
        {
            this.setWolpertingerType(compound.getInt("WolpertingerType"));
        }
    }

    public int getWolpertingerType()
    {
        return entityData.get(TYPE);
    }

    public void setWolpertingerType(int wolperTypeId)
    {
        entityData.set(TYPE, wolperTypeId);
    }

    public boolean isAngry()
    {
        return entityData.get(ANGRY);
    }

    public void setAngry(boolean angry)
    {
        entityData.set(ANGRY, angry);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag)
    {
        Biome optional = worldIn.getBiome(this.blockPosition());
        int i = 0;

        if (optional.getRegistryName() == Biomes.TAIGA.getRegistryName())
            i = 1;
        else if (optional.getRegistryName() == Biomes.SUNFLOWER_PLAINS.getRegistryName())
            i = 2;
        else if (optional.getRegistryName() == Biomes.WOODED_BADLANDS.getRegistryName())
            i = 3;

        this.setWolpertingerType(i);
        if (level.random.nextInt(100) < 30)
            this.setAge(-24000);

        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob ageable)
    {
        WolpertingerEntity wolpertingerbaby = new WolpertingerEntity(EntityRegistryHandler.WOLPERTINGER_ENTITY.get(), level);

        wolpertingerbaby.setWolpertingerType(this.getWolpertingerType());
        return wolpertingerbaby;
    }

    @Override
    protected void customServerAiStep()
    {
        if (this.currentMoveTypeDuration > 0)
        {
            --this.currentMoveTypeDuration;
        }

        if (this.onGround && !isScared)
        {
            if (!this.wasOnGround)
            {
                this.setJumping(false);
                this.checkLandingDelay();
            }

            WolpertingerEntity.JumpHelperController wolpertingerentity$jumphelpercontroller = (WolpertingerEntity.JumpHelperController) this.jumpControl;
            if (!wolpertingerentity$jumphelpercontroller.getIsJumping())
            {
                if (this.moveControl.hasWanted() && this.currentMoveTypeDuration == 0)
                {
                    Path path = this.navigation.getPath();
                    Vec3 vec3d = new Vec3(this.moveControl.getWantedX(), this.moveControl.getWantedY(), this.moveControl.getWantedZ());
                    if (path != null && path.getNextNodeIndex() < path.getNodeCount())
                    {
                        vec3d = path.getNextEntityPos(this);
                    }

                    this.calculateRotationYaw(vec3d.x, vec3d.z);
                    this.startJumping();
                }
            } else if (!wolpertingerentity$jumphelpercontroller.canJump())
            {
                this.enableJumpControl();
            }
        }

        this.wasOnGround = this.onGround;
    }

    private void calculateRotationYaw(double x, double z)
    {
        this.setYRot((float) (Mth.atan2(z - this.getZ(), x - this.getX()) * (double) (180F / (float) Math.PI)) - 90.0F);
    }

    private void enableJumpControl()
    {
        ((WolpertingerEntity.JumpHelperController) this.jumpControl).setCanJump(true);
    }

    private void disableJumpControl()
    {
        ((WolpertingerEntity.JumpHelperController) this.jumpControl).setCanJump(false);
    }

    private void updateMoveTypeDuration()
    {
        if (this.moveControl.getSpeedModifier() < 2.2D)
        {
            this.currentMoveTypeDuration = 10;
        } else
        {
            this.currentMoveTypeDuration = 1;
        }

    }

    private void checkLandingDelay()
    {
        this.updateMoveTypeDuration();
        this.disableJumpControl();
    }

    @Override
    public void tick()
    {
        super.tick();

        if (!level.isClientSide)
        {
            if (!getPassengers().isEmpty())
            {
                if (drop_timer > 0)
                {

                    if (tickCount % 2 == 0)
                        --drop_timer;
                } else
                {
                    this.ejectPassengers();
                    grabbedEntity = null;
                    pickup_timer = 200;
                }
            }

            if (pickup_timer > 0)
            {
                if (tickCount % 2 == 0)
                    --pickup_timer;
            }
        }
    }

    @Override
    public void aiStep()
    {
        super.aiStep();

        if (!isNoGravity() && !jumping())
        {
            Vec3 vec3d = this.getDeltaMovement();
            if (!this.onGround && vec3d.y < 0.0D)
            {
                this.setDeltaMovement(vec3d.multiply(1.0D, 0.6D, 1.0D));
            }
        }

        if (getTarget() == null)
        {
            if (isScared)
            {
                if (tickCount % 5 == 0)
                    --scared_timer;

                if (scared_timer <= 0)
                {
                    this.setNoGravity(false);
                    this.isScared = false;
                    this.goalSelector.removeGoal(flyingavoidgoal);
                    this.moveControl = landController;
                    this.navigation = createNavigation(level);
                }
            }

            if (!this.isScared)
            {
                this.setNoGravity(false);

                if (this.jumpTicks != this.jumpDuration)
                {
                    ++this.jumpTicks;
                } else if (this.jumpDuration != 0)
                {
                    this.jumpTicks = 0;
                    this.jumpDuration = 0;
                    this.setJumping(false);
                }
            }
        }
    }

    public boolean jumping()
    {
        return jumping;
    }

    @Override
    public void setJumping(boolean jumping)
    {
        super.setJumping(jumping);
        if (jumping)
        {
            this.playSound(this.getJumpSound(), this.getSoundVolume(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 0.8F);
        }

    }

    @Override
    public int getMaxSpawnClusterSize()
    {
        return 4;
    }

    @Override
    public boolean isFood(ItemStack stack)
    {
        return stack.getItem() == Items.GOLDEN_CARROT;
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
            Entity entity = source.getEntity();
            if (!this.level.isClientSide && entity != null && this.hasLineOfSight(entity) && !this.isNoAi())
            {
                if (isScared)
                    this.goalSelector.removeGoal(flyingavoidgoal);

                this.isScared = true;

                flyingavoidgoal = new FlyingAvoidEntityGoal(this, entity, 10.0F);
                this.goalSelector.addGoal(3, flyingavoidgoal);
                this.moveControl = flyingController;
                this.navigation = createNavigation(level);
                scared_timer = 200;
            }

            return super.hurt(source, amount);
        }
    }

    @Override
    public void handleEntityEvent(byte id)
    {
        if (id == 1)
        {
            this.spawnSprintParticle();
            this.jumpDuration = 10;
            this.jumpTicks = 0;
        } else
        {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource dmgSource)
    {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos)
    {
    }

    static class MoveHelperController extends MoveControl
    {
        private final WolpertingerEntity wolpertinger;
        private double nextJumpSpeed;

        public MoveHelperController(WolpertingerEntity _wolpertinger)
        {
            super(_wolpertinger);
            this.wolpertinger = _wolpertinger;
        }

        @Override
        public void tick()
        {
            if (this.wolpertinger.onGround && !this.wolpertinger.jumping && !((WolpertingerEntity.JumpHelperController) this.wolpertinger.jumpControl).getIsJumping())
            {
                this.wolpertinger.setMovementSpeed(0.0D);
            } else if (this.hasWanted())
            {
                this.wolpertinger.setMovementSpeed(this.nextJumpSpeed);
                this.wolpertinger.moving_timer = 0.0F;
            }

            super.tick();
        }

        @Override
        public void setWantedPosition(double x, double y, double z, double speedIn)
        {
            if (this.wolpertinger.isInWater())
            {
                speedIn = 1.5D;
            }

            super.setWantedPosition(x, y, z, speedIn);
            if (speedIn > 0.0D)
            {
                this.nextJumpSpeed = speedIn;
            }
        }
    }

    static class FlyingAvoidEntityGoal extends Goal
    {
        private final WolpertingerEntity wolpertinger;
        private final float avoidDistance;
        private final Entity avoidTarget;

        public FlyingAvoidEntityGoal(WolpertingerEntity _wolpertinger, Entity p_i46403_2_, float p_i46403_3_)
        {
            this.wolpertinger = _wolpertinger;
            this.avoidTarget = p_i46403_2_;
            this.avoidDistance = p_i46403_3_;
        }


        @Override
        public boolean canUse()
        {
            return this.wolpertinger.isScared;
        }

        @Override
        public boolean canContinueToUse()
        {
            return this.wolpertinger.isScared && wolpertinger.getTarget() == null;
        }

        @Override
        public void tick()
        {
            if (this.wolpertinger.distanceTo(avoidTarget) < avoidDistance)
            {
                Vec3 vec3d = DefaultRandomPos.getPosAway(this.wolpertinger, 10, 5, this.avoidTarget.position());
                if (vec3d != null)
                {
                    this.wolpertinger.setNoGravity(true);
                    this.wolpertinger.MoveToPos(new BlockPos(vec3d.x, vec3d.y, vec3d.z));
                }
            } else
            {
                this.wolpertinger.setNoGravity(false);
                if (this.wolpertinger.tickCount % 5 == 0)
                    --this.wolpertinger.scared_timer;
            }
        }
    }

    static class JumpToGrabGoal extends Goal
    {
        private final WolpertingerEntity wolpertinger;
        private final float checkDist;
        private Entity tryGrabTarget;

        public JumpToGrabGoal(WolpertingerEntity _entity, float checkDistIn)
        {
            wolpertinger = _entity;
            checkDist = checkDistIn;
        }

        @Override
        public boolean canUse()
        {
            if (!this.wolpertinger.isScared && this.wolpertinger.grabbedEntity == null && this.wolpertinger.pickup_timer <= 0.0F && this.wolpertinger.getAge() >= 0)
            {
                for (Class entClass : this.wolpertinger.grabTargets)
                {
                    List<LivingEntity> list = this.wolpertinger.level.getEntitiesOfClass(entClass, (new AABB(this.wolpertinger.getX(), this.wolpertinger.getY(), this.wolpertinger.getZ(), this.wolpertinger.getX() + 1.0D, this.wolpertinger.getY() + 1.0D, this.wolpertinger.getZ() + 1.0D)).inflate(checkDist, checkDist, checkDist));
                    Iterator iterator = list.iterator();

                    while (iterator.hasNext())
                    {
                        LivingEntity _ent = (LivingEntity) iterator.next();

                        if (_ent.getVehicle() != null)
                            continue;

                        if (tryGrabTarget != null)
                        {
                            if (this.wolpertinger.distanceToSqr(tryGrabTarget) > this.wolpertinger.distanceToSqr(_ent))
                                tryGrabTarget = _ent;
                        } else
                            tryGrabTarget = _ent;
                    }
                }

                return tryGrabTarget != null;
            }


            return false;
        }

        @Override
        public void stop()
        {
            tryGrabTarget = null;
        }

        @Override
        public boolean canContinueToUse()
        {
            return this.wolpertinger.grabbedEntity == null && tryGrabTarget.distanceToSqr(this.wolpertinger) < 40;
        }

        @Override
        public void tick()
        {
            this.wolpertinger.navigation.moveTo(tryGrabTarget.position().x(), tryGrabTarget.position().y(), tryGrabTarget.position().z(), this.wolpertinger.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue());

            if (this.wolpertinger.onGround && tryGrabTarget.distanceToSqr(this.wolpertinger) < 10)
            {
                Vec3 dir = tryGrabTarget.position().subtract(this.wolpertinger.position()).normalize();
                Vec3 motion = this.wolpertinger.getDeltaMovement().add(dir.x, dir.y * 1.5F, dir.z);

                this.wolpertinger.setDeltaMovement(motion.x(), motion.y(), motion.z());
            }

            if (this.wolpertinger.getBoundingBox().intersects(tryGrabTarget.getBoundingBox()))
            {
                tryGrabTarget.startRiding(this.wolpertinger, true);
                this.wolpertinger.grabbedEntity = tryGrabTarget;
                this.wolpertinger.drop_timer = 200;
            }
        }
    }

    static class RandomWalkingWithRidden extends WaterAvoidingRandomStrollGoal
    {
        public RandomWalkingWithRidden(PathfinderMob creature, double speedIn)
        {
            super(creature, speedIn);
        }

        @Override
        public boolean canUse()
        {
            if (!this.forceTrigger)
            {
                if (this.mob.getNoActionTime() >= 100)
                {
                    return false;
                }

                if (this.mob.getRandom().nextInt(this.interval) != 0)
                {
                    return false;
                }
            }

            Vec3 vec3d = this.getPosition();
            if (vec3d == null)
            {
                return false;
            } else
            {
                this.wantedX = vec3d.x;
                this.wantedY = vec3d.y;
                this.wantedZ = vec3d.z;
                this.forceTrigger = false;
                return true;
            }
        }

        @Override
        public boolean canContinueToUse()
        {
            return !this.mob.getNavigation().isDone();
        }
    }

    static class LookAtWithPassenger extends LookAtPlayerGoal
    {
        public LookAtWithPassenger(Mob entityIn, Class<? extends LivingEntity> watchTargetClass, float maxDistance)
        {
            super(entityIn, watchTargetClass, maxDistance);
        }

        @Override
        public boolean canUse()
        {
            return this.mob.getPassengers().isEmpty() && super.canUse();
        }
    }

    public class JumpHelperController extends JumpControl
    {
        private final WolpertingerEntity wolpertinger;
        private boolean canJump;

        public JumpHelperController(WolpertingerEntity _wolpertinger)
        {
            super(_wolpertinger);
            this.wolpertinger = _wolpertinger;
        }

        public boolean getIsJumping()
        {
            return this.jump;
        }

        public boolean canJump()
        {
            return this.canJump;
        }

        public void setCanJump(boolean canJumpIn)
        {
            this.canJump = canJumpIn;
        }

        @Override
        public void tick()
        {
            if (this.jump && !isScared)
            {
                this.wolpertinger.startJumping();
                this.jump = false;
            }
        }
    }

}