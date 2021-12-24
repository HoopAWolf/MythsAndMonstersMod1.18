package com.hoopawolf.mwaw.entities.ai.navigation;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.metrics.MetricCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.pathfinder.*;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MWAWPathFinder extends PathFinder
{
    private final BinaryHeap path = new BinaryHeap();
    private final Set<Node> closedSet = Sets.newHashSet();
    private final Node[] pathOptions = new Node[32];
    private final NodeEvaluator nodeProcessor;
    private final int field_215751_d;

    public MWAWPathFinder(NodeEvaluator processor, int p_i51280_2_)
    {
        super(processor, p_i51280_2_);
        this.nodeProcessor = processor;
        this.field_215751_d = p_i51280_2_;
    }

    @Override
    @Nullable
    public Path findPath(PathNavigationRegion p_227478_1_, Mob p_227478_2_, Set<BlockPos> p_227478_3_, float p_227478_4_, int p_227478_5_, float p_227478_6_)
    {
        this.path.clear();
        this.nodeProcessor.prepare(p_227478_1_, p_227478_2_);
        Node pathpoint = this.nodeProcessor.getStart();
        Map<Target, BlockPos> map = p_227478_3_.stream().collect(Collectors.toMap((p_224782_1_) ->
                this.nodeProcessor.getGoal(p_224782_1_.getX(), p_224782_1_.getY(), p_224782_1_.getZ()), Function.identity()));
        Path path = this.findPath(p_227478_1_.getProfiler(), pathpoint, map, p_227478_4_, p_227478_5_, p_227478_6_);
        this.nodeProcessor.done();
        return path;
    }

    @Nullable
    private Path findPath(ProfilerFiller p_227479_1_, Node p_164718_, Map<Target, BlockPos> p_227479_2_, float p_227479_3_, int p_227479_4_, float p_227479_5_)
    {
        p_227479_1_.push("find_path");
        p_227479_1_.markForCharting(MetricCategory.PATH_FINDING);
        Set<Target> set = p_227479_2_.keySet();
        p_164718_.g = 0.0F;
        p_164718_.h = this.getBestH(p_164718_, set);
        p_164718_.f = p_164718_.h;
        this.path.clear();
        this.closedSet.clear();
        this.path.insert(p_164718_);
        Set<Target> set2 = Sets.newHashSetWithExpectedSize(set.size());
        int i = 0;
        int j = (int) ((float) this.field_215751_d * p_227479_5_);

        while (!this.path.isEmpty())
        {
            ++i;
            if (i >= j)
            {
                break;
            }

            Node pathpoint = this.path.pop();
            pathpoint.closed = true;
            for (Target flaggedpathpoint : set)
            {
                if (pathpoint.distanceManhattan(flaggedpathpoint) <= (float) p_227479_4_)
                {
                    flaggedpathpoint.setReached();
                    set2.add(flaggedpathpoint);
                }
            }

            if (!set2.isEmpty())
            {
                break;
            }

            if (!(pathpoint.distanceTo(p_164718_) >= p_227479_3_))
            {
                int k = this.nodeProcessor.getNeighbors(this.pathOptions, pathpoint);

                for (int l = 0; l < k; ++l)
                {
                    Node pathpoint1 = this.pathOptions[l];
                    float f = pathpoint.distanceTo(pathpoint1);
                    pathpoint1.walkedDistance = pathpoint.walkedDistance + f;
                    float f1 = pathpoint.g + f + pathpoint1.costMalus;
                    if (pathpoint1.walkedDistance < p_227479_3_ && (!pathpoint1.inOpenSet() || f1 < pathpoint1.g))
                    {
                        pathpoint1.cameFrom = pathpoint;
                        pathpoint1.g = f1;
                        pathpoint1.h = this.getBestH(pathpoint1, set) * 1.5F;
                        if (pathpoint1.inOpenSet())
                        {
                            this.path.changeCost(pathpoint1, pathpoint1.g + pathpoint1.h);
                        } else
                        {
                            pathpoint1.f = pathpoint1.g + pathpoint1.h;
                            this.path.insert(pathpoint1);
                        }
                    }
                }
            }
        }

        Optional<Path> optional = !set2.isEmpty() ? set2.stream().map((p_224778_2_) ->
        {
            return this.createPath(p_224778_2_.getBestNode(), p_227479_2_.get(p_224778_2_), true);
        }).min(Comparator.comparingInt(Path::getNodeCount)) : set.stream().map((p_224777_2_) ->
        {
            return this.createPath(p_224777_2_.getBestNode(), p_227479_2_.get(p_224777_2_), false);
        }).min(Comparator.comparingDouble(Path::getDistToTarget).thenComparingInt(Path::getNodeCount));
        return !optional.isPresent() ? null : optional.get();
    }

    private float getBestH(Node p_224776_1_, Set<Target> p_224776_2_)
    {
        float f = Float.MAX_VALUE;

        for (Target flaggedpathpoint : p_224776_2_)
        {
            float f1 = p_224776_1_.distanceTo(flaggedpathpoint);
            flaggedpathpoint.updateBest(f1, p_224776_1_);
            f = Math.min(f1, f);
        }

        return f;
    }

    private Path createPath(Node p_224780_1_, BlockPos p_224780_2_, boolean p_224780_3_)
    {
        List<Node> list = Lists.newArrayList();
        Node pathpoint = p_224780_1_;
        list.add(0, p_224780_1_);

        while (pathpoint.cameFrom != null)
        {
            pathpoint = pathpoint.cameFrom;
            list.add(0, pathpoint);
        }

        return new PatchedPath(list, p_224780_2_, p_224780_3_);
    }

    static class PatchedPath extends Path
    {

        public PatchedPath(List<Node> p_i51804_1_, BlockPos p_i51804_2_, boolean p_i51804_3_)
        {
            super(p_i51804_1_, p_i51804_2_, p_i51804_3_);
        }

        @Override
        public Vec3 getEntityPosAtNode(Entity entity, int index)
        {
            Node point = this.getNode(index);
            double d0 = point.x + Mth.floor(entity.getBbWidth() + 1.0F) * 0.5D;
            double d1 = point.y;
            double d2 = point.z + Mth.floor(entity.getBbWidth() + 1.0F) * 0.5D;
            return new Vec3(d0, d1, d2);
        }
    }
}
