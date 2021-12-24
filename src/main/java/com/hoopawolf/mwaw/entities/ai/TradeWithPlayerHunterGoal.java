package com.hoopawolf.mwaw.entities.ai;

import com.hoopawolf.mwaw.entities.HunterEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;

import java.util.EnumSet;

public class TradeWithPlayerHunterGoal extends Goal
{
    private final HunterEntity hunter;

    public TradeWithPlayerHunterGoal(HunterEntity _hunter)
    {
        this.hunter = _hunter;
        this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse()
    {
        if (!this.hunter.isAlive())
        {
            return false;
        } else if (this.hunter.isInWater())
        {
            return false;
        } else if (!this.hunter.isOnGround())
        {
            return false;
        } else if (this.hunter.hurtMarked)
        {
            return false;
        } else
        {
            Player playerentity = this.hunter.getTradingPlayer();
            if (playerentity == null)
            {
                return false;
            } else if (this.hunter.distanceToSqr(playerentity) > 16.0D)
            {
                return false;
            } else
            {
                return playerentity.containerMenu != null;
            }
        }
    }

    @Override
    public void start()
    {
        this.hunter.getNavigation().stop();
    }

    @Override
    public void stop()
    {
        this.hunter.setTradingPlayer(null);
    }
}