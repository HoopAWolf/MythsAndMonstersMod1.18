package com.hoopawolf.mwaw.entities;

import com.hoopawolf.mwaw.entities.ai.MWAWMeleeAttackGoal;
import com.hoopawolf.mwaw.entities.ai.controller.MWAWMovementController;
import com.hoopawolf.mwaw.entities.ai.navigation.MWAWPathNavigateGround;
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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class JackalopeEntity extends Animal
{
    private static final EntityDataAccessor<Boolean> ANGRY = SynchedEntityData.defineId(JackalopeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> ESCAPE = SynchedEntityData.defineId(JackalopeEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Float> ESCAPE_TIMER = SynchedEntityData.defineId(JackalopeEntity.class, EntityDataSerializers.FLOAT);
    private int ramingCoolDown,
            escapeCoolDown;
    private boolean isRamming;
    private Vec3 attackedPos;

    public JackalopeEntity(EntityType<? extends JackalopeEntity> type, Level worldIn)
    {
        super(type, worldIn);
        ramingCoolDown = 0;
        escapeCoolDown = 0;
        this.maxUpStep = 1.0F;
        attackedPos = null;
        this.moveControl = new MWAWMovementController(this, 30);
    }

    public static AttributeSupplier.Builder func_234321_m_()
    {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 14.0D).add(Attributes.ATTACK_DAMAGE, 5.0D)
                .add(Attributes.FOLLOW_RANGE, 12.0D).add(Attributes.MOVEMENT_SPEED, 0.3D);
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new AvoidPlayerJackalopeGoal(this, Player.class, 10.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(3, new JackalopeEntity.RammingGoal(this));
        this.goalSelector.addGoal(3, new JackalopeEntity.EscapeGoal(this));
        this.goalSelector.addGoal(4, new JackalopeEntity.LeapAtTargetJackalopeGoal(this, 0.4F));
        this.goalSelector.addGoal(5, new JackalopeEntity.MeleeJackalopeGoal(this, 1.0D, true));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new JackalopeEntity.LookAtJackalopeGoal(this, Player.class, 10.0F));
        this.goalSelector.addGoal(8, new JackalopeEntity.LookAtRandomJackalopeGoal(this));
    }

    @Override
    protected PathNavigation createNavigation(Level world)
    {
        return new MWAWPathNavigateGround(this, world);
    }

    @Override
    protected void customServerAiStep()
    {
        if (tickCount % 2 == 0)
        {
            if (ramingCoolDown > 0)
            {
                --ramingCoolDown;
            }

            if (escapeCoolDown > 0)
            {
                --escapeCoolDown;
            }
        }

        super.customServerAiStep();
    }

    @Override
    public void aiStep()
    {
        super.aiStep();

        if (!this.level.isClientSide)
        {
            if (this.getTarget() == null && this.isAngry())
            {
                this.setAngry(false);
            } else if (this.getTarget() != null && !this.isAngry())
            {
                this.setAngry(true);
            }

            if (isRamming || isEscaping())
            {
                jumping = false;
                xxa = 0.0F;
                zza = 0.0F;
                navigation.stop();
            }

            if (isEscaping())
            {
                setEscapeTimer(getEscapingTimer() + 0.1F);
            }
        }
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob ageable)
    {
        JackalopeEntity jackalopebaby = new JackalopeEntity(EntityRegistryHandler.JACKALOPE_ENTITY.get(), level);
        return jackalopebaby;
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(ANGRY, false);
        this.entityData.define(ESCAPE, false);
        this.entityData.define(ESCAPE_TIMER, 0.0F);
    }

    public boolean isAngry()
    {
        return entityData.get(ANGRY);
    }

    public void setAngry(boolean angry)
    {
        entityData.set(ANGRY, angry);
    }

    public boolean isEscaping()
    {
        return entityData.get(ESCAPE);
    }

    public void setEscape(boolean escape)
    {
        entityData.set(ESCAPE, escape);
    }

    public float getEscapingTimer()
    {
        return entityData.get(ESCAPE_TIMER);
    }

    public void setEscapeTimer(float escapeTimerIn)
    {
        entityData.set(ESCAPE_TIMER, escapeTimerIn);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound)
    {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Angry", this.isAngry());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound)
    {
        super.readAdditionalSaveData(compound);
        if(compound.contains("Angry"))
        {
            this.setAngry(compound.getBoolean("Angry"));
        }
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.RABBIT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.RABBIT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.RABBIT_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState blockIn)
    {
        this.playSound(SoundEvents.SHEEP_STEP, 0.15F, 1.0F);
    }

    @Override
    public boolean hurt(DamageSource source, float amount)
    {
        if (!level.isClientSide)
        {
            if (source.getEntity() != null && !isRamming)
            {
                setEscape(true);
                attackedPos = new Vec3(source.getEntity().position().x(), source.getEntity().position().y(), source.getEntity().position().z());

                if (level.random.nextInt(100) < 50)
                {
                    if (source.getEntity() != null)
                    {
                        double d1 = source.getEntity().getX() - this.getX();

                        double d0;
                        for (d0 = source.getEntity().getZ() - this.getZ(); d1 * d1 + d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D)
                        {
                            d1 = (Math.random() - Math.random()) * 0.01D;
                        }

                        this.hurtDir = (float) (Mth.atan2(d0, d1) * (double) (180F / (float) Math.PI) - (double) this.getYRot());
                        this.knockback(0.4F, d1, d0);
                    }

                    return false;
                }

                if (source.getEntity() instanceof LivingEntity)
                {
                    this.setTarget((LivingEntity) source.getEntity());
                }
            }
        }

        return super.hurt(source, amount);

    }

    private class RammingGoal extends Goal
    {
        private final JackalopeEntity entity;
        private Vec3 motion;
        private int timer;

        public RammingGoal(JackalopeEntity _entity)
        {
            entity = _entity;
        }

        @Override
        public boolean canUse()
        {
            return ramingCoolDown <= 0 && !entity.isRamming
                    && entity.isOnGround() && entity.getTarget() != null && entity.getTarget().distanceTo(entity) < 5;
        }

        @Override
        public boolean canContinueToUse()
        {
            return entity.isRamming && entity.getTarget() != null && entity.getTarget().distanceTo(entity) < 5;
        }

        @Override
        public void start()
        {
            entity.isRamming = false;
            entity.setEscape(false);
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
            entity.getLookControl().setLookAt(entity.getTarget().getX(), entity.getTarget().getEyeY(), entity.getTarget().getZ());
            entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);

            if (!isRamming)
            {
                double d2 = entity.getTarget().getX() - entity.getX();
                double d1 = entity.getTarget().getZ() - entity.getZ();
                entity.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
                entity.yBodyRot = entity.getYRot();

                isRamming = true;

                if (entity.getTarget() != null)
                {
                    Vec3 dir = entity.getTarget().position().subtract(entity.position()).normalize();
                    motion = new Vec3(dir.x, dir.y, dir.z);

                    entity.playSound(SoundEvents.WOOL_PLACE, 3.0F, 0.1F);
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
                    entity.playSound(SoundEvents.ZOMBIE_ATTACK_WOODEN_DOOR, 1.0F, 0.1F);
                }

                if (!level.isClientSide)
                {
                    for (int j = 0; j < 10; ++j)
                    {
                        SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(new Vec3(JackalopeEntity.this.getX(), JackalopeEntity.this.getY() + JackalopeEntity.this.getEyeY(), JackalopeEntity.this.getZ()),
                                new Vec3(0.0f, -0.1f, 0.0f), 4, 4, getBbWidth());
                        MWAWPacketHandler.packetHandler.sendToDimension(JackalopeEntity.this.level.dimension(), spawnParticleMessage);
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

    private class EscapeGoal extends Goal
    {
        private final JackalopeEntity entity;
        private Vec3 motion;

        public EscapeGoal(JackalopeEntity _entity)
        {
            entity = _entity;
        }

        @Override
        public boolean canUse()
        {
            return entity.isEscaping() && attackedPos != null;
        }

        @Override
        public boolean canContinueToUse()
        {
            return entity.isEscaping() && entity.getEscapingTimer() < 0.5F;
        }

        @Override
        public void start()
        {
            double d2 = attackedPos.x() - entity.getX();
            double d1 = attackedPos.z() - entity.getZ();
            entity.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
            entity.yBodyRot = entity.getYRot();

            Vec3 dir = attackedPos.subtract(entity.position()).normalize().reverse();
            motion = new Vec3(dir.x * 0.09F, 0.0D, dir.z * 0.09F);
            entity.playSound(SoundEvents.WOOL_PLACE, 3.0F, 0.1F);
        }

        @Override
        public void stop()
        {
            entity.escapeCoolDown = 0;
            attackedPos = null;
            entity.setEscape(false);
            entity.setEscapeTimer(0);
            entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(0.0D);
        }

        @Override
        public void tick()
        {
            entity.getLookControl().setLookAt(attackedPos.x(), attackedPos.y() + 2, attackedPos.z());
            entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);

            double d2 = attackedPos.x() - entity.getX();
            double d1 = attackedPos.z() - entity.getZ();
            entity.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
            entity.yBodyRot = entity.getYRot();

            entity.setDeltaMovement(entity.getDeltaMovement().add(motion));
        }
    }

    private class LeapAtTargetJackalopeGoal extends LeapAtTargetGoal
    {

        public LeapAtTargetJackalopeGoal(Mob leapingEntity, float leapMotionYIn)
        {
            super(leapingEntity, leapMotionYIn);
        }

        @Override
        public boolean canUse()
        {
            return !JackalopeEntity.this.isRamming && super.canUse() && !JackalopeEntity.this.isEscaping();
        }
    }

    private class MeleeJackalopeGoal extends MWAWMeleeAttackGoal
    {
        public MeleeJackalopeGoal(PathfinderMob creature, double speedIn, boolean useLongMemory)
        {
            super(creature, speedIn, useLongMemory);
        }

        @Override
        public boolean canUse()
        {
            return !JackalopeEntity.this.isRamming && super.canUse() && !JackalopeEntity.this.isEscaping();
        }
    }

    private class LookAtJackalopeGoal extends LookAtPlayerGoal
    {
        public LookAtJackalopeGoal(Mob entityIn, Class<? extends LivingEntity> watchTargetClass, float maxDistance)
        {
            super(entityIn, watchTargetClass, maxDistance);
        }

        @Override
        public boolean canUse()
        {
            return !JackalopeEntity.this.isRamming && super.canUse() && !JackalopeEntity.this.isEscaping();
        }
    }

    private class LookAtRandomJackalopeGoal extends RandomLookAroundGoal
    {
        public LookAtRandomJackalopeGoal(Mob entitylivingIn)
        {
            super(entitylivingIn);
        }

        @Override
        public boolean canUse()
        {
            return !JackalopeEntity.this.isRamming && super.canUse() && !JackalopeEntity.this.isEscaping();
        }
    }

    private class AvoidPlayerJackalopeGoal extends AvoidEntityGoal
    {

        public AvoidPlayerJackalopeGoal(PathfinderMob entityIn, Class classToAvoidIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn)
        {
            super(entityIn, classToAvoidIn, avoidDistanceIn, farSpeedIn, nearSpeedIn);
        }

        @Override
        public boolean canUse()
        {
            return (getTarget() == null || JackalopeEntity.this.getHealth() < 5) && super.canUse();
        }
    }
}
