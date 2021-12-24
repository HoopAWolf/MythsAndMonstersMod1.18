package com.hoopawolf.mwaw.entities;

import com.google.common.collect.Sets;
import com.hoopawolf.mwaw.entities.ai.LookAtCustomerHunterGoal;
import com.hoopawolf.mwaw.entities.ai.RangedBowAttackHunterGoal;
import com.hoopawolf.mwaw.entities.ai.TradeWithPlayerHunterGoal;
import com.hoopawolf.mwaw.entities.ai.controller.MWAWMovementController;
import com.hoopawolf.mwaw.entities.ai.navigation.MWAWPathNavigateGround;
import com.hoopawolf.mwaw.entities.merchant.Trades;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class HunterEntity extends AbstractVillager implements RangedAttackMob
{
    private static final Predicate<ItemEntity> TRUSTED_TARGET_SELECTOR = (p_213489_0_) ->
    {
        return (p_213489_0_.getItem().isEdible())
                && (!p_213489_0_.isPickable() && p_213489_0_.isAlive());
    };

    private final Item[] WEAPON =
            {
                    Items.BOW
            };

    private final Item[] BODY_ARMOR =
            {
                    Items.LEATHER_CHESTPLATE,
            };

    private final Item[] LEGGING_ARMOR =
            {
                    Items.LEATHER_LEGGINGS,
            };

    private final Item[] BOOTS_ARMOR =
            {
                    Items.LEATHER_BOOTS,
            };
    private final SimpleContainer hunterInventory = new SimpleContainer(8);
    protected MerchantOffers offers;
    private Player customer;

    public HunterEntity(EntityType<? extends HunterEntity> type, Level worldIn)
    {
        super(type, worldIn);

        this.setCanPickUpLoot(true);
        this.maxUpStep = 1.0F;
        this.moveControl = new MWAWMovementController(this, 180);
    }

    public static AttributeSupplier.Builder func_234321_m_()
    {
        return Monster.createMonsterAttributes().add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.FOLLOW_RANGE, 12.0D).add(Attributes.MOVEMENT_SPEED, 0.35D);
    }

    @Override
    protected void registerGoals()
    {
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, DendroidEntity.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Animal.class, true));

        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new TradeWithPlayerHunterGoal(this));
        this.goalSelector.addGoal(4, new RangedBowAttackHunterGoal<>(this, 1.0D, 20, 15.0F));
        this.goalSelector.addGoal(5, new FindItemsGoal());
        this.goalSelector.addGoal(6, new LookAtCustomerHunterGoal(this));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 3.0F, 1.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.goalSelector.addGoal(11, new WaterAvoidingRandomStrollGoal(this, 1.0D));
    }

    @Override
    protected PathNavigation createNavigation(Level world)
    {
        return new MWAWPathNavigateGround(this, world);
    }

    @Override
    protected boolean shouldDespawnInPeaceful()
    {
        return false;
    }

    @Override
    public MobType getMobType()
    {
        return MobType.UNDEFINED;
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor worldIn, MobSpawnType spawnReasonIn)
    {
        return worldIn.canSeeSky(blockPosition());
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.PLAYER_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn)
    {
        return SoundEvents.PLAYER_HURT;
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
    public InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        ItemStack itemstack = player.getItemInHand(hand);
        boolean flag = itemstack.getItem() == Items.NAME_TAG;
        if (flag)
        {
            itemstack.interactLivingEntity(player, this, hand);
            return InteractionResult.sidedSuccess(this.level.isClientSide);
        } else if (this.isAlive() && !this.isTrading() && !this.isBaby())
        {
            if (this.getOffers().isEmpty() || this.getTarget() != null)
            {
                return super.mobInteract(player, hand);
            } else
            {
                if (!this.level.isClientSide)
                {
                    this.setTradingPlayer(player);
                    this.openTradingScreen(player, this.getDisplayName(), 0);
                }

                return InteractionResult.sidedSuccess(this.level.isClientSide);
            }
        } else
        {
            return super.mobInteract(player, hand);
        }
    }

    @Override
    protected void rewardTradeXp(MerchantOffer offer)
    {
    }

    @Override
    protected void updateTrades()
    {
        Trades.ITrade[] hunter$itrade = Trades.hunter_trade.get(0);
        if (hunter$itrade != null)
        {
            MerchantOffers merchantoffers = this.getOffers();
            this.addTrades(merchantoffers, hunter$itrade, 5);
            Trades.ITrade villagertrades$itrade = hunter$itrade[0];
            MerchantOffer merchantoffer = villagertrades$itrade.getOffer(this, this.random);
            if (merchantoffer != null)
            {
                merchantoffers.add(merchantoffer);
            }
        }
    }

    @Override
    public SoundEvent getNotifyTradeSound()
    {
        return SoundEvents.NOTE_BLOCK_BIT;
    }

    @Override
    protected SoundEvent getTradeUpdatedSound(boolean getYesSound)
    {
        return getYesSound ? SoundEvents.NOTE_BLOCK_BIT : SoundEvents.NOTE_BLOCK_BASS;
    }

    @Override
    public void playCelebrateSound()
    {
        this.playSound(SoundEvents.NOTE_BLOCK_CHIME, this.getSoundVolume(), this.getVoicePitch());
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, SpawnGroupData spawnDataIn, CompoundTag dataTag)
    {
        this.populateDefaultEquipmentSlots(difficultyIn);
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob ageable)
    {
        return null;
    }

    protected void addTrades(MerchantOffers givenMerchantOffers, Trades.ITrade[] newTrades, int maxNumbers)
    {
        Set<Integer> set = Sets.newHashSet();
        if (newTrades.length > maxNumbers)
        {
            while (set.size() < maxNumbers)
            {
                set.add(this.random.nextInt(newTrades.length));
            }
        } else
        {
            for (int i = 0; i < newTrades.length; ++i)
            {
                set.add(i);
            }
        }

        for (Integer integer : set)
        {
            Trades.ITrade huntertrades$itrade = newTrades[integer];
            MerchantOffer merchantoffer = huntertrades$itrade.getOffer(this, this.random);
            if (merchantoffer != null)
            {
                givenMerchantOffers.add(merchantoffer);
            }
        }

    }

    @Override
    protected void populateDefaultEquipmentSlots(DifficultyInstance difficulty)
    {
        ItemStack
                weapon = null,
                chest = null,
                legging = null,
                boot = null;

        int random_weapon = random.nextInt(WEAPON.length);
        weapon = new ItemStack(WEAPON[random_weapon]);

        if (this.random.nextInt(100) < 15)
        {
            int randomInt = random.nextInt(BODY_ARMOR.length);
            chest = new ItemStack(BODY_ARMOR[randomInt]);
        }

        if (this.random.nextInt(100) < 15)
        {
            int randomInt = random.nextInt(LEGGING_ARMOR.length);
            legging = new ItemStack(LEGGING_ARMOR[randomInt]);
        }

        if (this.random.nextInt(100) < 15)
        {
            int randomInt = random.nextInt(BOOTS_ARMOR.length);
            boot = new ItemStack(BOOTS_ARMOR[randomInt]);
        }

        if (weapon != null)
            this.setItemSlot(EquipmentSlot.MAINHAND, weapon);

        if (chest != null)
            this.setItemSlot(EquipmentSlot.CHEST, chest);

        if (legging != null)
            this.setItemSlot(EquipmentSlot.LEGS, legging);

        if (boot != null)
            this.setItemSlot(EquipmentSlot.FEET, boot);
    }


    @Override
    protected void pickUpItem(ItemEntity itemEntity)
    {
        ItemStack itemstack = itemEntity.getItem();
        if (this.canHoldItem(itemstack))
        {
            this.setItemSlot(EquipmentSlot.OFFHAND, itemstack);
            this.take(itemEntity, itemstack.getCount());
            itemEntity.remove(RemovalReason.DISCARDED);
        }
    }

    @Override
    public int getMaxSpawnClusterSize()
    {
        return 2;
    }

    @Override
    public boolean canHoldItem(ItemStack stack)
    {
        Item item = stack.getItem();
        ItemStack itemstack = this.getItemBySlot(EquipmentSlot.OFFHAND);
        return itemstack.isEmpty() && item.isEdible();
    }

    private void eatFoodOnOffHand(float healAmount)
    {
        if (this.getOffhandItem().isEdible())
        {
            this.heal(this.getOffhandItem().getItem().getFoodProperties().getNutrition() * 0.5F);
            ItemStack itemstack = this.getItemBySlot(EquipmentSlot.OFFHAND);
            ItemStack itemstack1 = itemstack.finishUsingItem(this.level, this);
            if (!itemstack1.isEmpty())
            {
                this.setItemSlot(EquipmentSlot.OFFHAND, itemstack1);
            }

            this.playSound(this.getEatingSound(itemstack), 1.0F, 1.0F);
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
            if (source.getEntity() instanceof HunterEntity)
                return false;

            if (tickCount % 2 == 0)
                eatFoodOnOffHand(getMaxHealth() * 0.2F);

            return super.hurt(source, amount);
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor)
    {
        ItemStack itemstack = this.getProjectile(this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, Predicate.isEqual(Items.BOW))));
        AbstractArrow abstractarrowentity = this.fireArrow(itemstack, distanceFactor);
        if (this.getMainHandItem().getItem() instanceof BowItem)
            abstractarrowentity = ((BowItem) this.getMainHandItem().getItem()).customArrow(abstractarrowentity);
        double d0 = target.getX() - this.getX();
        double d1 = target.getY(0.3333333333333333D) - abstractarrowentity.getY();
        double d2 = target.getZ() - this.getZ();
        double d3 = Mth.sqrt((float) (d0 * d0 + d2 * d2));
        abstractarrowentity.shoot(d0, d1 + d3 * (double) 0.2F, d2, 1.6F, (float) (14 - this.level.getDifficulty().getId() * 4));
        this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level.addFreshEntity(abstractarrowentity);
    }

    protected AbstractArrow fireArrow(ItemStack arrowStack, float distanceFactor)
    {
        return ProjectileUtil.getMobArrow(this, arrowStack, distanceFactor);
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
            if (!HunterEntity.this.getItemBySlot(EquipmentSlot.OFFHAND).isEmpty())
            {
                return false;
            } else if (HunterEntity.this.getTarget() == null && HunterEntity.this.getLastHurtByMob() == null)
            {
                if (HunterEntity.this.getRandom().nextInt(10) != 0)
                {
                    return false;
                } else
                {
                    List<ItemEntity> list = HunterEntity.this.level.getEntitiesOfClass(ItemEntity.class, HunterEntity.this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), HunterEntity.TRUSTED_TARGET_SELECTOR);
                    return !list.isEmpty() && HunterEntity.this.getItemBySlot(EquipmentSlot.OFFHAND).isEmpty();
                }
            } else
            {
                return false;
            }
        }

        @Override
        public void tick()
        {
            List<ItemEntity> list = HunterEntity.this.level.getEntitiesOfClass(ItemEntity.class, HunterEntity.this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), HunterEntity.TRUSTED_TARGET_SELECTOR);
            ItemStack itemstack = HunterEntity.this.getItemBySlot(EquipmentSlot.OFFHAND);
            if (itemstack.isEmpty() && !list.isEmpty())
            {
                HunterEntity.this.getNavigation().moveTo(list.get(0), HunterEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue());
            }
        }

        @Override
        public void start()
        {
            List<ItemEntity> list = HunterEntity.this.level.getEntitiesOfClass(ItemEntity.class, HunterEntity.this.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), HunterEntity.TRUSTED_TARGET_SELECTOR);
            if (!list.isEmpty())
            {
                HunterEntity.this.getNavigation().moveTo(list.get(0), HunterEntity.this.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue());
            }

        }
    }
}