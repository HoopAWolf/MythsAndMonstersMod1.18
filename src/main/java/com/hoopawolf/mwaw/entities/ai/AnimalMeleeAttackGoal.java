package com.hoopawolf.mwaw.entities.ai;

import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class AnimalMeleeAttackGoal extends MeleeAttackGoal
{
    private final PathfinderMob host;
    private final double damage;
    private final double knockBack;

    public AnimalMeleeAttackGoal(PathfinderMob creature, double speedIn, boolean useLongMemory, double damageIn, double knockBackIn)
    {
        super(creature, speedIn, useLongMemory);
        host = creature;
        damage = damageIn;
        knockBack = knockBackIn;
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr)
    {
        double d0 = this.getAttackReachSqr(enemy);
        if (distToEnemySqr <= d0 && this.getTicksUntilNextAttack() <= 0)
        {
            this.resetAttackCooldown();
            this.host.swing(InteractionHand.MAIN_HAND);

            float f = (float) damage;
            float f1 = (float) knockBack;

            boolean flag = enemy.hurt(DamageSource.mobAttack(host), f);

            if (flag)
            {
                if (f1 > 0.0F && enemy instanceof LivingEntity)
                {
                    enemy.knockback(f1 * 0.5F, Mth.sin(host.yBodyRot * ((float) Math.PI / 180F)), -Mth.cos(host.yBodyRot * ((float) Math.PI / 180F)));
                    host.setDeltaMovement(host.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
                }

                host.setLastHurtMob(enemy);
            }
        }
    }
}
