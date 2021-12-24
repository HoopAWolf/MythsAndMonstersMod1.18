package com.hoopawolf.mwaw.items.weapons;

import com.hoopawolf.mwaw.network.MWAWPacketHandler;
import com.hoopawolf.mwaw.network.packets.client.SpawnOrbitingParticleMessage;
import com.hoopawolf.mwaw.util.ItemBlockRegistryHandler;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

public class GoldenBowItem extends ProjectileWeaponItem
{

    public GoldenBowItem(Item.Properties builder)
    {
        super(builder);
    }

    /**
     * Gets the velocity of the arrow entity from the bow's charge
     */
    public static float getArrowVelocity(int charge)
    {
        float f = (float) charge / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F)
        {
            f = 1.0F;
        }

        return f;
    }

    @Override
    public void onUseTick(Level worldIn, LivingEntity livingEntityIn, ItemStack stack, int count)
    {
        if (!worldIn.isClientSide)
        {
            SpawnOrbitingParticleMessage spawnParticleMessage = new SpawnOrbitingParticleMessage(livingEntityIn.position(), new Vec3(0.0D, 0.02D, 0.0D), 1, 0, 0.5F);
            MWAWPacketHandler.packetHandler.sendToDimension(livingEntityIn.level.dimension(), spawnParticleMessage);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft)
    {
        if (entityLiving instanceof Player)
        {
            Player playerentity = (Player) entityLiving;
            boolean flag = playerentity.getAbilities().instabuild || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.INFINITY_ARROWS, stack) > 0;
            ItemStack itemstack = playerentity.getProjectile(stack);

            int i = this.getUseDuration(stack) - timeLeft;
            i = net.minecraftforge.event.ForgeEventFactory.onArrowLoose(stack, worldIn, playerentity, i, !itemstack.isEmpty() || flag);
            if (i < 0) return;

            if (!itemstack.isEmpty() || flag)
            {
                if (itemstack.isEmpty())
                {
                    itemstack = new ItemStack(ItemBlockRegistryHandler.GOLDEN_ARROW.get());
                }

                float f = getArrowVelocity(i);
                if (!((double) f < 0.1D))
                {
                    boolean flag1 = playerentity.getAbilities().instabuild || (itemstack.getItem() instanceof GoldenArrowItem && ((GoldenArrowItem) itemstack.getItem()).isInfinite(itemstack, stack, playerentity));
                    if (!worldIn.isClientSide)
                    {
                        GoldenArrowItem arrowitem = (GoldenArrowItem) (itemstack.getItem() instanceof GoldenArrowItem ? itemstack.getItem() : ItemBlockRegistryHandler.GOLDEN_ARROW.get());
                        AbstractArrow abstractarrowentity = arrowitem.createArrow(worldIn, itemstack, playerentity);
                        abstractarrowentity = customeArrow(abstractarrowentity);
                        abstractarrowentity.shootFromRotation(playerentity, playerentity.getXRot(), playerentity.getYRot(), 0.0F, f * 3.0F, 1.0F);
                        if (f == 1.0F)
                        {
                            abstractarrowentity.setCritArrow(true);
                        }

                        int j = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.POWER_ARROWS, stack);
                        if (j > 0)
                        {
                            abstractarrowentity.setBaseDamage(abstractarrowentity.getBaseDamage() + (double) j * 0.5D + 0.5D);
                        }

                        int k = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.PUNCH_ARROWS, stack);
                        if (k > 0)
                        {
                            abstractarrowentity.setKnockback(k);
                        }

                        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FLAMING_ARROWS, stack) > 0)
                        {
                            abstractarrowentity.setSecondsOnFire(100);
                        }

                        stack.hurtAndBreak(1, playerentity, (p_220009_1_) ->
                        {
                            p_220009_1_.broadcastBreakEvent(playerentity.getUsedItemHand());
                        });

                        worldIn.addFreshEntity(abstractarrowentity);
                    }

                    worldIn.playSound(null, playerentity.getX(), playerentity.getY(), playerentity.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (playerentity.getRandom().nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                    if (!flag1 && !playerentity.getAbilities().instabuild)
                    {
                        itemstack.shrink(1);
                        if (itemstack.isEmpty())
                        {
                            playerentity.getInventory().removeItem(itemstack);
                        }
                    }

                    playerentity.awardStat(Stats.ITEM_USED.get(this));
                }
            }
        }
    }

    @Override
    public int getUseDuration(ItemStack stack)
    {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack)
    {
        return UseAnim.BOW;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn)
    {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        boolean flag = !playerIn.getProjectile(itemstack).isEmpty();

        InteractionResultHolder<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onArrowNock(itemstack, worldIn, playerIn, handIn, flag);
        if (ret != null) return ret;

        if (!playerIn.getAbilities().instabuild && !flag)
        {
            return InteractionResultHolder.fail(itemstack);
        } else
        {
            playerIn.startUsingItem(handIn);
            return InteractionResultHolder.consume(itemstack);
        }
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles()
    {
        return itemStack -> itemStack.getItem() instanceof GoldenArrowItem;
    }

    @Override
    public int getDefaultProjectileRange()
    {
        return 15;
    }

    public AbstractArrow customeArrow(AbstractArrow arrow)
    {
        return arrow;
    }
}