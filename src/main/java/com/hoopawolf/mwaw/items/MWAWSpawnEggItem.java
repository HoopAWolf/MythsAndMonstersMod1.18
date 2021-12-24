package com.hoopawolf.mwaw.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class MWAWSpawnEggItem extends ForgeSpawnEggItem
{
    protected static final List<MWAWSpawnEggItem> UNADDED_EGGS = new ArrayList<>();
    private final Supplier<? extends EntityType<? extends Mob>> entityTypeSupplier;

    public MWAWSpawnEggItem(final Supplier<? extends EntityType<? extends Mob>> type, final int p_i48465_2_, final int p_i48465_3_, final Properties p_i48465_4_)
    {
        super(null, p_i48465_2_, p_i48465_3_, p_i48465_4_);
        this.entityTypeSupplier = type;
        UNADDED_EGGS.add(this);
    }

    @Override
    public EntityType<?> getType(@Nullable CompoundTag tag)
    {
        EntityType<?> type = super.getType(tag);
        return type != null ? type : entityTypeSupplier.get();
    }
}
