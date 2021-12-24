package com.hoopawolf.mwaw.entities;

import com.hoopawolf.mwaw.entities.ai.RangedAttackWithStrafeGoal;
import com.hoopawolf.mwaw.entities.ai.controller.MWAWMovementController;
import com.hoopawolf.mwaw.entities.ai.navigation.MWAWPathNavigateGround;
import com.hoopawolf.mwaw.entities.projectiles.SapEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

public class DendroidEntity extends PathfinderMob implements RangedAttackMob
{
    private static final EntityDataAccessor<Boolean> SHOOTING = SynchedEntityData.defineId(DendroidEntity.class, EntityDataSerializers.BOOLEAN);
    float shootRenderTimer;

    public DendroidEntity(EntityType<? extends DendroidEntity> type, Level worldIn)
    {
        super(type, worldIn);
        shootRenderTimer = 0.0F;
        this.maxUpStep = 1.0F;
        this.moveControl = new MWAWMovementController(this, 30);
    }

    public static AttributeSupplier.Builder func_234321_m_()
    {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 10.0D).add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 12.0D);
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(SHOOTING, false);
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(4, new RangedAttackWithStrafeGoal(this, 1.0D, 40, 50, 10.0F));
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
    protected PathNavigation createNavigation(Level world)
    {
        return new MWAWPathNavigateGround(this, world);
    }

    public boolean isShooting()
    {
        return this.entityData.get(SHOOTING);
    }

    public void setIsShooting(boolean _isShooting)
    {
        if (_isShooting)
            shootRenderTimer = 10.0F;

        this.entityData.set(SHOOTING, _isShooting);
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
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, getOnPos().getX(), getOnPos().getZ());

        return worldIn.canSeeSky(this.getOnPos()) && (int) this.getY() == y;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn)
    {
        this.playSound(this.getStepSound(), 0.15F, 5.0F);
    }

    protected SoundEvent getStepSound()
    {
        return SoundEvents.WOOD_STEP;
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
    protected void customServerAiStep()
    {
        if (isShooting())
        {
            if (shootRenderTimer > 0)
                --shootRenderTimer;
            else
            {
                setIsShooting(false);
            }
        }

    }

    @Override
    public int getMaxSpawnClusterSize()
    {
        return 3;
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

            return super.hurt(source, amount);
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor)
    {
        SapEntity sapentity = new SapEntity(this.level, this);
        double d0 = target.getEyeY() - (double) 1.1F;
        double d1 = target.getX() - this.getX();
        double d2 = d0 - sapentity.getY();
        double d3 = target.getZ() - this.getZ();
        float f = Mth.sqrt((float) (d1 * d1 + d3 * d3)) * 0.2F;
        sapentity.shoot(d1, d2 + (double) f, d3, 1.6F, 12.0F);
        this.playSound(SoundEvents.SLIME_JUMP, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level.addFreshEntity(sapentity);
    }
}