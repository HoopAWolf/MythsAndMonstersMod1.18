package com.hoopawolf.mwaw.entities;

import com.hoopawolf.mwaw.entities.ai.MWAWMeleeAttackGoal;
import com.hoopawolf.mwaw.entities.helper.EntityHelper;
import com.hoopawolf.mwaw.network.MWAWPacketHandler;
import com.hoopawolf.mwaw.network.packets.client.SpawnParticleMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class SandWyrmEntity extends PathfinderMob implements Enemy
{
    private static final EntityDataAccessor<Boolean> TIRED = SynchedEntityData.defineId(SandWyrmEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> TYPE = SynchedEntityData.defineId(SandWyrmEntity.class, EntityDataSerializers.INT); //0 - NORMAL SAND, 1 - RED SAND
    private static final EntityDataAccessor<Integer> ROTATION = SynchedEntityData.defineId(SandWyrmEntity.class, EntityDataSerializers.INT); // 0 - normal, 1 - dive up, 2 - dive below
    private final int maxJump = 5;
    private final float[] modelRotateX = new float[6];
    boolean _flag = false;
    boolean dived = false;
    int recoveryTimer = 0;
    MoveControl land_controller = new MoveControl(this);
    MoveControl underground_controller = new SandWyrmEntity.MoveHelperController(this);
    private int jumpRemaining;
    private float lastRotateX, newRotateX;

    private int attackTimer;
    private float timer;

    public SandWyrmEntity(EntityType<? extends SandWyrmEntity> type, Level worldIn)
    {
        super(type, worldIn);
        this.moveControl = underground_controller;
        this.jumpRemaining = this.maxJump;
        this.setTired(false);
        this.xpReward = 5;
    }

    public static AttributeSupplier.Builder func_234321_m_()
    {
        return Monster.createMonsterAttributes().add(Attributes.FLYING_SPEED, 1.7D).add(Attributes.MAX_HEALTH, 24.0D).add(Attributes.ATTACK_DAMAGE, 3.0D)
                .add(Attributes.FOLLOW_RANGE, 12.0D).add(Attributes.MOVEMENT_SPEED, 0.35D);
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();

        this.entityData.define(TIRED, false);
        this.entityData.define(ROTATION, 0);
        this.entityData.define(TYPE, 0);
    }

    @Override
    public void move(MoverType typeIn, Vec3 pos)
    {
        super.move(typeIn, pos);
        this.checkInsideBlocks();
    }

    @Override
    protected void registerGoals()
    {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(3, new SandWyrmEntity.ChargeAttackGoal(this));
        this.goalSelector.addGoal(4, new SandWyrmEntity.DiveGoal(this));
        this.goalSelector.addGoal(5, new SandWyrmEntity.MoveRandomGoal());
        this.goalSelector.addGoal(6, new SandWyrmEntity.TiredMeleeAttackGoal(this, 0.5F, true));

        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound)
    {
        super.addAdditionalSaveData(compound);
        compound.putInt("SandWyrmType", this.getSandWyrmType());

        for (int i = 0; i < modelRotateX.length; ++i)
        {
            compound.putFloat("RotateX" + i, modelRotateX[i]);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound)
    {
        super.readAdditionalSaveData(compound);

        if(compound.contains("SandWyrmType"))
        {
            this.setSandWyrmType(compound.getInt("SandWyrmType"));
        }

        if(compound.contains("RotateX0"))
        {
            for (int i = 0; i < modelRotateX.length; ++i)
            {
                if (compound.contains("RotateX" + i))
                {
                    modelRotateX[i] = compound.getFloat("RotateX" + i);
                } else
                {
                    modelRotateX[i] = 0.0F;
                }
            }
        }
    }

    @Override
    protected boolean shouldDespawnInPeaceful()
    {
        return true;
    }

    @Override
    public void tick()
    {
        super.tick();

        if (this.attackTimer > 0)
        {
            --this.attackTimer;
        }

        if (!isTired())
        {
            if (getBlockUnder(0).is(BlockTags.SAND) || getBlockUnder(4).is(BlockTags.SAND))
            {
                timer = 0;
                for (int i = 0; i < modelRotateX.length; ++i)
                {
                    if (i == 0)
                    {
                        this.lastRotateX = this.modelRotateX[i];
                        this.modelRotateX[i] = Mth.cos(tickCount * 0.6F) * (float) Math.PI * 0.01F * (float) (1 + Math.abs(i - 2));

                    } else
                    {
                        newRotateX = this.lastRotateX;
                        this.lastRotateX = this.modelRotateX[i];

                        this.modelRotateX[i] = newRotateX;
                    }
                }

                this.moveControl = underground_controller;
                this.noPhysics = true;
                this.setNoGravity(true);

                if (tickCount % 10 == 0)
                {
                    if (xOld == getX() && zOld == getZ() || !level.getBlockState(this.getOnPos()).canOcclude())
                    {
                        this.navigation.stop();
                        this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.5F, 0.0D));
                    }
                }

                if (_flag)
                {
                    if (!level.isClientSide)
                    {
                        SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(this.position(), new Vec3(level.random.nextInt(2), level.random.nextInt(2), level.random.nextInt(2)), 5, 2, 1.5F);
                        MWAWPacketHandler.packetHandler.sendToDimension(this.level.dimension(), spawnParticleMessage);
                    }
                    this.playSound(SoundEvents.SAND_BREAK, 1.0F, 1.0F);
                }
            } else
            {
                if (level.isEmptyBlock(this.getOnPos()))
                {
                    this.moveControl = land_controller;
                    this.noPhysics = false;
                    this.setNoGravity(false);
                    timer += 0.1F;
                    if (level.isEmptyBlock(this.getOnPos()) && level.getBlockState(getBlockPosBelowThatAffectsMyMovement()).canOcclude() || timer > 50.0F)
                    {
                        DecreaseStamina();
                    }
                }
            }
        } else
        {
            if (level.isEmptyBlock(this.blockPosition()))
            {
                this.moveControl = land_controller;
                this.noPhysics = false;
                this.setNoGravity(false);

                SandWyrmEntity.this.setXRot(0.0F);

                if (isTired())
                {
                    for (int i = 0; i < modelRotateX.length; ++i)
                    {
                        this.modelRotateX[i] = 0.0F;
                    }

                    SandWyrmEntity.this.setRotation(0);
                }

            } else
            {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, 0.05F, 0.0D));
            }
        }

        if (getTarget() == null)
        {
            double d0 = this.getAttribute(Attributes.FOLLOW_RANGE).getBaseValue();
            List<Player> entities = EntityHelper.getPlayersNearby(this, d0, d0, d0, d0 * 2);
            for (Player entity : entities)
            {
                if (!entity.isCreative() && !entity.isCrouching())
                {
                    this.setTarget(entity);
                    break;
                }
            }
        } else
        {
            if (!getTarget().isAlive() || distanceToSqr(getTarget()) > 50.0D)
            {
                this.setTarget(null);
                this.navigation.stop();
            }
        }
    }

    @Override
    protected void customServerAiStep()
    {
        if (!this.isTired())
        {
            if (getBlockAbove(1).is(BlockTags.SAND))
            {
                if (!(getTarget() != null && this.getDeltaMovement().y() > 0.0F))
                    this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -this.getDeltaMovement().y(), 0.0D));
                _flag = false;
            } else
            {
                _flag = true;
            }

            if (this.jumpRemaining <= 0)
            {
                this.jumpRemaining = 0;
                this.setTired(true);
            }
        } else
        {
            SandWyrmEntity.this.IncreaseStamina();

            if (this.jumpRemaining >= this.maxJump)
            {
                this.navigation.stop();
                this.ResetStamina();
                this.setTired(false);
            }
        }
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag)
    {
        Biome optional = worldIn.getBiome(this.getOnPos());
        int i = 0;

        if (optional.getRegistryName() == Biomes.BADLANDS.getRegistryName())
            i = 1;

        this.setSandWyrmType(i);
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
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
            if (source.msgId.equals(DamageSource.IN_WALL.msgId))
            {
                return false;
            }

            return super.hurt(source, amount);
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
    protected void checkFallDamage(double y, boolean onGroundIn, BlockState state, BlockPos pos)
    {
    }

    @Override
    public int getMaxSpawnClusterSize()
    {
        return 1;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleEntityEvent(byte id)
    {
        if (id == 4)
        {
            this.attackTimer = 10;
            this.playSound(SoundEvents.PANDA_BITE, 1.0F, 1.0F);
        } else
        {
            super.handleEntityEvent(id);
        }
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn)
    {
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, blockPosition().getX(), blockPosition().getZ());

        return worldIn.canSeeSky(this.blockPosition()) && (int) this.getY() == y;
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return null;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.VEX_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.VEX_HURT;
    }

    public float[] getAllRotateX()
    {
        return modelRotateX;
    }

    public boolean isTired()
    {
        return entityData.get(TIRED);
    }

    public void setTired(boolean tired)
    {
        entityData.set(TIRED, tired);
    }

    public int getSandWyrmType()
    {
        return entityData.get(TYPE);
    }

    public void setSandWyrmType(int type)
    {
        entityData.set(TYPE, type);
    }

    public int getRotation()
    {
        return entityData.get(ROTATION);
    }

    public void setRotation(int _rotation)
    {
        entityData.set(ROTATION, _rotation);
    }

    public int getAttackTimer()
    {
        return this.attackTimer;
    }

    protected void DecreaseStamina()
    {
        if (random.nextInt(100) < 60)
            --jumpRemaining;
    }

    protected void IncreaseStamina()
    {
        if (random.nextInt(100) < 40)
        {
            ++recoveryTimer;

            if (recoveryTimer >= 100)
            {
                ++jumpRemaining;
                recoveryTimer = 0;
            }
        }
    }

    protected void ResetStamina()
    {
        jumpRemaining = maxJump;
    }

    private BlockState getBlockUnder(int _deepness)
    {
        for (int i = 0; i <= _deepness; ++i)
        {
            if (!this.level.isEmptyBlock(new BlockPos(this.getX(), this.getY() - (1 + i), this.getZ())))
            {
                return this.level.getBlockState(new BlockPos(this.getX(), this.getY() - (1 + i), this.getZ()));
            }
        }

        return this.level.getBlockState(new BlockPos(this.getX(), this.getY() - 1, this.getZ()));
    }

    private BlockState getBlockAbove(int _highness)
    {
        for (int i = _highness; i > 0; --i)
        {
            if (!this.level.isEmptyBlock(new BlockPos(this.getX(), this.getY() + (1 + i), this.getZ())))
            {
                return this.level.getBlockState(new BlockPos(this.getX(), this.getY() + (1 + i), this.getZ()));
            }
        }

        return this.level.getBlockState(new BlockPos(this.getX(), this.getY() + 1, this.getZ()));
    }

    class ChargeAttackGoal extends Goal
    {
        SandWyrmEntity entity;

        public ChargeAttackGoal(SandWyrmEntity _entity)
        {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
            entity = _entity;
        }

        @Override
        public boolean canUse()
        {
            return dived && !_flag && !entity.isTired() && entity.getTarget() != null;
        }

        @Override
        public void stop()
        {
            dived = false;
        }

        @Override
        public void tick()
        {
            LivingEntity livingentity = entity.getTarget();
            if (livingentity != null && entity.getBoundingBox().intersects(livingentity.getBoundingBox().inflate(1.0D)))
            {
                entity.doHurtTarget(livingentity);
            }

            double d0 = entity.distanceToSqr(livingentity);
            if (d0 < 32.0D && entity.random.nextInt(7) == 0)
            {
                entity.playSound(SoundEvents.SAND_HIT, 1.0F, 1.0F);
                Direction direction = entity.getMotionDirection();
                entity.setDeltaMovement(entity.getDeltaMovement().add((double) direction.getStepX() * 0.6D, 0.0D, (double) direction.getStepZ() * 0.6D));
                entity.setDeltaMovement(entity.getDeltaMovement().x(), 0.5D, entity.getDeltaMovement().z());
                entity.navigation.stop();
                entity.DecreaseStamina();
                entity.setRotation(2);
            } else
            {
                Vec3 vec3d = livingentity.position();
                entity.moveControl.setWantedPosition(vec3d.x, vec3d.y - 3.0F, vec3d.z, entity.getAttribute(Attributes.FLYING_SPEED).getBaseValue());
                entity.setRotation(0);
            }
        }
    }

    class DiveGoal extends Goal
    {
        SandWyrmEntity entity;
        private boolean hasDived = false;

        public DiveGoal(SandWyrmEntity _entity)
        {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
            entity = _entity;
        }

        @Override
        public boolean canUse()
        {
            return !dived && !entity.isTired();
        }

        @Override
        public boolean canContinueToUse()
        {
            return !dived && _flag && !entity.isTired();
        }

        @Override
        public void stop()
        {
            dived = true;
            hasDived = false;
        }

        @Override
        public void tick()
        {
            entity.navigation.stop();
            if (hasDived)
            {
                if (entity.level.isEmptyBlock(new BlockPos(entity.getX(), entity.getY() - 0.75F, entity.getZ())))
                {
                    LivingEntity livingentity = entity.getTarget();
                    if (livingentity != null && entity.getBoundingBox().intersects(livingentity.getBoundingBox().inflate(1.0D)) && getDeltaMovement().y() > 0.1F)
                    {
                        entity.attackTimer = 10;
                        entity.level.broadcastEntityEvent(entity, (byte) 4);
                        entity.doHurtTarget(livingentity);
                    }
                }

                if (getBlockUnder(3).is(BlockTags.SAND) && entity.getDeltaMovement().y() < 0.0D)
                {
                    entity.setDeltaMovement(entity.getDeltaMovement().x(), -0.5D, entity.getDeltaMovement().z());
                    entity.setRotation(2);
                }
            }

            if (_flag && !hasDived)
            {
                Direction direction = entity.getMotionDirection();
                entity.setDeltaMovement(entity.getDeltaMovement().add((double) direction.getStepX() * 0.6D, 1.0D, (double) direction.getStepZ() * 0.6D));
                entity.setRotation(1);
                hasDived = true;
            }
        }
    }

    class MoveHelperController extends MoveControl
    {
        public MoveHelperController(SandWyrmEntity sandwyrm)
        {
            super(sandwyrm);
        }

        @Override
        public void tick()
        {
            if (this.operation == MoveControl.Operation.MOVE_TO)
            {
                Vec3 vec3d = new Vec3(this.getWantedX() - SandWyrmEntity.this.getX(), this.getWantedY() - SandWyrmEntity.this.getY(), this.getWantedZ() - SandWyrmEntity.this.getZ());
                double d0 = vec3d.length();
                if (d0 < SandWyrmEntity.this.getBoundingBox().getSize())
                {
                    this.operation = MoveControl.Operation.WAIT;
                    SandWyrmEntity.this.setDeltaMovement(SandWyrmEntity.this.getDeltaMovement().scale(0.5D));
                } else
                {
                    if (dived)
                    {
                        SandWyrmEntity.this.setDeltaMovement(SandWyrmEntity.this.getDeltaMovement().add(vec3d.scale(SandWyrmEntity.this.getAttribute(Attributes.FLYING_SPEED).getBaseValue() * 0.05D / d0)));

                        if (!level.isClientSide)
                        {
                            SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(SandWyrmEntity.this.position(), new Vec3(level.random.nextInt(2), level.random.nextInt(2), level.random.nextInt(2)), 5, 2, 1.5F);
                            MWAWPacketHandler.packetHandler.sendToDimension(SandWyrmEntity.this.level.dimension(), spawnParticleMessage);
                        }
                        SandWyrmEntity.this.playSound(SoundEvents.SAND_BREAK, 1.0F, 1.0F);

                        if (SandWyrmEntity.this.getTarget() == null)
                        {
                            Vec3 vec3d1 = SandWyrmEntity.this.getDeltaMovement();
                            SandWyrmEntity.this.setYRot(-((float) Mth.atan2(vec3d1.x, vec3d1.z)) * (180F / (float) Math.PI));
                        } else
                        {
                            double d2 = SandWyrmEntity.this.getTarget().getX() - SandWyrmEntity.this.getX();
                            double d1 = SandWyrmEntity.this.getTarget().getZ() - SandWyrmEntity.this.getZ();
                            SandWyrmEntity.this.setYRot(-((float) Mth.atan2(d2, d1)) * (180F / (float) Math.PI));
                        }
                        SandWyrmEntity.this.yBodyRot = SandWyrmEntity.this.getYRot();
                        SandWyrmEntity.this.setRotation(0);
                    }
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
            return dived && !_flag && !SandWyrmEntity.this.isTired() && !SandWyrmEntity.this.getMoveControl().hasWanted() && SandWyrmEntity.this.random.nextInt(20) == 0;
        }

        @Override
        public void tick()
        {
            for (int i = 0; i < 3; ++i)
            {
                BlockPos blockpos1 = SandWyrmEntity.this.blockPosition().offset(SandWyrmEntity.this.random.nextInt(15) - 7, SandWyrmEntity.this.random.nextInt(11) - 5, SandWyrmEntity.this.random.nextInt(15) - 7);
                if (SandWyrmEntity.this.level.getBlockState(blockpos1).is(BlockTags.SAND))
                {
                    SandWyrmEntity.this.setRotation(0);
                    SandWyrmEntity.this.moveControl.setWantedPosition((double) blockpos1.getX() + 0.5D, (double) blockpos1.getY() + 0.5D, (double) blockpos1.getZ() + 0.5D, 0.25D);
                    break;
                }
            }
        }
    }

    class TiredMeleeAttackGoal extends MWAWMeleeAttackGoal
    {
        public TiredMeleeAttackGoal(PathfinderMob creature, double speedIn, boolean useLongMemory)
        {
            super(creature, speedIn, useLongMemory);
        }

        @Override
        public boolean canUse()
        {
            return isTired() && super.canUse();
        }

        @Override
        public boolean canContinueToUse()
        {
            return isTired() && super.canContinueToUse();
        }
    }
}
