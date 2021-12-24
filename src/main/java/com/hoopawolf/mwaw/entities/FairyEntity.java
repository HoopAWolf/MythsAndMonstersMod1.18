package com.hoopawolf.mwaw.entities;

import com.hoopawolf.mwaw.network.MWAWPacketHandler;
import com.hoopawolf.mwaw.network.packets.client.SpawnParticleMessage;
import com.hoopawolf.mwaw.util.ItemBlockRegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.AirAndWaterRandomPos;
import net.minecraft.world.entity.ai.util.HoverRandomPos;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.UUID;

public class FairyEntity extends Animal implements FlyingAnimal
{
    private static final EntityDataAccessor<Boolean> ANGRY = SynchedEntityData.defineId(FairyEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> RESTING = SynchedEntityData.defineId(FairyEntity.class, EntityDataSerializers.BOOLEAN);
    private final float maxStamina = 1000;
    LinkedList<BlockPos> prevAttraction = new LinkedList<BlockPos>();
    private UUID revengeTargetUUID;
    private BlockPos flowerPos = null;
    private int inWaterTick;
    private float staminaRemaining;
    private int attackTimer;

    public FairyEntity(EntityType<? extends FairyEntity> p_i225714_1_, Level p_i225714_2_)
    {
        super(p_i225714_1_, p_i225714_2_);
        this.moveControl = new FlyingMoveControl(this, 20, true);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.COCOA, -1.0F);
        this.setPathfindingMalus(BlockPathTypes.FENCE, -1.0F);
        this.staminaRemaining = this.maxStamina;
    }

    public static AttributeSupplier.Builder func_234321_m_()
    {
        return Monster.createMonsterAttributes().add(Attributes.FLYING_SPEED, 1.7D).add(Attributes.MAX_HEALTH, 3.0D).add(Attributes.ATTACK_DAMAGE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 12.0D).add(Attributes.MOVEMENT_SPEED, 0.0D);
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob ageable)
    {
        return null;
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(ANGRY, false);
        this.entityData.define(RESTING, false);
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader worldIn)
    {
        return worldIn.getBlockState(pos).isAir() ? 10.0F : 0.0F;
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(0, new FairyEntity.AttackGoal(this, 1.4F, true));
        this.goalSelector.addGoal(1, new FairyEntity.RestingGoal());
        this.goalSelector.addGoal(6, new FairyEntity.GoToFlowerGoal());
        this.goalSelector.addGoal(8, new FairyEntity.WanderGoal());
        this.goalSelector.addGoal(9, new FloatGoal(this));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));

        this.targetSelector.addGoal(1, new FairyEntity.AttackDendroidGoal(this));
        this.targetSelector.addGoal(2, (new FairyEntity.AngerGoal(this)).setAlertOthers());
    }

    public boolean isAngry()
    {
        return entityData.get(ANGRY);
    }

    public void setAngry(boolean angry)
    {
        entityData.set(ANGRY, angry);
    }

    public boolean isResting()
    {
        return entityData.get(RESTING);
    }

    public void setResting(boolean resting)
    {
        entityData.set(RESTING, resting);
    }

    public int getAttackTimer()
    {
        return this.attackTimer;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound)
    {
        super.addAdditionalSaveData(compound);
        if (this.HasFlowerPos())
        {
            compound.put("FlowerPos", NbtUtils.writeBlockPos(this.getFlowerPos()));
        }

        compound.putFloat("StaminaLeft", this.staminaRemaining);

        if (this.revengeTargetUUID != null)
        {
            compound.putString("HurtBy", this.revengeTargetUUID.toString());
        } else
        {
            compound.putString("HurtBy", "");
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound)
    {
        super.readAdditionalSaveData(compound);
        this.flowerPos = null;
        if (compound.contains("FlowerPos"))
        {
            this.flowerPos = NbtUtils.readBlockPos(compound.getCompound("FlowerPos"));
        }

        if (compound.contains("StaminaLeft"))
        {
            this.staminaRemaining = compound.getFloat("StaminaLeft");
        } else
        {
            this.ResetStamina();
        }

        String s = compound.getString("HurtBy");
        if (!s.isEmpty())
        {
            this.revengeTargetUUID = UUID.fromString(s);
            Player playerentity = this.level.getPlayerByUUID(this.revengeTargetUUID);
            this.setLastHurtByMob(playerentity);
            if (playerentity != null)
            {
                this.lastHurtByPlayer = playerentity;
                this.lastHurtByPlayerTime = this.getLastHurtByMobTimestamp();
            }
        }

    }

    @Override
    public int getMaxSpawnClusterSize()
    {
        return 4;
    }

    @Override
    public boolean doHurtTarget(Entity entityIn)
    {
        this.attackTimer = 10;
        this.level.broadcastEntityEvent(this, (byte) 4);

        return entityIn.hurt(new EntityDamageSource("fairy", this), (float) this.getAttribute(Attributes.ATTACK_DAMAGE).getValue());
    }

    @Override
    public void tick()
    {
        super.tick();

        if (this.attackTimer > 0)
        {
            --this.attackTimer;
        }

        if (!this.isResting())
            this.setNoGravity(true);
        else
        {
            this.setNoGravity(false);

            Vec3 vec3d = this.getDeltaMovement();
            if (!this.onGround && vec3d.y < 0.0D)
            {
                this.setDeltaMovement(vec3d.multiply(1.0D, 0.6D, 1.0D));
            }
        }

        if (tickCount % 2 == 0 && !level.isClientSide)
        {
            int _iteration = this.random.nextInt(2);
            Vec3 _vec = new Vec3(this.getX() - (double) 0.3F, this.getY(0.5D), this.getZ() + (double) 0.3F);
            SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(_vec, new Vec3(0, -0.1f, 0), _iteration, 0, getBbWidth());
            MWAWPacketHandler.packetHandler.sendToDimension(this.level.dimension(), spawnParticleMessage);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleEntityEvent(byte id)
    {
        if (id == 4)
        {
            this.attackTimer = 10;
        } else
        {
            super.handleEntityEvent(id);
        }
    }

    private void MoveToPos(BlockPos p_226433_1_)
    {
        if (p_226433_1_ != null)
        {
            Vec3 vec3d = new Vec3(p_226433_1_.getX(), p_226433_1_.getY(), p_226433_1_.getZ());
            int i = 0;
            BlockPos blockpos = this.blockPosition();
            int j = (int) vec3d.y - blockpos.getY();
            if (j > 2)
            {
                i = 4;
            } else if (j < -2)
            {
                i = -4;
            }

            int k = 6;
            int l = 8;
            int i1 = blockpos.distManhattan(p_226433_1_);
            if (i1 < 15)
            {
                k = i1 / 2;
                l = i1 / 2;
            }

            int finalK = k;
            int finalL = l;
            int finalI = i;
            Vec3 vec3d1 = RandomPos.generateRandomPos(this, () ->
                    RandomPos.generateRandomDirectionWithinRadians(this.getRandom(), finalK, finalL, finalI, vec3d.x, vec3d.z, (float) Math.PI / 15F));
            if (vec3d1 != null)
            {
                this.navigation.setSpeedModifier(0.5F);
                this.navigation.moveTo(vec3d1.x, vec3d1.y, vec3d1.z, FairyEntity.this.getAttribute(Attributes.FLYING_SPEED).getBaseValue());
                this.lookControl.setLookAt(new Vec3(getFlowerPos().getX(), getFlowerPos().getY(), getFlowerPos().getZ()));

                if (tickCount % 5 == 0 && !this.level.isClientSide)
                {
                    Vec3 _vec = new Vec3(this.getX() - (double) 0.3F, this.getY(0.5D), this.getZ() + (double) 0.3F);
                    SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(_vec, new Vec3(0, 0, 0), 1, 1, getBbWidth());
                    MWAWPacketHandler.packetHandler.sendToDimension(this.level.dimension(), spawnParticleMessage);
                }
            }
        }
    }

    public BlockPos getFlowerPos()
    {
        return this.flowerPos;
    }

    public boolean HasFlowerPos()
    {
        return this.getFlowerPos() != null;
    }

    @Override
    public void setLastHurtByMob(@Nullable LivingEntity livingBase)
    {
        super.setLastHurtByMob(livingBase);
        if (livingBase != null)
        {
            this.revengeTargetUUID = livingBase.getUUID();
        }
    }

    @Override
    protected void customServerAiStep()
    {
        if (this.isInWaterOrBubble())
        {
            ++this.inWaterTick;
        } else
        {
            this.inWaterTick = 0;
        }

        if (this.inWaterTick > 20)
        {
            this.hurt(DamageSource.DROWN, 1.0F);
        }

        if (!this.isResting())
        {
            if (this.staminaRemaining <= 0.0F)
            {
                this.staminaRemaining = 0.0F;
                this.setResting(true);
            }
        } else
        {
            if (this.staminaRemaining >= this.maxStamina)
            {
                this.ResetStamina();
                this.setResting(false);
            }
        }
    }

    private boolean IsBlockFar(BlockPos p_226437_1_)
    {
        return !this.isWithinDistance(p_226437_1_, 15);
    }

    @Override
    protected PathNavigation createNavigation(Level worldIn)
    {
        FlyingPathNavigation flyingpathnavigator = new FlyingPathNavigation(this, worldIn)
        {
            @Override
            public boolean isStableDestination(BlockPos pos)
            {
                return !this.level.isEmptyBlock(pos.below());
            }

            @Override
            public void tick()
            {
                if (!FairyEntity.this.isResting())
                {
                    super.tick();
                }
            }
        };
        flyingpathnavigator.setCanOpenDoors(false);
        flyingpathnavigator.setCanFloat(false);
        flyingpathnavigator.setCanPassDoors(true);
        return flyingpathnavigator;
    }

    private boolean IsFlowerBlock(BlockPos p_226439_1_)
    {
        return this.level.isLoaded(p_226439_1_) && (this.level.getBlockState(p_226439_1_).is(BlockTags.FLOWERS) || this.level.getBlockState(p_226439_1_).getBlock() == ItemBlockRegistryHandler.FAIRY_MUSHROOM_BLOCK.get());
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn)
    {
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.EXPERIENCE_ORB_PICKUP;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.FIRE_EXTINGUISH;
    }

    @Override
    protected float getSoundVolume()
    {
        return 0.4F;
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn)
    {
        return sizeIn.height * 0.8F;
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource p_148704_)
    {
        return false;
    }

    @Override
    public boolean isFlying()
    {
        return !isResting();
    }

    public boolean SetRevengeTarget(Entity _entity)
    {
        if (_entity instanceof LivingEntity)
        {
            this.setLastHurtByMob((LivingEntity) _entity);
        }

        return true;
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
            if (this.isInvulnerableTo(source))
            {
                return false;
            } else
            {
                Entity entity = source.getEntity();
                this.ResetStamina();
                if (!this.level.isClientSide && entity instanceof Player && !((Player) entity).isCreative() && this.hasLineOfSight(entity) && !this.isNoAi())
                {
                    this.SetRevengeTarget(entity);
                }

                return super.hurt(source, amount);
            }
        }
    }

    @Override
    public MobType getMobType()
    {
        return MobType.UNDEFINED;
    }

    private boolean isWithinDistance(BlockPos pos, int distance)
    {
        return pos.closerThan(this.blockPosition(), distance);
    }

    protected void DecreaseStamina()
    {
        if (random.nextInt(50) < 20)
            --staminaRemaining;
    }

    protected void IncreaseStamina()
    {
        ++staminaRemaining;
    }

    protected void ResetStamina()
    {
        staminaRemaining = maxStamina;
    }

    class AngerGoal extends HurtByTargetGoal
    {
        AngerGoal(FairyEntity p_i225726_2_)
        {
            super(p_i225726_2_);
        }

        @Override
        protected void alertOther(Mob mobIn, LivingEntity targetIn)
        {
            if (mobIn instanceof FairyEntity && this.mob.hasLineOfSight(targetIn) && ((FairyEntity) mobIn).SetRevengeTarget(targetIn))
            {
                mobIn.setTarget(targetIn);
            }
        }
    }

    public class AttackDendroidGoal extends NearestAttackableTargetGoal<DendroidEntity>
    {
        AttackDendroidGoal(FairyEntity p_i225719_1_)
        {
            super(p_i225719_1_, DendroidEntity.class, true);
        }

        @Override
        public void start()
        {
            FairyEntity.this.ResetStamina();
            super.start();
        }

        @Override
        public boolean canContinueToUse()
        {
            if (this.mob.getTarget() != null && this.mob.getTarget() instanceof DendroidEntity)
            {
                return super.canContinueToUse();
            } else
            {
                this.target = null;
                return false;
            }
        }
    }

    public class GoToFlowerGoal extends FairyEntity.PassiveGoal
    {
        private int goToFlowerTimer = FairyEntity.this.level.random.nextInt(10);

        GoToFlowerGoal()
        {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean func_225506_g_()
        {
            return FairyEntity.this.flowerPos != null && FairyEntity.this.IsFlowerBlock(FairyEntity.this.flowerPos) && !FairyEntity.this.isWithinDistance(FairyEntity.this.flowerPos, 2);
        }

        @Override
        public boolean func_225507_h_()
        {
            return this.func_225506_g_();
        }

        @Override
        public void start()
        {
            this.goToFlowerTimer = 0;
            super.start();
        }

        @Override
        public void stop()
        {
            this.goToFlowerTimer = 0;
            FairyEntity.this.navigation.stop();
            FairyEntity.this.navigation.resetMaxVisitedNodesMultiplier();

            if (HasFlowerPos())
            {
                FairyEntity.this.prevAttraction.add(new BlockPos(getFlowerPos()));
                FairyEntity.this.flowerPos = null;
            }

            if (FairyEntity.this.prevAttraction.size() > 10)
            {
                FairyEntity.this.prevAttraction.removeFirst();
            }
        }

        @Override
        public void tick()
        {
            if (FairyEntity.this.flowerPos != null)
            {
                FairyEntity.this.DecreaseStamina();
                ++this.goToFlowerTimer;

                if (this.goToFlowerTimer > 300)
                {
                    FairyEntity.this.prevAttraction.add(new BlockPos(getFlowerPos()));
                    FairyEntity.this.flowerPos = null;

                    if (FairyEntity.this.prevAttraction.size() > 10)
                    {
                        FairyEntity.this.prevAttraction.removeFirst();
                    }

                } else if (!FairyEntity.this.navigation.isInProgress())
                {
                    if (FairyEntity.this.IsBlockFar(FairyEntity.this.flowerPos))
                    {
                        FairyEntity.this.flowerPos = null;
                    } else
                    {
                        FairyEntity.this.MoveToPos(FairyEntity.this.flowerPos);
                    }
                }
            }
        }
    }

    abstract class PassiveGoal extends Goal
    {
        private PassiveGoal()
        {
        }

        public abstract boolean func_225506_g_();

        public abstract boolean func_225507_h_();

        @Override
        public boolean canUse()
        {
            return this.func_225506_g_();
        }

        @Override
        public boolean canContinueToUse()
        {
            return this.func_225507_h_();
        }
    }

    class AttackGoal extends MeleeAttackGoal
    {
        private final FairyEntity parentEntity;

        AttackGoal(FairyEntity p_i225718_2_, double p_i225718_3_, boolean p_i225718_5_)
        {
            super(p_i225718_2_, p_i225718_3_, p_i225718_5_);
            parentEntity = p_i225718_2_;
        }

        @Override
        public void stop()
        {
            this.parentEntity.setAngry(false);
        }

        @Override
        public void tick()
        {
            LivingEntity livingentity = FairyEntity.this.getTarget();
            if (FairyEntity.this.getBoundingBox().intersects(livingentity.getBoundingBox()))
            {
                FairyEntity.this.doHurtTarget(livingentity);
            } else
            {
                Vec3 vec3d = livingentity.getEyePosition(1.0F);
                FairyEntity.this.moveControl.setWantedPosition(vec3d.x, vec3d.y, vec3d.z, FairyEntity.this.getAttribute(Attributes.FLYING_SPEED).getBaseValue() * 2);
            }

            this.parentEntity.setAngry(true);
        }
    }

    class WanderGoal extends Goal
    {
        WanderGoal()
        {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse()
        {
            return FairyEntity.this.navigation.isDone() && FairyEntity.this.random.nextInt(10) == 0 && !FairyEntity.this.isResting();
        }

        @Override
        public boolean canContinueToUse()
        {
            return FairyEntity.this.navigation.isInProgress() && !FairyEntity.this.isResting();
        }

        @Override
        public void start()
        {
            Vec3 vec3d = this.GetNextPosViaForward();
            if (vec3d != null)
            {
                FairyEntity.this.navigation.moveTo(FairyEntity.this.navigation.createPath(new BlockPos(vec3d), 1), FairyEntity.this.getAttribute(Attributes.FLYING_SPEED).getBaseValue());
            }

        }

        @Override
        public void tick()
        {
            FairyEntity.this.DecreaseStamina();

            if (!FairyEntity.this.HasFlowerPos())
            {
                int bound = 7;
                int yBound = 3;
                for (int y = -yBound; y < 0; ++y)
                {
                    for (int x = -bound; x < bound; ++x)
                    {
                        for (int z = -bound; z < bound; ++z)
                        {
                            boolean getOutFlag = false;
                            BlockPos pos = new BlockPos(FairyEntity.this.getX() + x, FairyEntity.this.getY() + y, FairyEntity.this.getZ() + z);

                            for (BlockPos _blockPos : FairyEntity.this.prevAttraction)
                            {
                                if (_blockPos != null && _blockPos.equals(pos))
                                {
                                    getOutFlag = true;
                                    break;
                                }
                            }

                            if (getOutFlag)
                            {
                                break;
                            }

                            if (IsFlowerBlock(pos))
                            {
                                FairyEntity.this.flowerPos = pos;
                                break;
                            }
                        }

                        if (FairyEntity.this.getFlowerPos() != null)
                        {
                            break;
                        }
                    }

                    if (FairyEntity.this.getFlowerPos() != null)
                    {
                        break;
                    }
                }
            }
        }

        private Vec3 GetNextPosViaForward()
        {
            Vec3 vec3 = FairyEntity.this.getViewVector(0.0F);

            Vec3 vec32 = HoverRandomPos.getPos(FairyEntity.this, 8, 7, vec3.x, vec3.z, ((float)Math.PI / 2F), 3, 1);
            return vec32 != null ? vec32 : AirAndWaterRandomPos.getPos(FairyEntity.this, 8, 4, -2, vec3.x, vec3.z, (double)((float)Math.PI / 2F));
        }
    }

    class RestingGoal extends Goal
    {

        @Override
        public boolean canUse()
        {
            return FairyEntity.this.isResting();
        }

        @Override
        public boolean canContinueToUse()
        {
            return FairyEntity.this.isResting();
        }

        @Override
        public void start()
        {
            FairyEntity.this.navigation.stop();
            super.start();
        }

        @Override
        public void tick()
        {
            FairyEntity.this.IncreaseStamina();
        }
    }
}
//TODO Ride fairy jar entity. Have a hunger state if is riding jar. if hungry, becoe ANGERY. try to break jar. get riding on and damage. right click jar to get item. right click jar with sugar to calm the fairy hunger. Fairy pet maybe