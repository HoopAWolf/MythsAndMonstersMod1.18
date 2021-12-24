//package com.hoopawolf.mwaw.structure.piece;
//
//import com.hoopawolf.mwaw.entities.HunterEntity;
//import com.hoopawolf.mwaw.ref.Reference;
//import com.hoopawolf.mwaw.util.EntityRegistryHandler;
//import com.hoopawolf.mwaw.util.StructureRegistryHandler;
//import net.minecraft.core.BlockPos;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.entity.MobSpawnType;
//import net.minecraft.world.level.ChunkPos;
//import net.minecraft.world.level.ServerLevelAccessor;
//import net.minecraft.world.level.StructureFeatureManager;
//import net.minecraft.world.level.WorldGenLevel;
//import net.minecraft.world.level.block.Mirror;
//import net.minecraft.world.level.block.Rotation;
//import net.minecraft.world.level.chunk.ChunkGenerator;
//import net.minecraft.world.level.levelgen.structure.BoundingBox;
//import net.minecraft.world.level.levelgen.structure.StructurePiece;
//import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
//import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
//import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
//import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
//import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
//
//import java.util.List;
//import java.util.Random;
//
//public class CampPiece
//{
//    public static final ResourceLocation HUNTER_CAMP_LOC = new ResourceLocation(Reference.MOD_ID, "huntercamp");
//
//    public static void start(StructureManager templateManager, BlockPos pos, Rotation rotation, List<StructurePiece> pieceList, Random random)
//    {
//        int x = pos.getX();
//        int z = pos.getZ();
//
//        //This is how we factor in rotation for multi-piece structures.
//        //
//        //I would recommend using the OFFSET map above to have each piece at correct height relative of each other
//        //and keep the X and Z equal to 0. And then in rotations, have the centermost piece have a rotation
//        //of 0, 0, 0 and then have all other pieces' rotation be based off of the bottommost left corner of
//        //that piece (the corner that is smallest in X and Z).
//        //
//        //Lots of trial and error may be needed to get this right for your structure.
//        BlockPos rotationOffSet = new BlockPos(0, 0, 0).rotate(rotation);
//        BlockPos blockpos = rotationOffSet.offset(x, pos.getY(), z);
//        pieceList.add(new CampPiece.Piece(templateManager, HUNTER_CAMP_LOC, blockpos, rotation));
//    }
//
//    public static class Piece extends TemplateStructurePiece
//    {
//        private final ResourceLocation resourceLocation;
//        private final Rotation rotation;
//        private boolean isSpawned;
//
//        public Piece(StructureManager templateManagerIn, ResourceLocation resourceLocationIn, BlockPos pos, Rotation rotationIn)
//        {
//            super(StructureRegistryHandler.HUNTER_CAMP_FEATURE, 0);
//            this.resourceLocation = resourceLocationIn;
//            BlockPos blockpos = new BlockPos(0, 1, 0);
//            this.templatePosition = pos.offset(blockpos.getX(), blockpos.getY(), blockpos.getZ());
//            this.rotation = rotationIn;
//            this.setupPiece(templateManagerIn);
//        }
//
//
//        public Piece(StructureManager templateManagerIn, CompoundTag tagCompound)
//        {
//            super(StructureRegistryHandler.HUNTER_CAMP_FEATURE, tagCompound);
//            this.resourceLocation = new ResourceLocation(tagCompound.getString("StructureTemplate"));
//            this.rotation = Rotation.valueOf(tagCompound.getString("Rot"));
//            this.isSpawned = tagCompound.getBoolean("Spawned");
//            this.setupPiece(templateManagerIn);
//        }
//
//
//        private void setupPiece(StructureManager templateManager)
//        {
//            StructureTemplate template = templateManager.getOrCreate(this.resourceLocation);
//            StructurePlaceSettings placementsettings = (new StructurePlaceSettings()).setRotation(this.rotation).setMirror(Mirror.NONE);
//            this.setup(template, this.templatePosition, placementsettings);
//        }
//
//        @Override
//        protected void addAdditionalSaveData(StructurePieceSerializationContext p_192690_, CompoundTag tagCompound)
//        {
//            super.addAdditionalSaveData(p_192690_, tagCompound);
//            tagCompound.putString("StructureTemplate", this.resourceLocation.toString());
//            tagCompound.putString("Rot", this.rotation.name());
//            tagCompound.putBoolean("Spawned", this.isSpawned);
//        }
//
//        @Override
//        protected void handleDataMarker(String function, BlockPos pos, ServerLevelAccessor worldIn, Random random, BoundingBox sbb)
//        {
//        }
//
//        @Override
//        public void postProcess(WorldGenLevel p_230383_1_, StructureFeatureManager p_230383_2_, ChunkGenerator p_225577_2_, Random randomIn, BoundingBox structureBoundingBoxIn, ChunkPos chunkPos, BlockPos p_230383_7_)
//        {
//            StructurePlaceSettings placementsettings = (new StructurePlaceSettings()).setRotation(this.rotation).setMirror(Mirror.NONE);
//            BlockPos blockpos = new BlockPos(0, 1, 0);
//            this.templatePosition.offset(StructureTemplate.calculateRelativePosition(placementsettings, new BlockPos(-blockpos.getX(), 0, -blockpos.getZ())));
//
//            if (!isSpawned)
//            {
//                int i = randomIn.nextInt(2) + 1;
//
//                for (int j = 0; j < i; ++j)
//                {
//                    int l = this.getWorldX(2, 2);
//                    int i1 = this.getWorldY(1);
//                    int k = this.getWorldZ(2, 2);
//
//                    if (structureBoundingBoxIn.isInside(new BlockPos(l, i1, k)))
//                    {
//                        HunterEntity hunter = EntityRegistryHandler.HUNTER_ENTITY.get().create(p_230383_1_.getWorld());
//                        hunter.setPersistenceRequired();
//                        hunter.moveTo((double) l + 0.5D, i1, (double) k + 0.5D, 0.0F, 0.0F);
//                        hunter.finalizeSpawn(p_230383_1_, p_230383_1_.getCurrentDifficultyAt(new BlockPos(l, i1, k)), MobSpawnType.STRUCTURE, null, null);
//                        p_230383_1_.addFreshEntity(hunter);
//                    }
//                }
//
//                this.isSpawned = true;
//            }
//
//            super.postProcess(p_230383_1_, p_230383_2_, p_225577_2_, randomIn, structureBoundingBoxIn, chunkPos, p_230383_7_);
//        }
//    }
//}
