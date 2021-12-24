package com.hoopawolf.mwaw.entities;

import com.hoopawolf.mwaw.entities.ai.MWAWMeleeAttackGoal;
import com.hoopawolf.mwaw.entities.ai.controller.MWAWMovementController;
import com.hoopawolf.mwaw.entities.ai.navigation.MWAWPathNavigateGround;
import com.hoopawolf.mwaw.entities.helper.EntityHelper;
import com.hoopawolf.mwaw.network.MWAWPacketHandler;
import com.hoopawolf.mwaw.network.packets.client.SpawnParticleMessage;
import com.hoopawolf.mwaw.util.EntityRegistryHandler;
import com.hoopawolf.mwaw.util.ItemBlockRegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.IForgeShearable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

public class GoldenRamEntity extends PathfinderMob implements IForgeShearable
{

    private static final EntityDataAccessor<Boolean> SHEARED = SynchedEntityData.defineId(GoldenRamEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ANGRY = SynchedEntityData.defineId(GoldenRamEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> TIME_SHAKING = SynchedEntityData.defineId(GoldenRamEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> PREV_TIME_SHAKING = SynchedEntityData.defineId(GoldenRamEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> TIME_REARING = SynchedEntityData.defineId(GoldenRamEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> PREV_TIME_REARING = SynchedEntityData.defineId(GoldenRamEntity.class, EntityDataSerializers.FLOAT);

    private static final Predicate<Entity> IS_PREY = (p_213498_0_) ->
    {
        return p_213498_0_ instanceof DendroidEntity;
    };

    private int sheepTimer,
            shakeCoolDown,
            ramingCoolDown;
    private EatGrassRamGoal eatGrassGoal;
    private boolean isShaking, isRamming;

    public GoldenRamEntity(EntityType<? extends GoldenRamEntity> type, Level worldIn)
    {
        super(type, worldIn);
        shakeCoolDown = 0;
        ramingCoolDown = 0;
        this.maxUpStep = 1.0F;
        this.moveControl = new MWAWMovementController(this, 30);
    }

    public static AttributeSupplier.Builder func_234321_m_()
    {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 70.0D).add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.FOLLOW_RANGE, 12.0D).add(Attributes.MOVEMENT_SPEED, 0.3D);
    }

    @Override
    protected void registerGoals()
    {
        this.eatGrassGoal = new EatGrassRamGoal(this);

        this.targetSelector.addGoal(3, (new HurtByTargetGoal(this)).setAlertOthers());
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, Mob.class, 10, false, false, IS_PREY));

        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new ShakeFairyGoal(this));
        this.goalSelector.addGoal(3, new RammingGoal(this));
        this.goalSelector.addGoal(4, new LeapAtTargetRamGoal(this, 0.4F));
        this.goalSelector.addGoal(5, new MeleeRamGoal(this, 1.0D, true));
        this.goalSelector.addGoal(5, this.eatGrassGoal);
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtRamGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(8, new LookAtRandomRamGoal(this));
    }

    @Override
    protected PathNavigation createNavigation(Level world)
    {
        return new MWAWPathNavigateGround(this, world);
    }

    @Override
    protected void customServerAiStep()
    {
        this.sheepTimer = this.eatGrassGoal.getEatAnimationTick();

        if (tickCount % 2 == 0)
        {
            if (shakeCoolDown > 0)
            {
                --shakeCoolDown;
            }

            if (ramingCoolDown > 0)
            {
                --ramingCoolDown;
            }
        }

        super.customServerAiStep();
    }

    @Override
    public void aiStep()
    {
        super.aiStep();

        if (this.level.isClientSide)
        {
            this.sheepTimer = Math.max(0, this.sheepTimer - 1);
        }

        if (!this.level.isClientSide)
        {
            if (this.getTarget() == null && this.isAngry())
            {
                this.setAngry(false);
            } else if (this.getTarget() != null && !this.isAngry())
            {
                this.setAngry(true);
            }

            if (isShaking || isRamming)
            {
                jumping = false;
                xxa = 0.0F;
                zza = 0.0F;
                navigation.stop();
            } else
            {
                if (this.getRearTime() > 0)
                {
                    this.setRearTime(this.getRearTime() + (0.8F * this.getRearTime() * this.getRearTime() * this.getRearTime() - this.getRearTime()) * 0.8F - 0.05F);
                } else if (this.getRearTime() < 0)
                {
                    this.setRearTime(0.0F);
                }
            }

            if (getHealth() <= (getMaxHealth() * 0.5F) && tickCount % 5 == 0)
            {
                List<LivingEntity> entities = EntityHelper.getEntityLivingBaseNearby(this, 10, 3, 10, 10);
                for (LivingEntity entity : entities)
                {
                    if (entity instanceof Animal && ((Animal) entity).getTarget() == null)
                    {
                        ((Animal) entity).setTarget(this.getTarget());
                    }
                }
            }
        }
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(SHEARED, false);
        this.entityData.define(ANGRY, false);
        this.entityData.define(TIME_SHAKING, 0.0F);
        this.entityData.define(PREV_TIME_SHAKING, 0.0F);
        this.entityData.define(TIME_REARING, 0.0F);
        this.entityData.define(PREV_TIME_REARING, 0.0F);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleEntityEvent(byte id)
    {
        if (id == 10)
        {
            this.sheepTimer = 40;
        } else if (id == 8)
        {
            this.isShaking = true;
        } else
        {
            super.handleEntityEvent(id);
        }
    }

    public boolean isAngry()
    {
        return this.entityData.get(ANGRY);
    }

    public void setAngry(boolean angry)
    {
        this.entityData.set(ANGRY, angry);
    }

    public float getPrevRearTime()
    {
        return this.entityData.get(PREV_TIME_REARING);
    }

    public void setPrevRearTime(float _prevreartime)
    {
        this.entityData.set(PREV_TIME_REARING, _prevreartime);
    }

    public float getRearTime()
    {
        return this.entityData.get(TIME_REARING);
    }

    public void setRearTime(float _reartime)
    {
        this.entityData.set(TIME_REARING, _reartime);
    }

    public float getShakeTime()
    {
        return this.entityData.get(TIME_SHAKING);
    }

    public void setShakeTime(float _shaketime)
    {
        this.entityData.set(TIME_SHAKING, _shaketime);
    }

    public float getPrevShakeTime()
    {
        return this.entityData.get(PREV_TIME_SHAKING);
    }

    public void setPrevShakeTime(float _prevshaketime)
    {
        this.entityData.set(PREV_TIME_SHAKING, _prevshaketime);
    }

    @OnlyIn(Dist.CLIENT)
    public float getHeadRotationPointY(float p_70894_1_)
    {
        if (this.sheepTimer <= 0)
        {
            return 0.0F;
        } else if (this.sheepTimer >= 4 && this.sheepTimer <= 36)
        {
            return 1.0F;
        } else
        {
            return this.sheepTimer < 4 ? ((float) this.sheepTimer - p_70894_1_) / 4.0F : -((float) (this.sheepTimer - 40) - p_70894_1_) / 4.0F;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public float getHeadRotationAngleX(float p_70890_1_)
    {
        if (this.sheepTimer > 4 && this.sheepTimer <= 36)
        {
            float f = ((float) (this.sheepTimer - 4) - p_70890_1_) / 32.0F;
            return ((float) Math.PI / 5F) + 0.21991149F * Mth.sin(f * 28.7F);
        } else
        {
            return this.sheepTimer > 0 ? ((float) Math.PI / 5F) : this.getXRot() * ((float) Math.PI / 180F);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public float getShakeAngle(float p_70923_1_, float p_70923_2_)
    {
        float f = (Mth.lerp(p_70923_1_, this.getPrevShakeTime(), this.getShakeTime()) + p_70923_2_) / 1.8F;
        if (f < 0.0F)
        {
            f = 0.0F;
        } else if (f > 1.0F)
        {
            f = 1.0F;
        }

        return Mth.sin(f * (float) Math.PI) * Mth.sin(f * (float) Math.PI * 11.0F) * 0.15F * (float) Math.PI;
    }

    @OnlyIn(Dist.CLIENT)
    public float getRearingAmount(float p_110223_1_)
    {
        return Mth.lerp(p_110223_1_, this.getPrevRearTime(), this.getRearTime());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound)
    {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Sheared", this.getSheared());
        compound.putBoolean("Angry", this.isAngry());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound)
    {
        super.readAdditionalSaveData(compound);

        if(compound.contains("Sheared"))
        {
            this.setSheared(compound.getBoolean("Sheared"));
        }

        if(compound.contains("Angry"))
        {
            this.setAngry(compound.getBoolean("Angry"));
        }
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.SHEEP_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.SHEEP_HURT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.SHEEP_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn)
    {
        this.playSound(SoundEvents.SHEEP_STEP, 0.15F, 1.0F);
    }

    public boolean getSheared()
    {
        return this.entityData.get(SHEARED);
    }

    public void setSheared(boolean sheared)
    {
        this.entityData.set(SHEARED, sheared);
    }

    @Override
    public void ate()
    {
        this.setSheared(false);
        heal(10.0F);

        if (!level.isClientSide)
        {
            for (int j = 0; j < 10; ++j)
            {
                SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(new Vec3(GoldenRamEntity.this.getX(), GoldenRamEntity.this.getY() + GoldenRamEntity.this.getEyeY(), GoldenRamEntity.this.getZ()),
                        new Vec3(0.0f, -0.1f, 0.0f), 4, 6, getBbWidth());
                MWAWPacketHandler.packetHandler.sendToDimension(GoldenRamEntity.this.level.dimension(), spawnParticleMessage);
            }
        }
    }

    @Override
    public void die(DamageSource cause)
    {
        this.isShaking = false;
        this.setPrevShakeTime(0.0F);
        this.setShakeTime(0.0F);
        super.die(cause);
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn)
    {
        return 0.95F * sizeIn.height;
    }

    @Override
    public boolean isShearable(@Nonnull ItemStack item, Level world, BlockPos pos)
    {
        return !this.getSheared();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        if (!getSheared() && player.getInventory().getSelected().getItem().equals(Items.SHEARS))
        {
            this.setTarget(player);
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public List<ItemStack> onSheared(@Nullable Player player, @Nonnull ItemStack item, Level world, BlockPos pos, int fortune)
    {
        java.util.List<ItemStack> ret = new java.util.ArrayList<>();

        if (!this.level.isClientSide)
        {
            this.setSheared(true);
            ret.add(new ItemStack(ItemBlockRegistryHandler.GOLDEN_BOW.get()));//TODO CHANGE TO GOLDEN FLECE LAMO
        }

        this.playSound(SoundEvents.SHEEP_SHEAR, 1.0F, 1.0F);

        return ret;
    }

    private class ShakeFairyGoal extends Goal
    {
        private final GoldenRamEntity entity;
        private boolean spawned = false;

        public ShakeFairyGoal(GoldenRamEntity _entity)
        {
            entity = _entity;
        }

        @Override
        public boolean canUse()
        {
            return random.nextInt(100) < 20 && shakeCoolDown <= 0 && !entity.isShaking &&
                    !entity.getSheared() && !entity.isRamming && entity.getTarget() != null && entity.getHealth() <= (entity.getMaxHealth() * 0.5f);
        }

        @Override
        public boolean canContinueToUse()
        {
            return entity.isShaking && entity.getTarget() != null;
        }

        @Override
        public void start()
        {
            entity.isShaking = true;
            spawned = false;
            entity.setShakeTime(0.0F);
            entity.setPrevShakeTime(0.0F);
            entity.level.broadcastEntityEvent(entity, (byte) 8);
        }

        @Override
        public void stop()
        {
            entity.shakeCoolDown = 400;
            entity.isShaking = false;
            entity.setPrevShakeTime(0.0F);
            entity.setShakeTime(0.0F);
        }

        @Override
        public void tick()
        {
            if (entity.getShakeTime() == 0.0F)
            {
                entity.playSound(SoundEvents.WOLF_SHAKE, entity.getSoundVolume(), (entity.random.nextFloat() - entity.random.nextFloat()) * 0.2F + 1.0F);
            }

            entity.setPrevShakeTime(entity.getShakeTime());
            entity.setShakeTime(entity.getShakeTime() + 0.05F);
            if (entity.getPrevShakeTime() >= 2.0F)
            {
                if (!spawned)
                {
                    for (int i = 0; i < random.nextInt(3) + 1; ++i)
                    {
                        FairyEntity fairy = EntityRegistryHandler.FAIRY_ENTITY.get().create(level);
                        fairy.moveTo(entity.getX(), entity.getY() + 0.5D, entity.getZ(), 0.0F, 0.0F);
                        fairy.setDeltaMovement(random.nextDouble() - random.nextDouble(), 0.1F, random.nextDouble() - random.nextDouble());
                        fairy.setTarget(entity.getTarget());
                        level.addFreshEntity(fairy);
                    }
                }

                entity.shakeCoolDown = 200;
                entity.isShaking = false;
                entity.setPrevShakeTime(0.0F);
                entity.setShakeTime(0.0F);
            }

            if (entity.getPrevShakeTime() == 1.15F && !spawned)
            {
                for (int i = 0; i < random.nextInt(3) + 1; ++i)
                {
                    FairyEntity fairy = EntityRegistryHandler.FAIRY_ENTITY.get().create(level);
                    fairy.moveTo(entity.getX(), entity.getY() + 0.5D, entity.getZ(), 0.0F, 0.0F);
                    fairy.setDeltaMovement(random.nextDouble() - random.nextDouble(), 0.1F, random.nextDouble() - random.nextDouble());
                    fairy.setTarget(entity.getTarget());
                    level.addFreshEntity(fairy);
                }
                spawned = true;
            }

            if (entity.getShakeTime() > 0.4F)
            {
                int i = (int) (Mth.sin((entity.getShakeTime() - 0.4F) * (float) Math.PI) * 7.0F);

                for (int j = 0; j < i; ++j)
                {
                    SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(new Vec3(entity.getX(), entity.getY() + entity.getEyeY(), entity.getZ()),
                            new Vec3((random.nextDouble() - random.nextDouble()) * 0.3F, -0.1f, (random.nextDouble() - random.nextDouble()) * 0.3F), 4, 6, getBbWidth());
                    MWAWPacketHandler.packetHandler.sendToDimension(entity.level.dimension(), spawnParticleMessage);
                }
            }
        }
    }

    private class RammingGoal extends Goal
    {
        private final GoldenRamEntity entity;
        private boolean isRearing, isRamming;
        private Vec3 motion;
        private int timer;

        public RammingGoal(GoldenRamEntity _entity)
        {
            entity = _entity;
        }

        @Override
        public boolean canUse()
        {
            return random.nextInt(100) < 40 && ramingCoolDown <= 0 && !entity.isShaking && !entity.isRamming
                    && entity.onGround && entity.getTarget() != null;
        }

        @Override
        public boolean canContinueToUse()
        {
            return entity.isRamming && entity.getTarget() != null;
        }

        @Override
        public void start()
        {
            entity.isRamming = true;
            isRamming = false;
            isRearing = true;
            timer = 0;
        }

        @Override
        public void stop()
        {
            entity.setDeltaMovement(0.0F, entity.getDeltaMovement().y(), 0.0F);
            entity.isRamming = false;
            entity.ramingCoolDown = 100;
            entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
        }

        @Override
        public void tick()
        {
            entity.setPrevRearTime(entity.getRearTime());
            entity.getLookControl().setLookAt(entity.getTarget().getX(), entity.getTarget().getEyeY(), entity.getTarget().getZ());
            entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);

            if (isRearing)
            {
                double d2 = entity.getTarget().getX() - entity.getX();
                double d1 = entity.getTarget().getZ() - entity.getZ();
                entity.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
                entity.yBodyRot = entity.getYRot();

                entity.setRearTime(entity.getRearTime() + (1.0F - entity.getRearTime()) * 0.2F + 0.05F);
                if (entity.getRearTime() > 1.0F)
                {
                    entity.setRearTime(1.0F);
                    isRearing = false;
                }
            } else if (!isRamming)
            {
                entity.setRearTime(entity.getRearTime() + (0.8F * entity.getRearTime() * entity.getRearTime() * entity.getRearTime() - entity.getRearTime()) * 0.8F - 0.05F);
                double d2 = entity.getTarget().getX() - entity.getX();
                double d1 = entity.getTarget().getZ() - entity.getZ();
                entity.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
                entity.yBodyRot = entity.getYRot();

                if (entity.getRearTime() < 0.0F)
                {
                    entity.setRearTime(0.0F);
                    isRamming = true;

                    if (entity.getTarget() != null)
                    {
                        Vec3 dir = entity.getTarget().position().subtract(entity.position()).normalize();
                        motion = new Vec3(dir.x * 1.5F, dir.y, dir.z * 1.5F);

                        for (int i = 1; i <= 180; ++i)
                        {
                            double yaw = i * 360 / 180;
                            double speed = 5.5;
                            double xSpeed = speed * Math.cos(Math.toRadians(yaw));
                            double zSpeed = speed * Math.sin(Math.toRadians(yaw));

                            SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(new Vec3(getX(), getY() + 0.1F, getZ()), new Vec3(xSpeed, 0.0D, zSpeed), 3, 2, 0.0F);
                            MWAWPacketHandler.packetHandler.sendToDimension(entity.level.dimension(), spawnParticleMessage);
                        }

                        entity.playSound(SoundEvents.PUFFER_FISH_BLOW_OUT, 3.0F, 0.1F);
                    }
                }
            } else if (isRamming)
            {
                if (entity.tickCount % 2 == 0)
                {
                    ++timer;
                }

                entity.setDeltaMovement(entity.getDeltaMovement().add(motion));

                LivingEntity livingentity = entity.getTarget();
                if (livingentity != null && entity.getBoundingBox().intersects(livingentity.getBoundingBox().inflate(1.0D)))
                {
                    livingentity.hurt(new DamageSource("goldenram"), (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue() * 2);

                    livingentity.setDeltaMovement(livingentity.getDeltaMovement().add(motion.multiply(2.0D, 2.0D, 2.0D)));
                    entity.setDeltaMovement(0.0F, entity.getDeltaMovement().y(), 0.0F);
                    entity.isRamming = false;
                    entity.ramingCoolDown = 100;
                    entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
                    entity.playSound(SoundEvents.ANVIL_LAND, 1.0F, 0.1F);
                }

                if (!level.isClientSide)
                {
                    for (int j = 0; j < 10; ++j)
                    {
                        SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(new Vec3(GoldenRamEntity.this.getX(), GoldenRamEntity.this.getY() + GoldenRamEntity.this.getEyeY(), GoldenRamEntity.this.getZ()),
                                new Vec3(0.0f, -0.1f, 0.0f), 4, 6, getBbWidth());
                        MWAWPacketHandler.packetHandler.sendToDimension(GoldenRamEntity.this.level.dimension(), spawnParticleMessage);
                    }
                }

                if (timer >= 5 || (int) entity.xo == (int) entity.getX() && (int) entity.zo == (int) entity.getZ() && entity.horizontalCollision || !onGround)
                {
                    entity.setDeltaMovement(0.0F, entity.getDeltaMovement().y(), 0.0F);
                    entity.isRamming = false;
                    entity.ramingCoolDown = 100;
                    entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);

                }
            }
        }
    }

    private class EatGrassRamGoal extends EatBlockGoal
    {
        public EatGrassRamGoal(Mob grassEaterEntityIn)
        {
            super(grassEaterEntityIn);
        }

        @Override
        public boolean canUse()
        {
            return GoldenRamEntity.this.getTarget() == null && super.canUse();
        }
    }

    private class LeapAtTargetRamGoal extends LeapAtTargetGoal
    {

        public LeapAtTargetRamGoal(Mob leapingEntity, float leapMotionYIn)
        {
            super(leapingEntity, leapMotionYIn);
        }

        @Override
        public boolean canUse()
        {
            return !GoldenRamEntity.this.isShaking && !GoldenRamEntity.this.isRamming && super.canUse();
        }
    }

    private class MeleeRamGoal extends MWAWMeleeAttackGoal
    {
        public MeleeRamGoal(PathfinderMob creature, double speedIn, boolean useLongMemory)
        {
            super(creature, speedIn, useLongMemory);
        }

        @Override
        public boolean canUse()
        {
            return !GoldenRamEntity.this.isShaking && !GoldenRamEntity.this.isRamming && super.canUse();
        }
    }

    private class LookAtRamGoal extends LookAtPlayerGoal
    {
        public LookAtRamGoal(Mob entityIn, Class<? extends LivingEntity> watchTargetClass, float maxDistance)
        {
            super(entityIn, watchTargetClass, maxDistance);
        }

        @Override
        public boolean canUse()
        {
            return !GoldenRamEntity.this.isShaking && !GoldenRamEntity.this.isRamming && super.canUse();
        }
    }

    private class LookAtRandomRamGoal extends RandomLookAroundGoal
    {
        public LookAtRandomRamGoal(Mob entitylivingIn)
        {
            super(entitylivingIn);
        }

        @Override
        public boolean canUse()
        {
            return !GoldenRamEntity.this.isShaking && !GoldenRamEntity.this.isRamming && super.canUse();
        }
    }
}
