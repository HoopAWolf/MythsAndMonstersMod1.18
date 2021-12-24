package com.hoopawolf.mwaw.items.weapons;

import com.hoopawolf.mwaw.tab.MWAWItemGroup;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class MarrowSwordItem extends SwordItem
{
    public MarrowSwordItem(Tier tier, int attackDamageIn, float attackSpeedIn, Properties builder)
    {
        super(tier, attackDamageIn, attackSpeedIn, builder.tab(MWAWItemGroup.instance));

    }

    @Override
    public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected)
    {
        if (isSelected)
        {
            if (entityIn.tickCount % 3 == 0)
            {
                if (entityIn instanceof Player && !((Player) entityIn).isCreative())
                {
                    Player _playerEntity = (Player) entityIn;

                    if (_playerEntity.getHealth() != _playerEntity.getMaxHealth() && _playerEntity.getFoodData().getFoodLevel() > 1)
                    {
                        if (!worldIn.isClientSide())
                        {
                            _playerEntity.heal(1f);
                            _playerEntity.getFoodData().setFoodLevel(_playerEntity.getFoodData().getFoodLevel() - 1);
                        } else
                        {
                            worldIn.playSound(_playerEntity, _playerEntity.getX(), _playerEntity.getY() + 2, _playerEntity.getZ(), SoundEvents.PLAYER_BURP, SoundSource.NEUTRAL, 1, 100.0f);
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
        tooltip.add(new TranslatableComponent("\u00A77\u00A7o" + I18n.get("tooltip.mwaw:marrowinfo")));
    }

}
