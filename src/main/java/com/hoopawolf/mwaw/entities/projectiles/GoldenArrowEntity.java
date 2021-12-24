package com.hoopawolf.mwaw.entities.projectiles;

import com.hoopawolf.mwaw.util.EntityRegistryHandler;
import com.hoopawolf.mwaw.util.ItemBlockRegistryHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

public class GoldenArrowEntity extends AbstractArrow
{
    public GoldenArrowEntity(EntityType<? extends AbstractArrow> p_i50172_1_, Level p_i50172_2_)
    {
        super(p_i50172_1_, p_i50172_2_);
    }

    public GoldenArrowEntity(Level worldIn, double x, double y, double z)
    {
        super(EntityRegistryHandler.GOLDEN_ARROW_ENTITY.get(), x, y, z, worldIn);
    }

    public GoldenArrowEntity(Level worldIn, LivingEntity shooter)
    {
        super(EntityRegistryHandler.GOLDEN_ARROW_ENTITY.get(), shooter, worldIn);
    }

    @Override
    public void tick()
    {
        super.tick();
        if (this.level.isClientSide)
        {
            if (this.inGround && this.inGroundTime != 0 && this.inGroundTime >= 600)
            {
                this.level.broadcastEntityEvent(this, (byte) 0);
            }
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compound)
    {
        super.addAdditionalSaveData(compound);

    }

    @Override
    public void readAdditionalSaveData(CompoundTag compound)
    {
        super.readAdditionalSaveData(compound);

    }

    @Override
    public float getBrightness()
    {
        return 15;
    }

    @Override //TODO ADD SOME SPECIAL EFFECTS MAYBE
    protected void doPostHurtEffects(LivingEntity living)
    {
        super.doPostHurtEffects(living);
    }

    @Override
    protected ItemStack getPickupItem()
    {
        return new ItemStack(ItemBlockRegistryHandler.GOLDEN_ARROW.get());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void handleEntityEvent(byte id)
    {
        super.handleEntityEvent(id);
    }

    @Override
    public Packet<?> getAddEntityPacket()
    {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
