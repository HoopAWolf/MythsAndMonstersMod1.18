package com.hoopawolf.mwaw.blocks;

import com.hoopawolf.mwaw.network.MWAWPacketHandler;
import com.hoopawolf.mwaw.network.packets.client.SpawnParticleMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class FairyMushroomBlock extends MushroomBlock
{
    public FairyMushroomBlock(BlockBehaviour.Properties properties)
    {
        super(properties, null);
    }

    @Override
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random random)
    {
        super.animateTick(stateIn, worldIn, pos, random);

        if (worldIn.random.nextInt(100) < 20)
        {
            int _iteration = worldIn.random.nextInt(10);
            Vec3 _vec = new Vec3(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);
            SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(_vec, new Vec3(0, -0.1f, 0), _iteration, 0, 0.5F);
            MWAWPacketHandler.packetHandler.sendToDimension(worldIn.dimension(), spawnParticleMessage);
        }
    }

    @Override
    public boolean isBonemealSuccess(Level worldIn, Random random, BlockPos pos, BlockState state)
    {
        return false;
    }

    @Override
    public boolean isValidBonemealTarget(BlockGetter worldIn, BlockPos pos, BlockState state, boolean isClient)
    {
        return false;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader worldIn, BlockPos pos)
    {
        BlockPos blockpos = pos.below();
        BlockState blockstate = worldIn.getBlockState(blockpos);
        Block block = blockstate.getBlock();
        return block == Blocks.DIRT || block == Blocks.GRASS_BLOCK;
    }
}
