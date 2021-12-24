package com.hoopawolf.mwaw.entities.merchant;

import com.google.common.collect.ImmutableMap;
import com.hoopawolf.mwaw.util.ItemBlockRegistryHandler;
import com.mojang.datafixers.schemas.Schema;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.Random;

public class Trades extends VillagerTrades
{
    public static final Int2ObjectMap<ITrade[]> hunter_trade = gatAsIntMap(ImmutableMap.of(
            0, new ITrade[]{
                    new ItemsForEmeraldsTrade(Items.BEEF, 3, 12, 2, 0),
                    new ItemsForEmeraldsTrade(Items.CHICKEN, 1, 12, 5, 0),
                    new ItemsForEmeraldsTrade(Items.PORKCHOP, 2, 12, 3, 0),
                    new ItemsForEmeraldsTrade(Items.MUTTON, 2, 12, 3, 0),
                    new ItemsForEmeraldsTrade(ItemBlockRegistryHandler.HARDENED_LEATHER.get(), 1, 5, 12, 0),
                    new ItemsForEmeraldsTrade(Items.COOKED_BEEF, 3, 12, 2, 0),
                    new ItemsForEmeraldsTrade(Items.COOKED_CHICKEN, 1, 12, 5, 0),
                    new ItemsForEmeraldsTrade(Items.COOKED_PORKCHOP, 2, 12, 3, 0),
                    new ItemsForEmeraldsTrade(Items.COOKED_MUTTON, 2, 12, 3, 0),
                    new ItemsForEmeraldsTrade(Items.LEATHER, 1, 5, 12, 0)
            }));

//    public Trades(Schema outputSchema, boolean changesType)
//    {
//        super(outputSchema, changesType);
//    }

    private static Int2ObjectMap<ITrade[]> gatAsIntMap(ImmutableMap<Integer, ITrade[]> p_221238_0_)
    {
        return new Int2ObjectOpenHashMap<>(p_221238_0_);
    }

    public interface ITrade
    {
        @Nullable
        MerchantOffer getOffer(Entity trader, Random random);
    }

    static class ItemsForEmeraldsTrade implements ITrade
    {
        private final ItemStack itemStack;
        private final int emeraldCount;
        private final int count;
        private final int maxStock;
        private final int expGiven;
        private final float priceMultiplyer;

        public ItemsForEmeraldsTrade(Block block, int _emaraldCost, int _count, int _maxStock, int _expGiven)
        {
            this(new ItemStack(block), _emaraldCost, _count, _maxStock, _expGiven);
        }

        public ItemsForEmeraldsTrade(Item _item, int _emaraldCost, int _count, int _expGiven)
        {
            this(new ItemStack(_item), _emaraldCost, _count, 12, _expGiven);
        }

        public ItemsForEmeraldsTrade(Item _item, int _emaraldCost, int _count, int _maxStock, int _expGiven)
        {
            this(new ItemStack(_item), _emaraldCost, _count, _maxStock, _expGiven);
        }

        public ItemsForEmeraldsTrade(ItemStack _itemStack, int _emaraldCost, int _count, int _maxStock, int _expGiven)
        {
            this(_itemStack, _emaraldCost, _count, _maxStock, _maxStock, 0.05F);
        }

        public ItemsForEmeraldsTrade(ItemStack _itemStack, int _emaraldCost, int _count, int _maxStock, int _expGiven, float _priceMultiplyer)
        {
            this.itemStack = _itemStack;
            this.emeraldCount = _emaraldCost;
            this.count = _count;
            this.maxStock = _maxStock;
            this.expGiven = _expGiven;
            this.priceMultiplyer = _priceMultiplyer;
        }

        @Override
        public MerchantOffer getOffer(Entity trader, Random random)
        {
            return new MerchantOffer(new ItemStack(Items.EMERALD, this.emeraldCount), new ItemStack(this.itemStack.getItem(), this.count), this.maxStock, this.expGiven, this.priceMultiplyer);
        }
    }
}

