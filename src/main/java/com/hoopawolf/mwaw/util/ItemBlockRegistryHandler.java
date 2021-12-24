package com.hoopawolf.mwaw.util;

import com.hoopawolf.mwaw.blocks.DendroidRootsBlock;
import com.hoopawolf.mwaw.blocks.FairyMushroomBlock;
import com.hoopawolf.mwaw.items.BadAppleItem;
import com.hoopawolf.mwaw.items.MWAWSpawnEggItem;
import com.hoopawolf.mwaw.items.ShardItem;
import com.hoopawolf.mwaw.items.weapons.*;
import com.hoopawolf.mwaw.ref.Reference;
import com.hoopawolf.mwaw.tab.MWAWItemGroup;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class ItemBlockRegistryHandler
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Reference.MOD_ID);

    public static final FoodProperties BAD_APPLE_STAT = (new FoodProperties.Builder()).nutrition(4).saturationMod(1.2F).alwaysEat().build();

    //ITEMS
    public static final RegistryObject<Item> FIRE_SHARD = ITEMS.register("fireshard", () -> new ShardItem());
    public static final RegistryObject<Item> WATER_SHARD = ITEMS.register("watershard", () -> new ShardItem());
    public static final RegistryObject<Item> LIGHTNING_SHARD = ITEMS.register("lightningshard", () -> new ShardItem());
    public static final RegistryObject<Item> EARTH_SHARD = ITEMS.register("earthshard", () -> new ShardItem());
    public static final RegistryObject<Item> LIGHT_SHARD = ITEMS.register("lightshard", () -> new ShardItem());
    public static final RegistryObject<Item> DARK_SHARD = ITEMS.register("darkshard", () -> new ShardItem());
    public static final RegistryObject<Item> ICE_SHARD = ITEMS.register("iceshard", () -> new ShardItem());
    public static final RegistryObject<Item> NATURE_SHARD = ITEMS.register("natureshard", () -> new ShardItem());
    public static final RegistryObject<Item> AIR_SHARD = ITEMS.register("airshard", () -> new ShardItem());
    public static final RegistryObject<Item> SAND_SHARD = ITEMS.register("sandshard", () -> new ShardItem());
    public static final RegistryObject<Item> MAGICAL_SEED = ITEMS.register("magicalseed", () -> new Item(new Item.Properties().stacksTo(16).tab(MWAWItemGroup.instance)));

    public static final RegistryObject<Item> SAP = ITEMS.register("sap", () -> new Item(new Item.Properties().stacksTo(64)));
    public static final RegistryObject<Item> GOLDEN_ARROW = ITEMS.register("goldenarrow", () -> new GoldenArrowItem(new Item.Properties().stacksTo(16).tab(MWAWItemGroup.instance)));
    public static final RegistryObject<Item> HARDENED_LEATHER = ITEMS.register("hardenedleather", () -> new Item(new Item.Properties().tab(MWAWItemGroup.instance)));
    public static final RegistryObject<Item> SAND_WYRM_TUSK = ITEMS.register("sandwyrmtusk", () -> new Item(new Item.Properties().tab(MWAWItemGroup.instance)));
    public static final RegistryObject<Item> SAND_SCALE = ITEMS.register("sandscale", () -> new Item(new Item.Properties().tab(MWAWItemGroup.instance)));
    public static final RegistryObject<Item> CELESTIAL_STAR_DUST = ITEMS.register("celestialstardust", () -> new Item(new Item.Properties().stacksTo(16).tab(MWAWItemGroup.instance)));
    public static final RegistryObject<Item> VOID_STAR_DUST = ITEMS.register("voidstardust", () -> new Item(new Item.Properties().stacksTo(16).tab(MWAWItemGroup.instance)));
    public static final RegistryObject<Item> DENDROID_ROOT = ITEMS.register("dendroidroot", () -> new Item(new Item.Properties().stacksTo(1).tab(MWAWItemGroup.instance)));
    public static final RegistryObject<Item> DENDROID_EYE = ITEMS.register("dendroideye", () -> new Item(new Item.Properties().stacksTo(1).tab(MWAWItemGroup.instance)));
    public static final RegistryObject<Item> ANTLER = ITEMS.register("antler", () -> new Item(new Item.Properties().tab(MWAWItemGroup.instance)));
    public static final RegistryObject<Item> FAIRY_DUST = ITEMS.register("fairydust", () -> new Item(new Item.Properties().tab(MWAWItemGroup.instance)));
    public static final RegistryObject<Item> SCALE_MAIL = ITEMS.register("scalemail", () -> new Item(new Item.Properties().tab(MWAWItemGroup.instance)));
    public static final RegistryObject<Item> TAINTED_SEED = ITEMS.register("taintedseed", () -> new Item(new Item.Properties().stacksTo(16).tab(MWAWItemGroup.instance)));

    public static final RegistryObject<Item> BAD_APPLE = ITEMS.register("badapple", () -> new BadAppleItem(new Item.Properties().tab(MWAWItemGroup.instance).food(BAD_APPLE_STAT)));

    public static final RegistryObject<Item> FIRE_EGG = ITEMS.register("fireegg", () -> new Item(new Item.Properties().stacksTo(1).tab(MWAWItemGroup.instance)));

    public static final RegistryObject<Item> FIRE_STAFF = ITEMS.register("firestaff", () -> new Item(new Item.Properties().tab(MWAWItemGroup.instance)));

    public static final RegistryObject<Item> DENDROID_SWORD = ITEMS.register("dendroidsword", () -> new DendroidSwordItem(Tiers.DIAMOND, 3, -2.5f, new Item.Properties().durability(359)));
    public static final RegistryObject<Item> MARROW_SWORD = ITEMS.register("marrowsword", () -> new MarrowSwordItem(Tiers.DIAMOND, 4, -2.5f, new Item.Properties().durability(1000)));
    public static final RegistryObject<Item> BONE_DAGGER = ITEMS.register("bonedagger", () -> new BoneDaggerItem(Tiers.DIAMOND, 4, -2.5f, new Item.Properties().durability(1000)));
    public static final RegistryObject<Item> GOLDEN_BOW = ITEMS.register("goldenbow", () -> new GoldenBowItem(new Item.Properties().stacksTo(1).tab(MWAWItemGroup.instance).rarity(Rarity.UNCOMMON)));

    //BLOCKS
    public static final RegistryObject<Block> FAIRY_MUSHROOM_BLOCK = BLOCKS.register("fairymushroom", () -> new FairyMushroomBlock(Block.Properties.of(Material.PLANT).noCollission().randomTicks().sound(SoundType.GRASS).lightLevel((p_235464_0_) -> 5)));
    public static final RegistryObject<Item> FAIRY_MUSHROOM_ITEM = ITEMS.register("fairymushroom", () -> new BlockItem(FAIRY_MUSHROOM_BLOCK.get(), new Item.Properties().tab(MWAWItemGroup.instance)));
    public static final RegistryObject<Block> DENDROID_ROOTS_BLOCK = BLOCKS.register("dendroidroots", () -> new DendroidRootsBlock(Block.Properties.of(Material.PLANT).noCollission().randomTicks().sound(SoundType.GRASS).strength(0.5F)));
    public static final RegistryObject<Item> DENDROID_ROOTS_ITEM = ITEMS.register("dendroidroots", () -> new BlockItem(DENDROID_ROOTS_BLOCK.get(), new Item.Properties().tab(MWAWItemGroup.instance)));
    public static final RegistryObject<Block> CORPSE_WOOD_BLOCK = BLOCKS.register("corpsewood", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.WOOD, MaterialColor.COLOR_LIGHT_GRAY).strength(2.0F).sound(SoundType.WOOD)));
    public static final RegistryObject<Item> CORPSE_WOOD_ITEM = ITEMS.register("corpsewood", () -> new BlockItem(CORPSE_WOOD_BLOCK.get(), new Item.Properties().tab(MWAWItemGroup.instance)));

    //SPAWN EGGS
//    public static final RegistryObject<MWAWSpawnEggItem> FAIRY_SPAWN_EGG = ITEMS.register("fairyspawnegg", () -> new MWAWSpawnEggItem(EntityRegistryHandler.FAIRY_ENTITY, 0x15153F, 0x153F3F, new Item.Properties().tab(MWAWItemGroup.instance)));
//    public static final RegistryObject<MWAWSpawnEggItem> SAND_WYRM_SPAWN_EGG = ITEMS.register("sandwyrmspawnegg", () -> new MWAWSpawnEggItem(EntityRegistryHandler.SAND_WYRM_ENTITY, 0x2A2A00, 0x3F3F15, new Item.Properties().tab(MWAWItemGroup.instance)));
//    public static final RegistryObject<MWAWSpawnEggItem> DENDROID_SPAWN_EGG = ITEMS.register("dendroidspawnegg", () -> new MWAWSpawnEggItem(EntityRegistryHandler.DENDROID_ENTITY, 0x2A2A00, 0x153F3F, new Item.Properties().tab(MWAWItemGroup.instance)));
//    public static final RegistryObject<MWAWSpawnEggItem> WOLPERTINGER_SPAWN_EGG = ITEMS.register("wolpertingerspawnegg", () -> new MWAWSpawnEggItem(EntityRegistryHandler.WOLPERTINGER_ENTITY, 0x2A2A00, 0x153F3F, new Item.Properties().tab(MWAWItemGroup.instance)));
//    public static final RegistryObject<MWAWSpawnEggItem> KITSUNE_SPAWN_EGG = ITEMS.register("kitsunespawnegg", () -> new MWAWSpawnEggItem(EntityRegistryHandler.KITSUNE_ENTITY, 0x153F3F, 0x15153F, new Item.Properties().tab(MWAWItemGroup.instance)));
//    public static final RegistryObject<MWAWSpawnEggItem> HUNTER_SPAWN_EGG = ITEMS.register("hunterspawnegg", () -> new MWAWSpawnEggItem(EntityRegistryHandler.HUNTER_ENTITY, 0x153F3F, 0x15153F, new Item.Properties().tab(MWAWItemGroup.instance)));
//    public static final RegistryObject<MWAWSpawnEggItem> CLAY_GOLEM_SPAWN_EGG = ITEMS.register("claygolemspawnegg", () -> new MWAWSpawnEggItem(EntityRegistryHandler.CLAY_GOLEM_ENTITY, 0x153F3F, 0x15153F, new Item.Properties().tab(MWAWItemGroup.instance)));
//    public static final RegistryObject<MWAWSpawnEggItem> GOLDEN_RAM_SPAWN_EGG = ITEMS.register("goldenramspawnegg", () -> new MWAWSpawnEggItem(EntityRegistryHandler.GOLDEN_RAM_ENTITY, 0x15235F, 0x16234F, new Item.Properties().tab(MWAWItemGroup.instance)));
//    public static final RegistryObject<MWAWSpawnEggItem> DENDROID_ELDER_SPAWN_EGG = ITEMS.register("dendroidelderspawnegg", () -> new MWAWSpawnEggItem(EntityRegistryHandler.DENDROID_ELDER_ENTITY, 0x15235F, 0x16234F, new Item.Properties().tab(MWAWItemGroup.instance)));
//    public static final RegistryObject<MWAWSpawnEggItem> PYRO_SPAWN_EGG = ITEMS.register("pyrospawnegg", () -> new MWAWSpawnEggItem(EntityRegistryHandler.PYRO_ENTITY, 0x15153F, 0x20234F, new Item.Properties().tab(MWAWItemGroup.instance)));
//    public static final RegistryObject<MWAWSpawnEggItem> DROP_BEAR_SPAWN_EGG = ITEMS.register("dropbearspawnegg", () -> new MWAWSpawnEggItem(EntityRegistryHandler.DROP_BEAR_ENTITY, 0x15153F, 0x20234F, new Item.Properties().tab(MWAWItemGroup.instance)));
//    public static final RegistryObject<MWAWSpawnEggItem> JACKALOPE_SPAWN_EGG = ITEMS.register("jackalopespawnegg", () -> new MWAWSpawnEggItem(EntityRegistryHandler.JACKALOPE_ENTITY, 0x15153F, 0x20234F, new Item.Properties().tab(MWAWItemGroup.instance)));
//    public static final RegistryObject<MWAWSpawnEggItem> GIANT_SPAWN_EGG = ITEMS.register("giantspawnegg", () -> new MWAWSpawnEggItem(EntityRegistryHandler.GIANT_ENTITY, 0x15153F, 0x20234F, new Item.Properties().tab(MWAWItemGroup.instance)));

    public static void init(IEventBus _iEventBus)
    {
        ITEMS.register(_iEventBus);
        BLOCKS.register(_iEventBus);
    }

    public static void generateBlockWorldSpawn()
    {
//        registerBlockWorldSpawn(GenerationStep.Decoration.VEGETAL_DECORATION,
//                Feature.RANDOM_PATCH.place(new RandomPatchConfiguration.Builder(new SimpleBlockStateProvider(FAIRY_MUSHROOM_BLOCK.get().getDefaultState()), new SimpleBlockPlacer()).tries(64).func_227317_b_().build()).withPlacement(Placement.CHANCE_HEIGHTMAP_DOUBLE.configure(new ChanceConfig(8))),
//                new Biome[]{Biomes.FOREST, Biomes.BIRCH_FOREST, Biomes.BIRCH_FOREST_HILLS, Biomes.TALL_BIRCH_FOREST, Biomes.FLOWER_FOREST});
    }

//    protected static void registerBlockWorldSpawn(GenerationStage.Decoration decoration, ConfiguredFeature<?, ?> featureIn, Biome[] biomes)
//    {
//        for (Biome biome : biomes)
//        {
//            biome.addFeature(decoration, featureIn);
//        }
//    }

    public static void registerItemModelProperties()
    {
        ItemProperties.register(GOLDEN_BOW.get(), new ResourceLocation("pull"),
                (p_239427_0_, p_239427_1_, p_239427_2_, p_174633_) ->
                {

                    if (p_239427_2_ == null)
                    {
                        return 0.0F;
                    } else
                    {
                        return !(p_239427_2_.getUseItem().getItem() instanceof GoldenBowItem) ? 0.0F : (float) (p_239427_0_.getUseDuration() - p_239427_2_.getUseItemRemainingTicks()) / 20.0F;

                    }
                });

        ItemProperties.register(GOLDEN_BOW.get(), new ResourceLocation("pulling"),
                (p_210309_0_, p_210309_1_, p_210309_2_, p_174633_) ->
                {
                    return p_210309_2_ != null && p_210309_2_.isUsingItem() && p_210309_2_.getUseItem() == p_210309_0_ ? 1.0F : 0.0F;
                });
    }

}