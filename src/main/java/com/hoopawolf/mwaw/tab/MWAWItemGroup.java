package com.hoopawolf.mwaw.tab;

import com.hoopawolf.mwaw.util.ItemBlockRegistryHandler;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public class MWAWItemGroup extends CreativeModeTab
{
    public static final MWAWItemGroup instance = new MWAWItemGroup(CreativeModeTab.TABS.length, "mwawItemGroup");

    public MWAWItemGroup(int index, String label)
    {
        super(index, label);
    }

    @Override
    public ItemStack makeIcon()
    {
        return new ItemStack(ItemBlockRegistryHandler.GOLDEN_BOW.get());
    }
}