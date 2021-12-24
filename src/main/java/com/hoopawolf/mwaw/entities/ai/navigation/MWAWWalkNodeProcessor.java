package com.hoopawolf.mwaw.entities.ai.navigation;

import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.*;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.Set;

public class MWAWWalkNodeProcessor extends WalkNodeEvaluator
{
    @Override
    public Node getStart()
    {
        int y;
        AABB bounds = this.mob.getBoundingBox();
        if (this.canFloat() && this.mob.isInWater())
        {
            y = (int) bounds.minY;
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(Mth.floor(this.mob.getX()), y, Mth.floor(this.mob.getZ()));
            for (Block block = this.level.getBlockState(pos).getBlock(); block == Blocks.WATER; block = this.level.getBlockState(pos).getBlock())
            {
                pos.setY(++y);
            }
        } else if (this.mob.isOnGround())
        {
            y = Mth.floor(bounds.minY + 0.5D);
        } else
        {
            y = Mth.floor(this.mob.getY());
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(Mth.floor(this.mob.getX()), y, Mth.floor(this.mob.getZ()));
            while (y > 0 && (this.level.getBlockState(pos).getMaterial() == Material.AIR || this.level.getBlockState(pos).isPathfindable(this.level, pos, PathComputationType.LAND)))
            {
                pos.setY(y--);
            }
            y++;
        }
        // account for node size
        float r = this.mob.getBbWidth() * 0.5F;
        int x = Mth.floor(this.mob.getX() - r);
        int z = Mth.floor(this.mob.getZ() - r);
        if (this.mob.getPathfindingMalus(this.getPathType(this.mob, x, y, z)) < 0.0F)
        {
            Set<BlockPos> diagonals = Sets.newHashSet();
            diagonals.add(new BlockPos(bounds.minX - r, y, bounds.minZ - r));
            diagonals.add(new BlockPos(bounds.minX - r, y, bounds.maxZ - r));
            diagonals.add(new BlockPos(bounds.maxX - r, y, bounds.minZ - r));
            diagonals.add(new BlockPos(bounds.maxX - r, y, bounds.maxZ - r));
            for (BlockPos p : diagonals)
            {
                BlockPathTypes pathnodetype = this.getPathType(this.mob, p.getX(), p.getY(), p.getZ());
                if (this.mob.getPathfindingMalus(pathnodetype) >= 0.0F)
                {
                    return this.getNode(p.getX(), p.getY(), p.getZ());
                }
            }
        }
        return this.getNode(x, y, z);
    }

    @Override
    public int getNeighbors(Node[] pathOptions, Node currentPoint)
    {
        int optionCount = 0;
        int step = 0;
        BlockPathTypes pathnodetype = this.getPathType(this.mob, currentPoint.x, currentPoint.y + 1, currentPoint.z);
        if (this.mob.getPathfindingMalus(pathnodetype) >= 0.0F)
        {
            step = Mth.floor(Math.max(1.0F, this.mob.maxUpStep));
        }
        double floor = getFloorLevel(this.level, new BlockPos(currentPoint.x, currentPoint.y, currentPoint.z));
        Node south = this.getSafePoint(currentPoint.x, currentPoint.y, currentPoint.z + 1, step, floor, Direction.SOUTH);
        Node west = this.getSafePoint(currentPoint.x - 1, currentPoint.y, currentPoint.z, step, floor, Direction.WEST);
        Node east = this.getSafePoint(currentPoint.x + 1, currentPoint.y, currentPoint.z, step, floor, Direction.EAST);
        Node north = this.getSafePoint(currentPoint.x, currentPoint.y, currentPoint.z - 1, step, floor, Direction.NORTH);
        if (south != null && !south.closed && south.costMalus >= 0.0F)
        {
            pathOptions[optionCount++] = south;
        }
        if (west != null && !west.closed && west.costMalus >= 0.0F)
        {
            pathOptions[optionCount++] = west;
        }
        if (east != null && !east.closed && east.costMalus >= 0.0F)
        {
            pathOptions[optionCount++] = east;
        }
        if (north != null && !north.closed && north.costMalus >= 0.0F)
        {
            pathOptions[optionCount++] = north;
        }
        boolean northPassable = north == null || north.type == BlockPathTypes.OPEN || north.costMalus != 0.0F;
        boolean southPassable = south == null || south.type == BlockPathTypes.OPEN || south.costMalus != 0.0F;
        boolean eastPassable = east == null || east.type == BlockPathTypes.OPEN || east.costMalus != 0.0F;
        boolean westPassable = west == null || west.type == BlockPathTypes.OPEN || west.costMalus != 0.0F;
        if (northPassable && westPassable)
        {
            Node northwest = this.getSafePoint(currentPoint.x - 1, currentPoint.y, currentPoint.z - 1, step, floor, Direction.NORTH);
            if (northwest != null && !northwest.closed && northwest.costMalus >= 0.0F)
            {
                pathOptions[optionCount++] = northwest;
            }
        }
        if (northPassable && eastPassable)
        {
            Node northeast = this.getSafePoint(currentPoint.x + 1, currentPoint.y, currentPoint.z - 1, step, floor, Direction.NORTH);
            if (northeast != null && !northeast.closed && northeast.costMalus >= 0.0F)
            {
                pathOptions[optionCount++] = northeast;
            }
        }
        if (southPassable && westPassable)
        {
            Node southwest = this.getSafePoint(currentPoint.x - 1, currentPoint.y, currentPoint.z + 1, step, floor, Direction.SOUTH);
            if (southwest != null && !southwest.closed && southwest.costMalus >= 0.0F)
            {
                pathOptions[optionCount++] = southwest;
            }
        }
        if (southPassable && eastPassable)
        {
            Node southeast = this.getSafePoint(currentPoint.x + 1, currentPoint.y, currentPoint.z + 1, step, floor, Direction.SOUTH);
            if (southeast != null && !southeast.closed && southeast.costMalus >= 0.0F)
            {
                pathOptions[optionCount++] = southeast;
            }
        }
        return optionCount;
    }

    @Nullable
    private Node getSafePoint(int x, int y, int z, int step, double floor, Direction dir)
    {
        Node result = null;
        BlockPos pos = new BlockPos(x, y, z);
        double d0 = getFloorLevel(this.level, pos);
        if (d0 - floor > 1.125D)
        {
            return null;
        }
        BlockPathTypes atNode = this.getPathType(this.mob, x, y, z);
        float malus = this.mob.getPathfindingMalus(atNode);
        double r = this.mob.getBbWidth() / 2.0D;
        if (malus >= 0.0F)
        {
            result = this.getNode(x, y, z);
            result.type = atNode;
            result.costMalus = Math.max(result.costMalus, malus);
        }
        if (atNode == BlockPathTypes.WALKABLE)
        {
            return result;
        }
        if (result == null && step > 0 && atNode != BlockPathTypes.FENCE && atNode != BlockPathTypes.TRAPDOOR)
        {
            result = this.getSafePoint(x, y + 1, z, step - 1, floor, dir);
            if (result != null && (result.type == BlockPathTypes.OPEN || result.type == BlockPathTypes.WALKABLE) && this.mob.getBbWidth() < 1.0F)
            {
                double px = (x - dir.getStepX()) + 0.5D;
                double pz = (z - dir.getStepZ()) + 0.5D;
                AABB axisalignedbb = new AABB(px - r, getFloorLevel(this.level, new BlockPos(px, y + 1, pz)) + 0.001D, pz - r, px + r, (double) this.mob.getBbHeight() + getFloorLevel(this.level, new BlockPos(result.x, result.y, result.z)) - 0.002D, pz + r);
                if (!this.level.noCollision(this.mob, axisalignedbb))
                {
                    result = null;
                }
            }
        }
        if (atNode == BlockPathTypes.OPEN)
        {
            // account for node size
            AABB collision = new AABB(
                    x - r + this.entityWidth * 0.5D, y + 0.001D, z - r + this.entityDepth * 0.5D,
                    x + r + this.entityWidth * 0.5D, y + this.mob.getBbHeight(), z + r + this.entityDepth * 0.5D
            );
            if (!this.level.noCollision(this.mob, collision))
            {
                return null;
            }
            if (this.mob.getBbWidth() >= 1.0F)
            {
                BlockPathTypes below = this.getPathType(this.mob, x, y - 1, z);
                if (below == BlockPathTypes.BLOCKED)
                {
                    result = this.getNode(x, y, z);
                    result.type = BlockPathTypes.WALKABLE;
                    result.costMalus = Math.max(result.costMalus, malus);
                    return result;
                }
            }
            int fallDistance = 0;
            while (y-- > 0 && atNode == BlockPathTypes.OPEN)
            {
                if (fallDistance++ >= this.mob.getMaxFallDistance())
                {
                    return null;
                }
                atNode = this.getPathType(this.mob, x, y, z);
                malus = this.mob.getPathfindingMalus(atNode);
                if (atNode != BlockPathTypes.OPEN && malus >= 0.0F)
                {
                    result = this.getNode(x, y, z);
                    result.type = atNode;
                    result.costMalus = Math.max(result.costMalus, malus);
                    break;
                }
                if (malus < 0.0F)
                {
                    return null;
                }
            }
        }
        return result;
    }

    private BlockPathTypes getPathType(Mob living, int x, int y, int z)
    {
        return this.getBlockPathType(this.level, x, y, z, living, this.entityWidth, this.entityHeight, this.entityDepth, this.canOpenDoors(), this.canPassDoors());
    }
}
