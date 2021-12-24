package com.hoopawolf.mwaw.entities;

import com.hoopawolf.mwaw.entities.ai.MWAWMeleeAttackGoal;
import com.hoopawolf.mwaw.entities.ai.controller.MWAWMovementController;
import com.hoopawolf.mwaw.entities.ai.navigation.MWAWPathNavigateGround;
import com.hoopawolf.mwaw.entities.helper.EntityHelper;
import com.hoopawolf.mwaw.entities.projectiles.FoxHeadEntity;
import com.hoopawolf.mwaw.network.MWAWPacketHandler;
import com.hoopawolf.mwaw.network.packets.client.SpawnParticleMessage;
import com.hoopawolf.mwaw.network.packets.client.SpawnSuckingParticleMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class KitsuneEntity extends PathfinderMob
{
    private static final EntityDataAccessor<Boolean> VILLAGER_FORM = SynchedEntityData.defineId(KitsuneEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> SHOUTING = SynchedEntityData.defineId(KitsuneEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> FOX_PHASE = SynchedEntityData.defineId(KitsuneEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Byte> FOX_FLAGS = SynchedEntityData.defineId(KitsuneEntity.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> SHAKE_HEAD_TICKS = SynchedEntityData.defineId(KitsuneEntity.class, EntityDataSerializers.INT);
    private static final Predicate<Entity> IS_PREY = (p_213498_0_) ->
    {
        return p_213498_0_ instanceof Chicken || p_213498_0_ instanceof Sheep || p_213498_0_ instanceof Wolf;
    };
    private static final Predicate<Entity> SHOULD_AVOID = (p_213463_0_) ->
    {
        return !p_213463_0_.isDiscrete() && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(p_213463_0_);
    };
    private static final Predicate<ItemEntity> TRUSTED_TARGET_SELECTOR = (p_213489_0_) ->
    {
        return !p_213489_0_.hasPickUpDelay() && p_213489_0_.isAlive();
    };
    private float shouting_timer;
    private float villager_absorb;
    private boolean summoned;

    public KitsuneEntity(EntityType<? extends PathfinderMob> type, Level worldIn)
    {
        super(type, worldIn);
        this.lookControl = new KitsuneEntity.LookHelperController();
        this.moveControl = new KitsuneEntity.MoveHelperController();
        this.setPathfindingMalus(BlockPathTypes.DANGER_OTHER, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_OTHER, 0.0F);
        this.setCanPickUpLoot(true);
        shouting_timer = 0.0F;
        villager_absorb = 0.0F;
        this.maxUpStep = 1.0F;
        summoned = false;

        this.moveControl = new MWAWMovementController(this, 30);
    }

    public static AttributeSupplier.Builder func_234321_m_()
    {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 60.0D).add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.FOLLOW_RANGE, 12.0D).add(Attributes.MOVEMENT_SPEED, 0.35D);
    }

    @Override
    protected void registerGoals()
    {
        this.targetSelector.addGoal(2, (new HurtByTargetGoal(this)));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal(this, Animal.class, 10, false, false, IS_PREY));

        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new KitsuneEntity.LeapGoal(this, 0.4F));
        this.goalSelector.addGoal(3, new KitsuneEntity.BiteGoal(1.0D, true));
        this.goalSelector.addGoal(3, new KitsuneEntity.JumpGoal());
        this.goalSelector.addGoal(4, new KitsuneEntity.AvoidPlayerGoal(this, Player.class, 16.0F, 1.0D, 1.0D, (p_213497_1_) ->
        {
            return SHOULD_AVOID.test(p_213497_1_) && this.getTarget() == null;
        }));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(8, new KitsuneEntity.MoveToVillageGoal(this, 1.0D));
        this.goalSelector.addGoal(9, new KitsuneEntity.StrollGoal(32, 200));
        this.goalSelector.addGoal(11, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(11, new KitsuneEntity.FindItemsGoal());
        this.goalSelector.addGoal(12, new LookAtPlayerGoal(this, Villager.class, 24.0F));
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        this.entityData.define(VILLAGER_FORM, false);
        this.entityData.define(SHOUTING, false);
        this.entityData.define(FOX_PHASE, 3);
        this.entityData.define(FOX_FLAGS, (byte) 0);
        this.entityData.define(SHAKE_HEAD_TICKS, 0);
    }

    @Override
    protected PathNavigation createNavigation(Level world)
    {
        return new MWAWPathNavigateGround(this, world);
    }

    public boolean isVillagerForm()
    {
        return entityData.get(VILLAGER_FORM);
    }

    public void setVillagerForm(boolean isVillagerForm)
    {
        entityData.set(VILLAGER_FORM, isVillagerForm);
    }

    public int getShakeHeadTicks()
    {
        return entityData.get(SHAKE_HEAD_TICKS);
    }

    public void setShakeHeadTicks(int shakeTick)
    {
        entityData.set(SHAKE_HEAD_TICKS, shakeTick);
    }

    public boolean isShouting()
    {
        return entityData.get(SHOUTING);
    }

    public void setShouting(boolean isShouting)
    {
        entityData.set(SHOUTING, isShouting);
    }

    public int getFoxPhase()
    {
        return entityData.get(FOX_PHASE);
    }

    public void setFoxPhase(int foxPhaseIn)
    {
        entityData.set(FOX_PHASE, foxPhaseIn);
    }

    public boolean isStuck()
    {
        return this.getFoxFlag(64);
    }

    private void setStuck(boolean p_213492_1_)
    {
        this.setFoxFlag(64, p_213492_1_);
    }

    private void setFoxFlag(int p_213505_1_, boolean p_213505_2_)
    {
        if (p_213505_2_)
        {
            entityData.set(FOX_FLAGS, (byte) (entityData.get(FOX_FLAGS) | p_213505_1_));
        } else
        {
            entityData.set(FOX_FLAGS, (byte) (entityData.get(FOX_FLAGS) & ~p_213505_1_));
        }

    }

    private boolean getFoxFlag(int p_213507_1_)
    {
        return (entityData.get(FOX_FLAGS) & p_213507_1_) != 0;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound)
    {
        super.addAdditionalSaveData(compound);
        compound.putInt("FoxPhase", this.getFoxPhase());
        compound.putBoolean("VillagerForm", this.isVillagerForm());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound)
    {
        super.readAdditionalSaveData(compound);
        if(compound.contains("FoxPhase"))
        {
            this.setFoxPhase(compound.getInt("FoxPhase"));
        }

        if(compound.contains("VillagerForm"))
        {
            this.setVillagerForm(compound.getBoolean("VillagerForm"));
        }
    }

    @Override
    public void tick()
    {
        super.tick();


        if (this.getShakeHeadTicks() > 0)
        {
            this.setShakeHeadTicks(this.getShakeHeadTicks() - 1);
        }

        if (shouting_timer > 0 && !isShouting())
        {
            setShouting(true);
        } else if (shouting_timer <= 0 && isShouting())
        {
            setShouting(false);
        }

        if (this.isShouting() || this.isImmobile())
        {
            this.jumping = false;
            this.xxa = 0.0F;
            this.zza = 0.0F;

            List<LivingEntity> entities = EntityHelper.getEntityLivingBaseNearby(this, 10, 3, 10, 10);
            for (LivingEntity entity : entities)
            {
                if (!(entity instanceof Player && ((Player) entity).isCreative()))
                {
                    double angle = (EntityHelper.getAngleBetweenEntities(this, entity) + 90) * Math.PI / 180;
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
                    if (this.level.getDifficulty() == Difficulty.NORMAL)
                    {
                        i = 5;
                    } else if (this.level.getDifficulty() == Difficulty.HARD)
                    {
                        i = 10;
                    }

                    entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 20 * i, 1));
                    entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * i, 1));
                }
            }

            if (shouting_timer % 5 == 0 && shouting_timer >= 5 && !summoned)
            {
                if (!level.isClientSide)
                {
                    FoxHeadEntity foxHead = new FoxHeadEntity(this.level, this, getTarget());
                    foxHead.moveTo(getX() + ((shouting_timer / 5 == 3 || shouting_timer / 5 == 1) ? ((shouting_timer / 5 == 1) ? -2.5F : 2.5F) : 0.0F), getY() + 2.5F, getZ() + ((shouting_timer / 5 == 2) ? 2.5F : 0.0F));
                    this.level.addFreshEntity(foxHead);
                    this.playSound(SoundEvents.BELL_BLOCK, 5.0F, 10.0F);
                }
                summoned = true;
            }

            if (tickCount % 2 == 0)
            {
                --shouting_timer;
                summoned = false;
            }
        }


        if (!isShouting() && (getFoxPhase() == 3 && getHealth() <= 40 || getFoxPhase() == 2 && getHealth() <= 20 || getFoxPhase() == 1 && getHealth() <= 0))
        {
            setFoxPhase(getFoxPhase() - 1);
            shouting_timer = 20.0F;
            if (!level.isClientSide)
            {
                this.playSound(SoundEvents.ILLUSIONER_PREPARE_BLINDNESS, 4.0F, 10.0F);

                for (int i = 1; i <= 180; ++i)
                {
                    double yaw = i * 360 / 180;
                    double speed = 1.5;
                    double xSpeed = speed * Math.cos(Math.toRadians(yaw));
                    double zSpeed = speed * Math.sin(Math.toRadians(yaw));

                    SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(new Vec3(getX(), getY() + 0.5F, getZ()), new Vec3(xSpeed, 0.0D, zSpeed), 3, 4, 0.0F);
                    MWAWPacketHandler.packetHandler.sendToDimension(this.level.dimension(), spawnParticleMessage);
                }
            }
        }

        if (!level.isClientSide)
        {
            if (!isVillagerForm() && villager_absorb >= 10.0F && level.isDay())
            {
                changingForm(true);
                spitOutItem(this.getItemBySlot(EquipmentSlot.MAINHAND));
            } else if (isVillagerForm() && level.isNight())
            {
                changingForm(false);
            }

            if (this.isStuck() && this.level.random.nextFloat() < 0.2F)
            {
                BlockPos blockpos = this.blockPosition();
                BlockState blockstate = this.level.getBlockState(blockpos);
                this.level.levelEvent(2001, blockpos, Block.getId(blockstate));
            }

            if (tickCount % 3 == 0 && !isVillagerForm() && level.isDay())
            {
                if (getTarget() == null)
                {
                    List<LivingEntity> entities = EntityHelper.getEntityLivingBaseNearby(this, 10, 3, 10, 10);
                    for (LivingEntity entity : entities)
                    {
                        if (entity instanceof Villager)
                        {
                            villager_absorb += 0.5F;
                            SpawnSuckingParticleMessage spawnParticleMessage = new SpawnSuckingParticleMessage(new Vec3(entity.getX(), entity.getY() + 0.75F, entity.getZ()), new Vec3(0.1D, 0.1D, 0.1D), 5, 0, 0.5F);
                            MWAWPacketHandler.packetHandler.sendToDimension(this.level.dimension(), spawnParticleMessage);
                            entity.playSound(SoundEvents.NOTE_BLOCK_CHIME, 0.5F, 10.0F);
                        }
                    }
                }
            }
        }
    }

    private void changingForm(boolean change)
    {
        setVillagerForm(change);
        SpawnSuckingParticleMessage spawnSuckingParticleMessage = new SpawnSuckingParticleMessage(new Vec3(this.getX(), this.getY() + 0.75F, this.getZ()), new Vec3(0.1D, 0.1D, 0.1D), 5, 0, 0.5F);
        MWAWPacketHandler.packetHandler.sendToDimension(this.level.dimension(), spawnSuckingParticleMessage);

        SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(new Vec3(this.getX(), this.getY() + 0.75F, this.getZ()), new Vec3(0.0D, 0.1D, 0.0D), 10, 4, 0.5F);
        MWAWPacketHandler.packetHandler.sendToDimension(this.level.dimension(), spawnParticleMessage);

        this.playSound(SoundEvents.PUFFER_FISH_BLOW_OUT, 1.5F, 10.0F);
    }

    @Override
    public void handleEntityEvent(byte id)
    {
        if (id == 45)
        {
            ItemStack itemstack = this.getItemBySlot(EquipmentSlot.MAINHAND);
            if (!itemstack.isEmpty())
            {
                for (int i = 0; i < 8; ++i)
                {
                    Vec3 vec3d = (new Vec3(((double) this.random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D)).xRot(-this.getXRot() * ((float) Math.PI / 180F)).yRot(-this.getYRot() * ((float) Math.PI / 180F));
                    this.level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, itemstack), this.getX() + this.getLookAngle().x / 2.0D, this.getY(), this.getZ() + this.getLookAngle().z / 2.0D, vec3d.x, vec3d.y + 0.05D, vec3d.z);
                }
            }
        } else
        {
            super.handleEntityEvent(id);
        }
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntityDimensions sizeIn)
    {
        return this.isBaby() ? sizeIn.height * 0.85F : 0.4F;
    }

    @Override
    public boolean canHoldItem(ItemStack stack)
    {
        Item item = stack.getItem();
        ItemStack itemstack = this.getItemBySlot(EquipmentSlot.MAINHAND);
        return itemstack.isEmpty();
    }

    private void spitOutItem(ItemStack stackIn)
    {
        if (!stackIn.isEmpty() && !this.level.isClientSide)
        {
            ItemEntity itementity = new ItemEntity(this.level, this.getX() + this.getLookAngle().x, this.getY() + 1.0D, this.getZ() + this.getLookAngle().z, stackIn);
            itementity.setPickUpDelay(40);
            itementity.setThrower(this.getUUID());
            this.playSound(SoundEvents.FOX_SPIT, 1.0F, 1.0F);
            this.level.addFreshEntity(itementity);
            this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }
    }

    private void shakeHead()
    {
        this.setShakeHeadTicks(40);
        if (!this.level.isClientSide())
        {
            this.playSound(SoundEvents.VILLAGER_NO, this.getSoundVolume(), this.getVoicePitch());
        }
    }

    private void spawnItem(ItemStack stackIn)
    {
        ItemEntity itementity = new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), stackIn);
        this.level.addFreshEntity(itementity);
    }

    @Override
    protected void pickUpItem(ItemEntity itemEntity)
    {
        if (!isVillagerForm())
        {
            ItemStack itemstack = itemEntity.getItem();
            if (this.canHoldItem(itemstack))
            {
                int i = itemstack.getCount();
                if (i > 1)
                {
                    this.spawnItem(itemstack.split(i - 1));
                }

                this.spitOutItem(this.getItemBySlot(EquipmentSlot.MAINHAND));
                this.setItemSlot(EquipmentSlot.MAINHAND, itemstack.split(1));
                this.handDropChances[EquipmentSlot.MAINHAND.getIndex()] = 2.0F;
                this.take(itemEntity, itemstack.getCount());
                itemEntity.remove(RemovalReason.DISCARDED);
            }
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
            if (isVillagerForm())
            {
                changingForm(false);
                villager_absorb = 0.0F;
            }
        }

        return super.hurt(source, amount);
    }

    @Override
    public int getMaxSpawnClusterSize()
    {
        return 1;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        if (isVillagerForm())
            this.shakeHead();
        return super.mobInteract(player, hand);
    }

    @Override
    protected void dropAllDeathLoot(DamageSource damageSourceIn)
    {
        ItemStack itemstack = this.getItemBySlot(EquipmentSlot.MAINHAND);
        if (!itemstack.isEmpty())
        {
            this.spawnAtLocation(itemstack);
            this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }

        super.dropAllDeathLoot(damageSourceIn);
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return isVillagerForm() ? SoundEvents.VILLAGER_AMBIENT : null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return isVillagerForm() ? SoundEvents.VILLAGER_HURT : SoundEvents.FOX_HURT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return isVillagerForm() ? SoundEvents.VILLAGER_DEATH : SoundEvents.FOX_DEATH;
    }

    class JumpGoal extends Goal
    {
        int delay;

        public JumpGoal()
        {
            this.setFlags(EnumSet.of(Goal.Flag.LOOK, Goal.Flag.JUMP, Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse()
        {
            return KitsuneEntity.this.isStuck() && !KitsuneEntity.this.isVillagerForm();
        }

        @Override
        public boolean canContinueToUse()
        {
            return this.canUse() && this.delay > 0;
        }

        @Override
        public void start()
        {
            this.delay = 40;
        }

        @Override
        public void stop()
        {
            KitsuneEntity.this.setStuck(false);
        }

        @Override
        public void tick()
        {
            --this.delay;
        }
    }

    class MoveHelperController extends MoveControl
    {
        public MoveHelperController()
        {
            super(KitsuneEntity.this);
        }

        @Override
        public void tick()
        {
            if (!KitsuneEntity.this.isShouting())
            {
                super.tick();
            }
        }
    }

    public class LookHelperController extends LookControl
    {
        public LookHelperController()
        {
            super(KitsuneEntity.this);
        }

        @Override
        public void tick()
        {
            if (!KitsuneEntity.this.isShouting())
            {
                super.tick();
            }

        }
    }

    class MoveToVillageGoal extends GolemRandomStrollInVillageGoal
    {
        public MoveToVillageGoal(PathfinderMob p_i50325_1_, double p_i50325_2_)
        {
            super(p_i50325_1_, p_i50325_2_);
        }

        @Override
        public boolean canUse()
        {
            return super.canUse() && this.check();
        }

        @Override
        public boolean canContinueToUse()
        {
            return super.canContinueToUse() && this.check();
        }

        private boolean check()
        {
            return !KitsuneEntity.this.isSleeping() && KitsuneEntity.this.getTarget() == null;
        }
    }

    class StrollGoal extends StrollThroughVillageGoal
    {
        public StrollGoal(int p_i50726_2_, int p_i50726_3_)
        {
            super(KitsuneEntity.this, p_i50726_3_);
        }

        @Override
        public boolean canUse()
        {
            return super.canUse() && this.check();
        }

        @Override
        public boolean canContinueToUse()
        {
            return super.canContinueToUse() && this.check();
        }

        private boolean check()
        {
            return !KitsuneEntity.this.isSleeping() && KitsuneEntity.this.getTarget() == null;
        }
    }

    class LeapGoal extends LeapAtTargetGoal
    {

        public LeapGoal(Mob leapingEntity, float leapMotionYIn)
        {
            super(leapingEntity, leapMotionYIn);
        }

        @Override
        public boolean canUse()
        {
            return !KitsuneEntity.this.isShouting() && !KitsuneEntity.this.isVillagerForm() && super.canUse();
        }
    }

    class BiteGoal extends MWAWMeleeAttackGoal
    {
        public BiteGoal(double p_i50731_2_, boolean p_i50731_4_)
        {
            super(KitsuneEntity.this, p_i50731_2_, p_i50731_4_);
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr)
        {
            double d0 = this.getAttackReachSqr(enemy);
            if (distToEnemySqr <= d0 && this.attackTick <= 0)
            {
                this.attackTick = 20;
                this.attacker.doHurtTarget(enemy);
                KitsuneEntity.this.playSound(SoundEvents.FOX_BITE, 1.0F, 1.0F);
            }

        }

        @Override
        public boolean canUse()
        {
            return !KitsuneEntity.this.isShouting() && !KitsuneEntity.this.isVillagerForm() && super.canUse();
        }
    }

    class AvoidPlayerGoal extends AvoidEntityGoal
    {
        public AvoidPlayerGoal(PathfinderMob entityIn, Class classToAvoidIn, float avoidDistanceIn, double farSpeedIn, double nearSpeedIn)
        {
            super(entityIn, classToAvoidIn, avoidDistanceIn, farSpeedIn, nearSpeedIn);
        }

        public AvoidPlayerGoal(PathfinderMob entityIn, Class<Player> avoidClass, float distance, double nearSpeedIn, double farSpeedIn, Predicate<LivingEntity> targetPredicate)
        {
            super(entityIn, avoidClass, distance, nearSpeedIn, farSpeedIn, targetPredicate);
        }

        @Override
        public boolean canUse()
        {
            return super.canUse() && !isVillagerForm();
        }
    }

    class FindItemsGoal extends Goal
    {
        public FindItemsGoal()
        {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse()
        {
            if (!KitsuneEntity.this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty())
            {
                return false;
            } else if (KitsuneEntity.this.getTarget() == null && KitsuneEntity.this.getLastHurtByMob() == null)
            {
                if (KitsuneEntity.this.getRandom().nextInt(10) != 0)
                {
                    return false;
                } else
                {
                    List<ItemEntity> list = KitsuneEntity.this.level.getEntitiesOfClass(ItemEntity.class, KitsuneEntity.this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), KitsuneEntity.TRUSTED_TARGET_SELECTOR);
                    return !list.isEmpty() && KitsuneEntity.this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty() && !KitsuneEntity.this.isVillagerForm();
                }
            } else
            {
                return false;
            }
        }

        @Override
        public void tick()
        {
            List<ItemEntity> list = KitsuneEntity.this.level.getEntitiesOfClass(ItemEntity.class, KitsuneEntity.this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), KitsuneEntity.TRUSTED_TARGET_SELECTOR);
            ItemStack itemstack = KitsuneEntity.this.getItemBySlot(EquipmentSlot.MAINHAND);
            if (itemstack.isEmpty() && !list.isEmpty())
            {
                KitsuneEntity.this.getNavigation().moveTo(list.get(0), 1.0D);
            }

        }

        @Override
        public void start()
        {
            List<ItemEntity> list = KitsuneEntity.this.level.getEntitiesOfClass(ItemEntity.class, KitsuneEntity.this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), KitsuneEntity.TRUSTED_TARGET_SELECTOR);
            if (!list.isEmpty())
            {
                KitsuneEntity.this.getNavigation().moveTo(list.get(0), 1.);
            }

        }
    }
}