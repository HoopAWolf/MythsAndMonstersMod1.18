package com.hoopawolf.mwaw.structure;

//public class CampStructure extends Structure<NoneFeatureConfiguration>
//{
//    public CampStructure(Codec<NoneFeatureConfiguration> p_i231997_1_)
//    {
//        super(p_i231997_1_);
//    }
//
//    @Override
//    public String getStructureName()
//    {
//        return CampPiece.HUNTER_CAMP_LOC.toString();
//    }
//
//    protected ChunkPos getStartPositionForPosition(ChunkGenerator chunkGenerator, Random random, int x, int z, int spacingOffsetsX, int spacingOffsetsZ)
//    {
//        int featureDistance = 12;
//        int featureSeparation = 7;
//
//        int xTemp = x + featureDistance * spacingOffsetsX;
//        int zTemp = z + featureDistance * spacingOffsetsZ;
//        int validChunkX = (xTemp < 0 ? xTemp - featureDistance + 1 : xTemp) / featureDistance;
//        int validChunkZ = (zTemp < 0 ? zTemp - featureDistance + 1 : zTemp) / featureDistance;
//        ((WorldgenRandom) random).setLargeFeatureWithSalt(62353535, x, z, 62226333);
//        validChunkX *= featureDistance;
//        validChunkZ *= featureDistance;
//        validChunkX += random.nextInt(featureDistance - featureSeparation) + random.nextInt(featureDistance - featureSeparation) / 2;
//        validChunkZ += random.nextInt(featureDistance - featureSeparation) + random.nextInt(featureDistance - featureSeparation) / 2;
//        return new ChunkPos(validChunkX, validChunkZ);
//    }
//
//    @Override
//    protected boolean isFeatureChunk(ChunkGenerator p_230363_1_, BiomeSource p_230363_2_, long p_230363_3_, WorldgenRandom p_230363_5_, int p_230363_6_, int p_230363_7_, Biome p_230363_8_, ChunkPos p_230363_9_, NoneFeatureConfiguration p_230363_10_)
//    {
//        ChunkPos chunkpos = this.getStartPositionForPosition(p_230363_1_, p_230363_5_, p_230363_6_, p_230363_7_, 0, 0);
//
//        if (p_230363_6_ == chunkpos.x && p_230363_7_ == chunkpos.z && p_230363_5_.nextInt(100) < 70)
//        {
//            return p_230363_2_.canGenerateStructure(this);
//        }
//
//        return false;
//    }
//
//    @Override
//    public Structure.StructureStartFactory<NoneFeatureConfiguration> getStartFactory()
//    {
//        return CampStructure.Start::new;
//    }
//
//    @Override
//    public GenerationStage.Decoration func_236396_f_()
//    {
//        return GenerationStage.Decoration.SURFACE_STRUCTURES;
//    }
//
//    public static class Start extends StructureStart<NoneFeatureConfiguration>
//    {
//        public Start(Structure<NoneFeatureConfiguration> structureIn, int chunkX, int chunkZ, BoundingBox mutableBoundingBox, int referenceIn, long seedIn)
//        {
//            super(structureIn, chunkX, chunkZ, mutableBoundingBox, referenceIn, seedIn);
//        }
//
//        @Override
//        public void func_230364_a_(ChunkGenerator generator, StructureManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn, NoneFeatureConfiguration p_230364_6_)
//        {
//            Rotation rotation = Rotation.values()[this.random.nextInt(Rotation.values().length)];
//
//            int x = (chunkX << 4);
//            int z = (chunkZ << 4);
//
//            int surfaceY = generator.getNoiseHeight(x, z, Heightmap.Type.WORLD_SURFACE_WG) - 1;
//            BlockPos blockpos = new BlockPos(x, surfaceY, z);
//
//            CampPiece.start(templateManagerIn, blockpos, rotation, this.components, this.random);
//
//            this.recalculateStructureSize();
//
//            Reference.LOGGER.log(Level.DEBUG, "Hunter Camp at " + (blockpos.getX()) + " " + blockpos.getY() + " " + (blockpos.getZ()));
//        }
//    }
//}