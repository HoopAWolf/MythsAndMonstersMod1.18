package com.hoopawolf.mwaw.items.weapons;

import com.hoopawolf.mwaw.entities.projectiles.GoldenArrowEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class GoldenArrowItem extends ArrowItem
{
    public GoldenArrowItem(Properties builder)
    {
        super(builder);
    }

    @Override
    public AbstractArrow createArrow(Level worldIn, ItemStack stack, LivingEntity shooter)
    {
        return new GoldenArrowEntity(worldIn, shooter);
    }

    @Override
    public Rarity getRarity(ItemStack stack)
    {
        return Rarity.UNCOMMON;
    }
}