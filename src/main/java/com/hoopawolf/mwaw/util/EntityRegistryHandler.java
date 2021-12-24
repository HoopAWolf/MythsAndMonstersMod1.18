package com.hoopawolf.mwaw.util;

import com.hoopawolf.mwaw.entities.*;
import com.hoopawolf.mwaw.entities.model.*;
import com.hoopawolf.mwaw.entities.model.projectiles.FoxHeadModel;
import com.hoopawolf.mwaw.entities.model.projectiles.SpiritBombModel;
import com.hoopawolf.mwaw.entities.projectiles.*;
import com.hoopawolf.mwaw.entities.renderer.*;
import com.hoopawolf.mwaw.entities.renderer.projectiles.FoxHeadRenderer;
import com.hoopawolf.mwaw.entities.renderer.projectiles.GoldenArrowRenderer;
import com.hoopawolf.mwaw.entities.renderer.projectiles.SpiritBombRenderer;
import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.world.MobSpawnSettingsBuilder;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class EntityRegistryHandler
{
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, Reference.MOD_ID);

    //ENTITIES
    public static final RegistryObject<EntityType<FairyEntity>> FAIRY_ENTITY = ENTITIES.register("fairy", () -> EntityType.Builder.of(FairyEntity::new, MobCategory.CREATURE)
            .sized(0.4F, 0.8F)
            .setShouldReceiveVelocityUpdates(false)
            .build("fairy"));

    public static final RegistryObject<EntityType<SandWyrmEntity>> SAND_WYRM_ENTITY = ENTITIES.register("sandwyrm", () -> EntityType.Builder.of(SandWyrmEntity::new, MobCategory.CREATURE)
            .sized(1.5F, 1.0F)
            .setShouldReceiveVelocityUpdates(false)
            .build("sandwyrm"));

    public static final RegistryObject<EntityType<DendroidEntity>> DENDROID_ENTITY = ENTITIES.register("dendroid", () -> EntityType.Builder.of(DendroidEntity::new, MobCategory.CREATURE)
            .sized(1.0F, 1.5F)
            .setShouldReceiveVelocityUpdates(false)
            .build("dendroid"));

    public static final RegistryObject<EntityType<WolpertingerEntity>> WOLPERTINGER_ENTITY = ENTITIES.register("wolpertinger", () -> EntityType.Builder.of(WolpertingerEntity::new, MobCategory.CREATURE)
            .sized(1.0F, 0.9F)
            .setShouldReceiveVelocityUpdates(false)
            .build("wolpertinger"));

    public static final RegistryObject<EntityType<KitsuneEntity>> KITSUNE_ENTITY = ENTITIES.register("kitsune", () -> EntityType.Builder.of(KitsuneEntity::new, MobCategory.CREATURE)
            .sized(1.0F, 0.9F)
            .setShouldReceiveVelocityUpdates(false)
            .build("kitsune"));

    public static final RegistryObject<EntityType<HunterEntity>> HUNTER_ENTITY = ENTITIES.register("hunter", () -> EntityType.Builder.of(HunterEntity::new, MobCategory.CREATURE)
            .sized(0.7F, 1.85F)
            .setShouldReceiveVelocityUpdates(false)
            .build("hunter"));

    public static final RegistryObject<EntityType<ClayGolemEntity>> CLAY_GOLEM_ENTITY = ENTITIES.register("claygolem", () -> EntityType.Builder.of(ClayGolemEntity::new, MobCategory.CREATURE)
            .sized(1.4F, 2.4F)
            .setShouldReceiveVelocityUpdates(false)
            .build("claygolem"));

    public static final RegistryObject<EntityType<GoldenRamEntity>> GOLDEN_RAM_ENTITY = ENTITIES.register("goldenram", () -> EntityType.Builder.of(GoldenRamEntity::new, MobCategory.CREATURE)
            .sized(1.0F, 1.2F)
            .setShouldReceiveVelocityUpdates(false)
            .build("goldenram"));

    public static final RegistryObject<EntityType<DendroidElderEntity>> DENDROID_ELDER_ENTITY = ENTITIES.register("dendroidelder", () -> EntityType.Builder.of(DendroidElderEntity::new, MobCategory.CREATURE)
            .sized(1.4F, 2.9F)
            .setShouldReceiveVelocityUpdates(false)
            .build("dendroidelder"));

    public static final RegistryObject<EntityType<PyromancerEntity>> PYRO_ENTITY = ENTITIES.register("pyro", () -> EntityType.Builder.of(PyromancerEntity::new, MobCategory.CREATURE)
            .sized(0.75F, 2.3F)
            .setShouldReceiveVelocityUpdates(false)
            .build("pyro"));

    public static final RegistryObject<EntityType<FireSpiritEntity>> FIRE_SPIRIT_ENTITY = ENTITIES.register("firespirit", () -> EntityType.Builder.of(FireSpiritEntity::new, MobCategory.CREATURE)
            .sized(0.5F, 0.5F)
            .setShouldReceiveVelocityUpdates(false)
            .build("firespirit"));

    public static final RegistryObject<EntityType<DropBearEntity>> DROP_BEAR_ENTITY = ENTITIES.register("dropbear", () -> EntityType.Builder.of(DropBearEntity::new, MobCategory.CREATURE)
            .sized(0.7F, 0.9F)
            .setShouldReceiveVelocityUpdates(false)
            .build("dropbear"));

    public static final RegistryObject<EntityType<JackalopeEntity>> JACKALOPE_ENTITY = ENTITIES.register("jackalope", () -> EntityType.Builder.of(JackalopeEntity::new, MobCategory.CREATURE)
            .sized(1.0F, 1.0F)
            .setShouldReceiveVelocityUpdates(false)
            .build("jackalope"));

    public static final RegistryObject<EntityType<GiantEntity>> GIANT_ENTITY = ENTITIES.register("giant", () -> EntityType.Builder.of(GiantEntity::new, MobCategory.CREATURE)
            .sized(1.4F, 2.9F)
            .setShouldReceiveVelocityUpdates(false)
            .build("giant"));

    //PROJECTILE
    public static final RegistryObject<EntityType<GoldenArrowEntity>> GOLDEN_ARROW_ENTITY = ENTITIES.register("goldenarrow", () -> EntityType.Builder.<GoldenArrowEntity>of(GoldenArrowEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .build("goldenarrow"));

    public static final RegistryObject<EntityType<SapEntity>> SAP_ENTITY = ENTITIES.register("sap", () -> EntityType.Builder.<SapEntity>of(SapEntity::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
            .build("sap"));

    public static final RegistryObject<EntityType<ClayEntity>> CLAY_ENTITY = ENTITIES.register("clay", () -> EntityType.Builder.<ClayEntity>of(ClayEntity::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
            .build("clay"));

    public static final RegistryObject<EntityType<FoxHeadEntity>> FOX_HEAD_ENTITY = ENTITIES.register("foxspirit", () -> EntityType.Builder.<FoxHeadEntity>of(FoxHeadEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .build("foxspirit"));

    public static final RegistryObject<EntityType<SpiritBombEntity>> SPIRIT_BOMB_ENTITY = ENTITIES.register("spiritbomb", () -> EntityType.Builder.<SpiritBombEntity>of(SpiritBombEntity::new, MobCategory.MISC)
            .sized(3.0F, 3.0F)
            .build("spiritbomb"));

    public static void generateEntityWorldSpawn(ResourceLocation category, MobSpawnSettingsBuilder spawn)
    {
        registerEntityWorldSpawn(category, spawn, SAND_WYRM_ENTITY.get(), MobCategory.CREATURE, 1, 0, 2, new ResourceLocation[]{Biomes.DESERT.getRegistryName()});
        registerEntityWorldSpawn(category, spawn, FAIRY_ENTITY.get(), MobCategory.CREATURE, 10, 3, 3, new ResourceLocation[]{Biomes.FLOWER_FOREST.getRegistryName(), Biomes.SUNFLOWER_PLAINS.getRegistryName()});
        registerEntityWorldSpawn(category, spawn, FAIRY_ENTITY.get(), MobCategory.CREATURE, 3, 0, 3, new ResourceLocation[]{Biomes.FOREST.getRegistryName(), Biomes.BIRCH_FOREST.getRegistryName()});
        registerEntityWorldSpawn(category, spawn, FAIRY_ENTITY.get(), MobCategory.CREATURE, 1, 0, 3, new ResourceLocation[]{Biomes.SWAMP.getRegistryName()});
        registerEntityWorldSpawn(category, spawn, WOLPERTINGER_ENTITY.get(), MobCategory.CREATURE, 3, 3, 3, new ResourceLocation[]{Biomes.PLAINS.getRegistryName()});
        registerEntityWorldSpawn(category, spawn, WOLPERTINGER_ENTITY.get(), MobCategory.CREATURE, 10, 3, 3, new ResourceLocation[]{Biomes.FLOWER_FOREST.getRegistryName(), Biomes.SUNFLOWER_PLAINS.getRegistryName(), Biomes.WOODED_BADLANDS.getRegistryName(), Biomes.TAIGA.getRegistryName()});
        registerEntityWorldSpawn(category, spawn, KITSUNE_ENTITY.get(), MobCategory.CREATURE, 1, 0, 1, new ResourceLocation[]{Biomes.BIRCH_FOREST.getRegistryName(), Biomes.DARK_FOREST.getRegistryName()});
        registerEntityWorldSpawn(category, spawn, HUNTER_ENTITY.get(), MobCategory.CREATURE, 3, 0, 2, new ResourceLocation[]{Biomes.FOREST.getRegistryName(), Biomes.DARK_FOREST.getRegistryName(), Biomes.FLOWER_FOREST.getRegistryName(), Biomes.BIRCH_FOREST.getRegistryName()});

        registerEntityWorldSpawn(category, spawn, DENDROID_ENTITY.get(), MobCategory.MONSTER, 1, 0, 3, new ResourceLocation[]{Biomes.FOREST.getRegistryName(), Biomes.DARK_FOREST.getRegistryName()});
        registerEntityWorldSpawn(category, spawn, CLAY_GOLEM_ENTITY.get(), MobCategory.MONSTER, 1, 0, 1, new ResourceLocation[]{Biomes.BADLANDS.getRegistryName(), Biomes.SWAMP.getRegistryName()});
    }

    protected static void registerEntityWorldSpawn(ResourceLocation biomeName, MobSpawnSettingsBuilder spawn, EntityType<?> entity, MobCategory classification, int weight, int minGroup, int maxGroup, ResourceLocation[] biomes)
    {
        for (ResourceLocation biome : biomes)
        {
            if(biomeName.equals(biome))
            {
                spawn.addSpawn(classification, new MobSpawnSettings.SpawnerData(entity, weight, minGroup, maxGroup));
            }
        }
    }//TODO ADD KITSUNE SPAWN IN VILLAGE, SPAWNING RATE NEED FIX

    @OnlyIn(Dist.CLIENT)
    public static void registerEntityRenderer(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerEntityRenderer(FAIRY_ENTITY.get(), FairyRenderer::new);
        event.registerEntityRenderer(SAND_WYRM_ENTITY.get(), SandWyrmRenderer::new);
        event.registerEntityRenderer(DENDROID_ENTITY.get(), DendroidRenderer::new);
        event.registerEntityRenderer(WOLPERTINGER_ENTITY.get(), WolpertingerRenderer::new);
        event.registerEntityRenderer(KITSUNE_ENTITY.get(), KitsuneRenderer::new);
        event.registerEntityRenderer(HUNTER_ENTITY.get(), HunterRenderer::new);
        event.registerEntityRenderer(CLAY_GOLEM_ENTITY.get(), ClayGolemRenderer::new);
        event.registerEntityRenderer(GOLDEN_RAM_ENTITY.get(), GoldenRamRenderer::new);
        event.registerEntityRenderer(DENDROID_ELDER_ENTITY.get(), DendroidElderRenderer::new);
        event.registerEntityRenderer(PYRO_ENTITY.get(), PyromancerRenderer::new);
        event.registerEntityRenderer(FIRE_SPIRIT_ENTITY.get(), FireSpiritRenderer::new);
        event.registerEntityRenderer(DROP_BEAR_ENTITY.get(), DropBearRenderer::new);
        event.registerEntityRenderer(JACKALOPE_ENTITY.get(), JackalopeRenderer::new);
        event.registerEntityRenderer(GIANT_ENTITY.get(), GiantRenderer::new);

        event.registerEntityRenderer(GOLDEN_ARROW_ENTITY.get(), GoldenArrowRenderer::new);
        event.registerEntityRenderer(SAP_ENTITY.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(CLAY_ENTITY.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(FOX_HEAD_ENTITY.get(), FoxHeadRenderer::new);
        event.registerEntityRenderer(SPIRIT_BOMB_ENTITY.get(), SpiritBombRenderer::new);

        ItemBlockRenderTypes.setRenderLayer(ItemBlockRegistryHandler.FAIRY_MUSHROOM_BLOCK.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(ItemBlockRegistryHandler.DENDROID_ROOTS_BLOCK.get(), RenderType.cutout());
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerEntityLayer(EntityRenderersEvent.RegisterLayerDefinitions event)
    {
        event.registerLayerDefinition(DendroidElderModel.LAYER_LOCATION, DendroidElderModel::createBodyLayer);
        event.registerLayerDefinition(GoldenRamModel.LAYER_LOCATION, GoldenRamModel::createBodyLayer);
        event.registerLayerDefinition(ClayGolemModel.LAYER_LOCATION, ClayGolemModel::createBodyLayer);
        event.registerLayerDefinition(DendroidModel.LAYER_LOCATION, DendroidModel::createBodyLayer);
        event.registerLayerDefinition(KitsuneModel.LAYER_LOCATION, KitsuneModel::createBodyLayer);
        event.registerLayerDefinition(DropBearModel.LAYER_LOCATION, DropBearModel::createBodyLayer);
        event.registerLayerDefinition(FairyModel.LAYER_LOCATION, FairyModel::createBodyLayer);
        event.registerLayerDefinition(GiantModel.LAYER_LOCATION, GiantModel::createBodyLayer);
        event.registerLayerDefinition(JackalopeModel.LAYER_LOCATION, JackalopeModel::createBodyLayer);
        event.registerLayerDefinition(PyromancerModel.LAYER_LOCATION, PyromancerModel::createBodyLayer);
        event.registerLayerDefinition(SandWyrmModel.LAYER_LOCATION, SandWyrmModel::createBodyLayer);
        event.registerLayerDefinition(WolpertingerModel.LAYER_LOCATION, WolpertingerModel::createBodyLayer);
        event.registerLayerDefinition(SpiritBombModel.LAYER_LOCATION, SpiritBombModel::createBodyLayer);
        event.registerLayerDefinition(FoxHeadModel.LAYER_LOCATION, FoxHeadModel::createBodyLayer);
        event.registerLayerDefinition(HunterModel.LAYER_LOCATION, ()-> LayerDefinition.create(HunterModel.createBodyLayer(), 128, 128));
    }

    public static void registerEntityAttributes(EntityAttributeCreationEvent event)
    {
        event.put(FAIRY_ENTITY.get(), FairyEntity.func_234321_m_().build());
        event.put(SAND_WYRM_ENTITY.get(), SandWyrmEntity.func_234321_m_().build());
        event.put(DENDROID_ENTITY.get(), DendroidEntity.func_234321_m_().build());
        event.put(WOLPERTINGER_ENTITY.get(), WolpertingerEntity.func_234321_m_().build());
        event.put(KITSUNE_ENTITY.get(), KitsuneEntity.func_234321_m_().build());
        event.put(HUNTER_ENTITY.get(), HunterEntity.func_234321_m_().build());
        event.put(CLAY_GOLEM_ENTITY.get(), ClayGolemEntity.func_234321_m_().build());
        event.put(GOLDEN_RAM_ENTITY.get(), GoldenRamEntity.func_234321_m_().build());
        event.put(DENDROID_ELDER_ENTITY.get(), DendroidElderEntity.func_234321_m_().build());
        event.put(PYRO_ENTITY.get(), PyromancerEntity.func_234321_m_().build());
        event.put(FIRE_SPIRIT_ENTITY.get(), FireSpiritEntity.func_234321_m_().build());
        event.put(DROP_BEAR_ENTITY.get(), DropBearEntity.func_234321_m_().build());
        event.put(JACKALOPE_ENTITY.get(), JackalopeEntity.func_234321_m_().build());
        event.put(GIANT_ENTITY.get(), GiantEntity.func_234321_m_().build());
    }

    @SubscribeEvent
    public static void BiomeLoading(BiomeLoadingEvent event)
    {
        EntityRegistryHandler.generateEntityWorldSpawn(event.getName(), event.getSpawns());
    }
}
