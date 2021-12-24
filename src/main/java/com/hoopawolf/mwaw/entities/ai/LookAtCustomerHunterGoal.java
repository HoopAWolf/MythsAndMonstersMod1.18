package com.hoopawolf.mwaw.entities.ai;

import com.hoopawolf.mwaw.entities.HunterEntity;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;

public class LookAtCustomerHunterGoal extends LookAtPlayerGoal
{
    private final HunterEntity hunter;

    public LookAtCustomerHunterGoal(HunterEntity hunterEntity)
    {
        super(hunterEntity, Player.class, 8.0F);
        this.hunter = hunterEntity;
    }

    @Override
    public boolean canUse()
    {
        if (this.hunter.isTrading() && this.hunter.getTarget() == null)
        {
            this.lookAt = this.hunter.getTradingPlayer();
            return true;
        } else
        {
            return false;
        }
    }
}