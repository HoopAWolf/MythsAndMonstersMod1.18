package com.hoopawolf.mwaw.entities;

import com.hoopawolf.mwaw.entities.ai.MWAWMeleeAttackGoal;
import com.hoopawolf.mwaw.entities.ai.RangedAttackWithStrafeGoal;
import com.hoopawolf.mwaw.entities.ai.controller.MWAWMovementController;
import com.hoopawolf.mwaw.entities.ai.navigation.MWAWPathNavigateGround;
import com.hoopawolf.mwaw.entities.projectiles.ClayEntity;
import com.hoopawolf.mwaw.network.MWAWPacketHandler;
import com.hoopawolf.mwaw.network.packets.client.SpawnParticleMessage;
import com.hoopawolf.mwaw.util.EntityRegistryHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class ClayGolemEntity extends PathfinderMob implements Enemy, RangedAttackMob
{
    private static final EntityDataAccessor<Boolean> HARDEN_FORM = SynchedEntityData.defineId(ClayGolemEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> BURN_TIME = SynchedEntityData.defineId(ClayGolemEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> IS_MINION = SynchedEntityData.defineId(ClayGolemEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> CLAYGOLEM_PHASE = SynchedEntityData.defineId(ClayGolemEntity.class, EntityDataSerializers.INT);

    private MWAWMeleeAttackGoal meleeGoal;
    private RangedAttackWithStrafeGoal rangedGoal;

    private boolean resized;
    private int attackTimer;
    private boolean spawned;

    public ClayGolemEntity(EntityType<? extends ClayGolemEntity> type, Level worldIn)
    {
        super(type, worldIn);

        this.maxUpStep = 1.0F;
        resized = false;
        spawned = false;

        this.moveControl = new MWAWMovementController(this, 30);
    }

    public static AttributeSupplier.Builder func_234321_m_()
    {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 250.0D).add(Attributes.ATTACK_DAMAGE, 6.0D).add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 12.0D).add(Attributes.MOVEMENT_SPEED, 0.15D);
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(HARDEN_FORM, false);
        this.entityData.define(IS_MINION, false);
        this.entityData.define(BURN_TIME, 0.0F);
        this.entityData.define(CLAYGOLEM_PHASE, 5);
    }

    @Override
    protected void registerGoals()
    {
        meleeGoal = new MWAWMeleeAttackGoal(this, 1.0D, true);
        rangedGoal = new RangedAttackWithStrafeGoal(this, 1.0D, 40, 50, 15.0F);

        this.goalSelector.addGoal(1, meleeGoal);
        this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 1.0D, 32.0F));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 4.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, PathfinderMob.class, 4.0F));

        this.targetSelector.addGoal(2, new NearestAttackableTargetGolemGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGolemGoal<>(this, PathfinderMob.class, 10, true, false, (p_213621_0_) ->
        {
            return !(p_213621_0_ instanceof ClayGolemEntity);
        }));
    }

    @Override
    protected PathNavigation createNavigation(Level world)
    {
        return new MWAWPathNavigateGround(this, world);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound)
    {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("IsMinion", this.isMinion());
        compound.putBoolean("IsHarden", this.isHardenForm());
        compound.putInt("GolemPhase", this.getPhase());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound)
    {
        super.readAdditionalSaveData(compound);
        if(compound.contains("IsMinion"))
        {
            this.setMinion(compound.getBoolean("IsMinion"));
        }

        if(compound.contains("IsHarden"))
        {
            this.setHardenForm(compound.getBoolean("IsHarden"));
        }

        if(compound.contains("GolemPhase"))
        {
            this.setPhase(compound.getInt("GolemPhase"));
        }
    }

    @Override
    public float getScale()
    {
        return this.isMinion() ? 0.5F : 1.0F;
    }

    public boolean isHardenForm()
    {
        return this.entityData.get(HARDEN_FORM);
    }

    public void setHardenForm(boolean _isHardenForm)
    {
        if (!level.isClientSide && _isHardenForm)
        {
            this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(this.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue() * 0.8D);
        }

        this.entityData.set(HARDEN_FORM, _isHardenForm);
    }

    public boolean isMinion()
    {
        return this.entityData.get(IS_MINION);
    }

    public void setMinion(boolean _isMinion)
    {
        if (_isMinion)
        {
            if (!level.isClientSide)
            {
                this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
                this.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(0.2D);
                this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(1.0D);

                this.goalSelector.removeGoal(meleeGoal);
                this.goalSelector.removeGoal(rangedGoal);

                this.goalSelector.addGoal(1, rangedGoal);
            }
        }

        this.entityData.set(IS_MINION, _isMinion);
    }

    public float getBurnTime()
    {
        return this.entityData.get(BURN_TIME);
    }

    public void setBurnTime(float _burntime)
    {
        this.entityData.set(BURN_TIME, _burntime);
    }

    public int getPhase()
    {
        return this.entityData.get(CLAYGOLEM_PHASE);
    }

    public void setPhase(int _phase)
    {
        this.entityData.set(CLAYGOLEM_PHASE, _phase);
    }

    public int getAttackTimer()
    {
        return this.attackTimer;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleEntityEvent(byte id)
    {
        if (id == 4)
        {
            this.attackTimer = 10;
            this.playSound(isHardenForm() ? SoundEvents.STONE_PLACE : SoundEvents.HONEY_BLOCK_PLACE, 1.0F, 1.0F);
        } else
        {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public int getMaxSpawnClusterSize()
    {
        return 1;
    }

    @Override
    public boolean causeFallDamage(float distance, float damageMultiplier, DamageSource p_148877_)
    {
        return false;
    }

    @Override
    protected boolean shouldDespawnInPeaceful()
    {
        return true;
    }

    @Override
    protected int decreaseAirSupply(int air)
    {
        return air;
    }

    @Override
    public MobType getMobType()
    {
        return MobType.UNDEFINED;
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn)
    {
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, blockPosition().getX(), blockPosition().getZ());

        return worldIn.canSeeSky(this.blockPosition()) && (int) this.getY() == y;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn)
    {
        this.playSound(this.getStepSound(), 0.15F, 1.0F);
    }

    protected SoundEvent getStepSound()
    {
        return isHardenForm() ? SoundEvents.STONE_STEP : SoundEvents.HONEY_BLOCK_STEP;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return isHardenForm() ? SoundEvents.STONE_BREAK : SoundEvents.HONEY_BLOCK_BREAK;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return isHardenForm() ? SoundEvents.STONE_HIT : SoundEvents.HONEY_BLOCK_HIT;
    }

    @Override
    public void tick()
    {
        super.tick();

        if (this.attackTimer > 0)
        {
            --this.attackTimer;
        }

        if (isMinion() && !resized)
        {
            this.refreshDimensions();
            resized = true;
        }

        if (!level.isClientSide)
        {
            if (!isHardenForm() && getBurnTime() > 1.0F)
            {
                setHardenForm(true);
            }

            if (this.getDeltaMovement().y() > 0)
            {
                this.setDeltaMovement(this.getDeltaMovement().x(), this.getDeltaMovement().y() * 0.1F, this.getDeltaMovement().z());
            }

            if (getHealth() != getMaxHealth() && this.level.getBlockState(this.getOnPos()) == Blocks.CLAY.defaultBlockState())
            {
                if (tickCount % 10 == 0)
                {
                    heal(1.0F);
                    Vec3 _vec = new Vec3(this.getX() - (double) 0.3F, this.getY(1.0D), this.getZ() + (double) 0.3F);
                    SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(_vec, new Vec3(0, -0.5D, 0), 4, 5, getBbWidth());
                    MWAWPacketHandler.packetHandler.sendToDimension(this.level.dimension(), spawnParticleMessage);
                }
            }

            if (!isMinion())
            {
                if (!spawned && (getPhase() == 5 && getHealth() <= 200 || getPhase() == 4 && getHealth() <= 150 || getPhase() == 3 && getHealth() <= 100 || getPhase() == 2 && getHealth() <= 50 || getPhase() == 1 && getHealth() <= 0))
                {
                    spawned = true;
                    setPhase(getPhase() - 1);
                    this.playSound(SoundEvents.HONEY_BLOCK_BREAK, 4.0F, 10.0F);

                    for (int i = 0; i < 5; ++i)
                    {
                        ClayGolemEntity golemMinion = EntityRegistryHandler.CLAY_GOLEM_ENTITY.get().create(level);
                        golemMinion.setMinion(true);
                        golemMinion.moveTo(this.getX(), this.getY() + 1.5D, this.getZ(), 0.0F, 0.0F);
                        golemMinion.setDeltaMovement(random.nextDouble() - random.nextDouble(), random.nextDouble(), random.nextDouble() - random.nextDouble());
                        golemMinion.setTarget(this.getTarget());
                        level.addFreshEntity(golemMinion);
                    }
                }

                if (getHealth() <= 50.0F)
                {
                    if (random.nextInt(100) < 10)
                    {
                        for (int x = -1; x <= 1; ++x)
                        {
                            for (int z = -1; z <= 1; ++z)
                            {
                                BlockPos blockPos = new BlockPos(blockPosition().getX() + x, blockPosition().getY(), blockPosition().getZ() + z);

                                if (this.level.getBlockState(blockPos).is(BlockTags.ENDERMAN_HOLDABLE))
                                {
                                    this.level.setBlockAndUpdate(blockPos, Blocks.CLAY.defaultBlockState());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void customServerAiStep()
    {
        if (this.getTarget() != null && !this.getTarget().isAlive())
        {
            this.setTarget(null);
        }
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

            if (spawned)
            {
                spawned = false;
            }

            if (source.getEntity() instanceof ClayGolemEntity)
                return false;

            if (source.msgId.equals(DamageSource.ON_FIRE.msgId))
            {
                if (!isHardenForm())
                {
                    this.setSecondsOnFire(100);
                    setBurnTime(getBurnTime() + 0.1F);
                } else
                {
                    this.clearFire();
                    return false;
                }
            } else if (isHardenForm() && source.msgId.equals(DamageSource.IN_FIRE.msgId))
            {
                return false;
            }

            return isHardenForm() ? super.hurt(source, amount * 0.5F) : super.hurt(source, amount);
        }
    }

    @Override
    public boolean doHurtTarget(Entity entityIn)
    {
        this.attackTimer = 10;
        this.level.broadcastEntityEvent(this, (byte) 4);

        return super.doHurtTarget(entityIn);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor)
    {
        this.attackTimer = 10;
        this.level.broadcastEntityEvent(this, (byte) 4);
        ClayEntity clayentity = new ClayEntity(this.level, this);
        double d0 = target.getEyeY() - (double) 1.1F;
        double d1 = target.getX() - this.getX();
        double d2 = d0 - clayentity.getY();
        double d3 = target.getX() - this.getX();
        float f = Mth.sqrt((float) (d1 * d1 + d3 * d3)) * 0.2F;
        clayentity.shoot(d1, d2 + (double) f, d3, 1.6F, 12.0F);
        this.playSound(SoundEvents.HONEY_BLOCK_PLACE, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level.addFreshEntity(clayentity);
    }

    private class NearestAttackableTargetGolemGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T>
    {

        public NearestAttackableTargetGolemGoal(Mob goalOwnerIn, Class targetClassIn, boolean checkSight)
        {
            super(goalOwnerIn, targetClassIn, checkSight);
        }

        public NearestAttackableTargetGolemGoal(Mob goalOwnerIn, Class<T> targetClassIn, int targetChanceIn, boolean checkSight, boolean nearbyOnlyIn, @Nullable Predicate<LivingEntity> targetPredicate)
        {
            super(goalOwnerIn, targetClassIn, targetChanceIn, checkSight, nearbyOnlyIn, targetPredicate);
        }

        @Override
        public boolean canUse()
        {
            return getTarget() == null && super.canUse();
        }
    }
}
