package com.hoopawolf.mwaw.items.weapons;

import com.hoopawolf.mwaw.tab.MWAWItemGroup;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class DendroidSwordItem extends SwordItem
{
    public DendroidSwordItem(Tier tier, int attackDamageIn, float attackSpeedIn, Properties builder)
    {
        super(tier, attackDamageIn, attackSpeedIn, builder.tab(MWAWItemGroup.instance));

    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
        if (isSelected && stack.isDamaged())
        {
            if (entityIn instanceof Player)
            {
                if (entityIn.tickCount % 3 == 0)
                {
                    Player _playerEntity = (Player) entityIn;

                    if (!_playerEntity.getOffhandItem().isEmpty())
                    {
                        if (_playerEntity.getOffhandItem().getItem() instanceof BlockItem && ((BlockItem) _playerEntity.getOffhandItem().getItem()).getBlock().defaultBlockState().getMaterial().equals(Material.WOOD))
                        {
                            if (!worldIn.isClientSide())
                            {
                                int _recoveryAmount = 5;

                                stack.setDamageValue(stack.getDamageValue() - _recoveryAmount);
                                _playerEntity.getInventory().offhand.get(0).setCount(_playerEntity.getInventory().offhand.get(0).getCount() - 1);
                            } else
                            {
                                worldIn.playSound(_playerEntity, _playerEntity.getX(), _playerEntity.getY() + 2, _playerEntity.getZ(), SoundEvents.BAMBOO_BREAK, SoundSource.NEUTRAL, 1.0F, 5.0F);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
    {
        tooltip.add(new TranslatableComponent("\u00A77\u00A7o" + I18n.get("tooltip.mwaw:dendroidinfo")));
    }

    /*public InteractionResultHolder<ItemStack> onItemRightClick(Level worldIn, Player playerIn, InteractionHand handIn)
    {
        if (playerIn.getHeldItem(handIn).isDamaged() && !playerIn.getOffhandItem().isEmpty())
        {
            for (Item _item : log_list)
            {
                if (playerIn.getOffhandItem().getItem() == _item)
                {
                    if (!worldIn.isRemote())
                    {
                        int _recoveryAmount = 5;

                        playerIn.getHeldItem(handIn).setDamage(playerIn.getHeldItem(handIn).getDamage() - _recoveryAmount);
                        playerIn.getInventory().offHandInventory.get(0).setCount(playerIn.getInventory().offHandInventory.get(0).getCount() - 1);
                    } else
                    {
                        worldIn.playSound(playerIn.getX(), playerIn.getY() + 2, playerIn.getZ(), SoundEvents.BLOCK_BAMBOO_BREAK, SoundSource.NEUTRAL, 1, 1, false);
                    }
                    break;
                }
            }
        }

        return InteractionResultHolder.resultPass(playerIn.getHeldItem(handIn));
    }*/
}
