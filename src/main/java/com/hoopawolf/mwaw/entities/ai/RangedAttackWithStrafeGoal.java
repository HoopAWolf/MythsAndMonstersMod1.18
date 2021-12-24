package com.hoopawolf.mwaw.entities.ai;

import com.hoopawolf.mwaw.entities.DendroidEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;

import java.util.EnumSet;

public class RangedAttackWithStrafeGoal extends Goal
{
    private final Mob entityHost;
    private final RangedAttackMob rangedAttackEntityHost;
    private final double entityMoveSpeed;
    private final int attackIntervalMin;
    private final int maxRangedAttackTime;
    private final float attackRadius;
    private final float maxAttackDistance;
    private LivingEntity attackTarget;
    private int rangedAttackTime = -1;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;

    public RangedAttackWithStrafeGoal(RangedAttackMob attacker, double movespeed, int maxAttackTime, float maxAttackDistanceIn)
    {
        this(attacker, movespeed, maxAttackTime, maxAttackTime, maxAttackDistanceIn);
    }

    public RangedAttackWithStrafeGoal(RangedAttackMob attacker, double movespeed, int minAttackTime, int maxAttackTime, float maxAttackDistanceIn)
    {
        if (!(attacker instanceof LivingEntity))
        {
            throw new IllegalArgumentException("ArrowAttackGoal requires Mob implements RangedAttackMob");
        } else
        {
            this.rangedAttackEntityHost = attacker;
            this.entityHost = (Mob) attacker;
            this.entityMoveSpeed = movespeed;
            this.attackIntervalMin = minAttackTime;
            this.maxRangedAttackTime = maxAttackTime;
            this.attackRadius = maxAttackDistanceIn;
            this.maxAttackDistance = maxAttackDistanceIn * maxAttackDistanceIn;
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
        }
    }

    @Override
    public boolean canUse()
    {
        LivingEntity livingentity = this.entityHost.getTarget();
        if (livingentity != null && livingentity.isAlive())
        {
            attackTarget = livingentity;
            return true;
        } else
        {
            return false;
        }
    }

    @Override
    public boolean canContinueToUse()
    {
        return (this.canUse() || !this.entityHost.getNavigation().isDone());
    }

    @Override
    public void stop()
    {
        this.attackTarget = null;
        this.seeTime = 0;
        this.rangedAttackTime = -1;
    }

    @Override
    public void tick()
    {
        if (attackTarget != null)
        {
            double d0 = this.entityHost.distanceToSqr(attackTarget.getX(), attackTarget.getY(), attackTarget.getZ());
            boolean flag = this.entityHost.getSensing().hasLineOfSight(attackTarget);
            boolean flag1 = this.seeTime > 0;
            if (flag != flag1)
            {
                this.seeTime = 0;
            }

            if (flag)
            {
                ++this.seeTime;
            } else
            {
                --this.seeTime;
            }

            if (!(d0 > (double) this.maxAttackDistance) && this.seeTime >= 20)
            {
                this.entityHost.getNavigation().stop();
                ++this.strafingTime;
            } else
            {
                this.entityHost.getNavigation().moveTo(attackTarget, this.entityMoveSpeed);
                this.strafingTime = -1;
            }

            if (this.strafingTime >= 20)
            {
                if ((double) this.entityHost.getRandom().nextFloat() < 0.3D)
                {
                    this.strafingClockwise = !this.strafingClockwise;
                }

                if ((double) this.entityHost.getRandom().nextFloat() < 0.3D)
                {
                    this.strafingBackwards = !this.strafingBackwards;
                }

                this.strafingTime = 0;
            }

            if (this.strafingTime > -1)
            {
                if (d0 > (double) (this.maxAttackDistance * 0.75F))
                {
                    this.strafingBackwards = false;
                } else if (d0 < (double) (this.maxAttackDistance * 0.25F))
                {
                    this.strafingBackwards = true;
                }

                this.entityHost.getMoveControl().strafe(this.strafingBackwards ? -(float) entityMoveSpeed : (float) entityMoveSpeed, this.strafingClockwise ? (float) entityMoveSpeed : -(float) entityMoveSpeed);
                this.entityHost.lookAt(attackTarget, 30.0F, 30.0F);
            } else
            {
                this.entityHost.getLookControl().setLookAt(attackTarget, 30.0F, 30.0F);
            }

            if (--this.rangedAttackTime <= 0)
            {
                if (flag)
                {
                    float f = Mth.sqrt((float) d0) / this.attackRadius;
                    float lvt_5_1_ = Mth.clamp(f, 0.1F, 1.0F);
                    this.rangedAttackEntityHost.performRangedAttack(this.attackTarget, lvt_5_1_);
                    this.rangedAttackTime = Mth.nextInt(entityHost.level.random, this.attackIntervalMin, this.maxRangedAttackTime);
                    if (this.rangedAttackEntityHost instanceof DendroidEntity)
                    {
                        ((DendroidEntity) this.rangedAttackEntityHost).setIsShooting(true);
                    }
                } else if (this.rangedAttackTime < 0)
                {
                    this.rangedAttackTime = Mth.nextInt(entityHost.level.random, this.attackIntervalMin, this.maxRangedAttackTime);
                }
            }
        }
    }
}