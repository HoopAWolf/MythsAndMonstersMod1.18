package com.hoopawolf.mwaw.items.weapons;

import com.hoopawolf.mwaw.tab.MWAWItemGroup;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

public class BoneDaggerItem extends SwordItem
{
    public BoneDaggerItem(Tier tier, int attackDamageIn, float attackSpeedIn, Properties builder)
    {
        super(tier, attackDamageIn, attackSpeedIn, builder.tab(MWAWItemGroup.instance));

    }
}