package com.hoopawolf.mwaw.entities.ai;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;

import java.util.EnumSet;

public class MWAWMeleeAttackGoal extends Goal
{
    protected final PathfinderMob attacker;
    protected final int attackInterval = 20;
    private final double speedTowardsTarget;
    private final boolean longMemory;
    private final boolean canPenalize = false;
    protected int attackTick;
    private double runningSpeedTowardsTarget = 0;
    private Path path;
    private int delayCounter;
    private double targetX;
    private double targetY;
    private double targetZ;
    private long field_220720_k;
    private int failedPathFindingPenalty = 0;

    public MWAWMeleeAttackGoal(PathfinderMob creature, double speedIn, boolean useLongMemory)
    {
        this.attacker = creature;
        this.speedTowardsTarget = speedIn;
        this.runningSpeedTowardsTarget = this.speedTowardsTarget;
        this.longMemory = useLongMemory;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public MWAWMeleeAttackGoal(PathfinderMob creature, double speedIn, double runningSpeedIn, boolean useLongMemory)
    {
        this(creature, speedIn, useLongMemory);
        this.runningSpeedTowardsTarget = runningSpeedIn;
    }

    @Override
    public boolean canUse()
    {
        long i = this.attacker.level.getGameTime();
        if (i - this.field_220720_k < 20L)
        {
            return false;
        } else
        {
            this.field_220720_k = i;
            LivingEntity livingentity = this.attacker.getTarget();
            if (livingentity == null)
            {
                return false;
            } else if (!livingentity.isAlive())
            {
                return false;
            } else
            {
                if (canPenalize)
                {
                    if (--this.delayCounter <= 0)
                    {
                        this.path = this.attacker.getNavigation().createPath(livingentity, 0);
                        this.delayCounter = 4 + this.attacker.getRandom().nextInt(7);
                        return this.path != null;
                    } else
                    {
                        return true;
                    }
                }
                this.path = this.attacker.getNavigation().createPath(livingentity, 0);
                if (this.path != null)
                {
                    return true;
                } else
                {
                    return this.getAttackReachSqr(livingentity) >= this.attacker.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
                }
            }
        }
    }

    @Override
    public boolean canContinueToUse()
    {
        LivingEntity livingentity = this.attacker.getTarget();
        if (livingentity == null)
        {
            return false;
        } else if (!livingentity.isAlive())
        {
            return false;
        } else if (!this.longMemory)
        {
            return !this.attacker.getNavigation().isDone();
        } else if (!this.attacker.isWithinRestriction(livingentity.blockPosition()))
        {
            return false;
        } else
        {
            return !(livingentity instanceof Player) || !livingentity.isSpectator() && !((Player) livingentity).isCreative();
        }
    }

    @Override
    public void start()
    {
        this.attacker.getNavigation().moveTo(this.path, this.speedTowardsTarget);
        this.attacker.setAggressive(true);
        this.delayCounter = 0;
    }

    @Override
    public void stop()
    {
        LivingEntity livingentity = this.attacker.getTarget();
        if (!EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(livingentity))
        {
            this.attacker.setTarget(null);
        }

        this.attacker.setAggressive(false);
        this.attacker.getNavigation().stop();
    }

    @Override
    public void tick()
    {
        LivingEntity livingentity = this.attacker.getTarget();
        this.attacker.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
        double d0 = this.attacker.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
        --this.delayCounter;
        if ((this.longMemory || this.attacker.getSensing().hasLineOfSight(livingentity)) && this.delayCounter <= 0 && (this.targetX == 0.0D && this.targetY == 0.0D && this.targetZ == 0.0D || livingentity.distanceToSqr(this.targetX, this.targetY, this.targetZ) >= 1.0D || this.attacker.getRandom().nextFloat() < 0.05F))
        {
            this.targetX = livingentity.getX();
            this.targetY = livingentity.getY();
            this.targetZ = livingentity.getZ();
            this.delayCounter = 4 + this.attacker.getRandom().nextInt(7);
            if (this.canPenalize)
            {
                this.delayCounter += failedPathFindingPenalty;
                if (this.attacker.getNavigation().getPath() != null)
                {
                    Node finalPathPoint = this.attacker.getNavigation().getPath().getEndNode();
                    if (finalPathPoint != null && livingentity.distanceToSqr(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1)
                        failedPathFindingPenalty = 0;
                    else
                        failedPathFindingPenalty += 10;
                } else
                {
                    failedPathFindingPenalty += 10;
                }
            }
            if (d0 > 1024.0D)
            {
                this.delayCounter += 10;
            } else if (d0 > 256.0D)
            {
                this.delayCounter += 5;
            }

            if (!this.attacker.getNavigation().moveTo(livingentity, (d0 > 20.0D) ? this.runningSpeedTowardsTarget : this.speedTowardsTarget))
            {
                this.delayCounter += 15;
            }
        }

        this.attackTick = Math.max(this.attackTick - 1, 0);
        this.checkAndPerformAttack(livingentity, d0);
    }

    protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr)
    {
        double d0 = this.getAttackReachSqr(enemy);
        if (distToEnemySqr <= d0 && this.attackTick <= 0)
        {
            this.attackTick = 20;
            this.attacker.swing(InteractionHand.MAIN_HAND);
            this.attacker.doHurtTarget(enemy);
        }

    }

    protected double getAttackReachSqr(LivingEntity attackTarget)
    {
        return this.attacker.getBbWidth() * 2.0F * this.attacker.getBbWidth() * 2.0F + attackTarget.getBbWidth();
    }
}