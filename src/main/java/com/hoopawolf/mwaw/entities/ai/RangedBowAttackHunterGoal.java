package com.hoopawolf.mwaw.entities.ai;

import com.hoopawolf.mwaw.entities.HunterEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ProjectileWeaponItem;

import java.util.EnumSet;
import java.util.function.Predicate;

public class RangedBowAttackHunterGoal<T extends PathfinderMob & RangedAttackMob> extends Goal
{
    private final T entity;
    private final double moveSpeedAmp;
    private final float maxAttackDistance;
    private final int attackCooldown;
    private int attackTime = -1;
    private int seeTime;
    private boolean strafingClockwise;
    private boolean strafingBackwards;
    private int strafingTime = -1;

    public RangedBowAttackHunterGoal(T mob, double moveSpeedAmpIn, int attackCooldownIn, float maxAttackDistanceIn)
    {
        this.entity = mob;
        this.moveSpeedAmp = moveSpeedAmpIn;
        this.attackCooldown = attackCooldownIn;
        this.maxAttackDistance = maxAttackDistanceIn * maxAttackDistanceIn;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse()
    {
        return this.entity.getTarget() != null && this.isBowInMainhand();
    }

    protected boolean isBowInMainhand()
    {
        return this.entity.getMainHandItem().getItem() instanceof ProjectileWeaponItem || this.entity.getOffhandItem().getItem() instanceof ProjectileWeaponItem;
    }

    @Override
    public boolean canContinueToUse()
    {
        return (this.canUse() || !this.entity.getNavigation().isDone()) && this.isBowInMainhand();
    }

    @Override
    public void start()
    {
        super.stop();
        this.entity.setAggressive(true);
    }

    @Override
    public void stop()
    {
        super.stop();
        this.entity.setAggressive(false);
        this.seeTime = 0;
        this.attackTime = -1;
        this.entity.stopUsingItem();
        this.entity.getNavigation().stop();
        entity.setShiftKeyDown(false);
    }

    @Override
    public void tick()
    {
        LivingEntity livingentity = this.entity.getTarget();
        if (livingentity != null)
        {
            if (livingentity instanceof Mob)
            {
                entity.setShiftKeyDown(((Mob) livingentity).getTarget() == null || !(((Mob) livingentity).getTarget() instanceof HunterEntity));
            }

            double d0 = this.entity.distanceToSqr(livingentity.getX(), livingentity.getY(), livingentity.getZ());
            boolean flag = this.entity.getSensing().hasLineOfSight(livingentity);
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
                this.entity.getNavigation().stop();
                ++this.strafingTime;
            } else
            {
                this.entity.getNavigation().moveTo(livingentity, entity.isShiftKeyDown() ? this.moveSpeedAmp * 0.3D : this.moveSpeedAmp);
                this.strafingTime = -1;
            }

            if (this.strafingTime >= 20)
            {
                if ((double) this.entity.getRandom().nextFloat() < 0.3D)
                {
                    this.strafingClockwise = !this.strafingClockwise;
                }

                if ((double) this.entity.getRandom().nextFloat() < 0.3D)
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

                this.entity.getMoveControl().strafe(this.strafingBackwards ? -(float) (entity.isShiftKeyDown() ? this.moveSpeedAmp * 0.3D : this.moveSpeedAmp) : (float) (entity.isShiftKeyDown() ? this.moveSpeedAmp * 0.2D : this.moveSpeedAmp), this.strafingClockwise ? (float) (entity.isShiftKeyDown() ? this.moveSpeedAmp * 0.3D : this.moveSpeedAmp) : -(float) (entity.isShiftKeyDown() ? this.moveSpeedAmp * 0.3D : this.moveSpeedAmp));
                this.entity.lookAt(livingentity, 30.0F, 30.0F);
            } else
            {
                this.entity.getLookControl().setLookAt(livingentity, 30.0F, 30.0F);
            }

            if (this.entity.isUsingItem())
            {
                if (!flag && this.seeTime < -60)
                {
                    this.entity.stopUsingItem();
                } else if (flag)
                {
                    int i = this.entity.getTicksUsingItem();
                    if (i >= 20)
                    {
                        this.entity.stopUsingItem();
                        this.entity.performRangedAttack(livingentity, BowItem.getPowerForTime(i));
                        this.attackTime = this.attackCooldown;
                    }
                }
            } else if (--this.attackTime <= 0 && this.seeTime >= -60)
            {
                this.entity.startUsingItem(ProjectileUtil.getWeaponHoldingHand(this.entity, Predicate.isEqual(Items.BOW)));
            }

        }
    }
}