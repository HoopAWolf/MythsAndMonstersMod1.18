//package com.hoopawolf.mwaw.util;
//
//import com.hoopawolf.mwaw.ref.Reference;
//import com.hoopawolf.mwaw.structure.CampStructure;
//import com.hoopawolf.mwaw.structure.piece.CampPiece;
//import net.minecraft.util.ResourceLocation;
//import net.minecraft.util.registry.Registry;
//import net.minecraft.world.biome.Biome;
//import net.minecraft.world.gen.feature.IFeatureConfig;
//import net.minecraft.world.gen.feature.NoneFeatureConfiguration;
//import net.minecraft.world.gen.feature.StructureFeature;
//import net.minecraft.world.gen.feature.structure.IStructurePieceType;
//import net.minecraft.world.gen.feature.structure.Structure;
//import net.minecraftforge.eventbus.api.IEventBus;
//import net.minecraftforge.fml.RegistryObject;
//import net.minecraftforge.registries.DeferredRegister;
//import net.minecraftforge.registries.ForgeRegistries;
//
//import java.util.Arrays;
//
////Credits to TelepathicGrunt for helping me out
//public class StructureRegistryHandler
//{
//    public static final DeferredRegister<Structure<?>> STRUCTURES = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, Reference.MOD_ID);
//
//    public static final RegistryObject<Structure<NoneFeatureConfiguration>> HUNTER_CAMP_STRUCTURE = STRUCTURES.register("huntercamp", () -> new CampStructure(NoneFeatureConfiguration.field_236558_a_));
//
//    public static final IStructurePieceType HUNTER_CAMP_FEATURE = registerStructurePiece(CampPiece.Piece::new, CampPiece.HUNTER_CAMP_LOC);
//
//    public static void generateStructureWorldSpawn()
//    {
//        Structure.field_236365_a_.put(HUNTER_CAMP_STRUCTURE.get().getStructureName(), HUNTER_CAMP_STRUCTURE.get());
//        registerStructureWorldSpawn(HUNTER_CAMP_STRUCTURE.get().func_236391_a_(NoneFeatureConfiguration.field_236559_b_), new Biome.Category[]{Biome.Category.FOREST, Biome.Category.TAIGA});
//    }
//
//    protected static void registerStructureWorldSpawn(StructureFeature<?, ?> structureIn, Biome.Category[] biomeCatList)
//    {
//        for (Biome biome : ForgeRegistries.BIOMES)
//        {
//            if (Arrays.asList(biomeCatList).contains(biome.getCategory()))
//            {
//                biome.func_235063_a_(structureIn);
//            }
//        }
//    }
//
//    public static void init(IEventBus _iEventBus)
//    {
//        STRUCTURES.register(_iEventBus);
//    }
//
//    public static <C extends IFeatureConfig> IStructurePieceType registerStructurePiece(IStructurePieceType pieceType, ResourceLocation key)
//    {
//        return Registry.register(Registry.STRUCTURE_PIECE, key, pieceType);
//    }
//}