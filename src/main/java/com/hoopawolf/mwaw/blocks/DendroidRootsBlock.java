package com.hoopawolf.mwaw.blocks;

import com.hoopawolf.mwaw.entities.DendroidElderEntity;
import com.hoopawolf.mwaw.entities.DendroidEntity;
import com.hoopawolf.mwaw.network.MWAWPacketHandler;
import com.hoopawolf.mwaw.network.packets.client.SpawnParticleMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Random;

public class DendroidRootsBlock extends BushBlock
{
    public DendroidRootsBlock(Block.Properties properties)
    {
        super(properties);
    }

    @Override
    public void entityInside(BlockState state, Level worldIn, BlockPos pos, Entity entityIn)
    {
        if (!(entityIn instanceof DendroidElderEntity) && !(entityIn instanceof DendroidEntity))
        {
            entityIn.hurt(DamageSource.MAGIC, 2.0F);
        }
    }

    @Override
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random random)
    {
        super.animateTick(stateIn, worldIn, pos, random);

        if (worldIn.random.nextInt(100) < 20)
        {
            int _iteration = worldIn.random.nextInt(10);
            Vec3 _vec = new Vec3(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F);
            SpawnParticleMessage spawnParticleMessage = new SpawnParticleMessage(_vec, new Vec3(0, -0.1f, 0), _iteration, 8, 0.5F);
            MWAWPacketHandler.packetHandler.sendToDimension(worldIn.dimension(), spawnParticleMessage);
        }
    }

    @Override
    public BlockBehaviour.OffsetType getOffsetType()
    {
        return BlockBehaviour.OffsetType.XYZ;
    }
}