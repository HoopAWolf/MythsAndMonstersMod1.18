package com.hoopawolf.mwaw.client.particles;

import com.hoopawolf.mwaw.entities.PyromancerEntity;
import com.hoopawolf.mwaw.entities.helper.EntityHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FireParticle extends TextureSheetParticle
{
    private boolean setSized;
    private float sizeMultiplyer;

    private FireParticle(ClientLevel p_i51046_1_, double p_i51046_2_, double p_i51046_4_, double p_i51046_6_, double p_i51046_8_, double p_i51046_10_, double p_i51046_12_, boolean p_i51046_14_)
    {
        super(p_i51046_1_, p_i51046_2_, p_i51046_4_, p_i51046_6_);
        this.scale(3.0F);
        this.setSize(0.25F, 0.25F);
        this.lifetime = this.random.nextInt(10);
        this.alpha = 1.0F;
        this.gravity = 0.01F;
        this.xd = p_i51046_8_;
        this.yd = p_i51046_10_ + (double) (this.random.nextFloat() / 500.0F);
        this.zd = p_i51046_12_;
        setSized = false;
        sizeMultiplyer = 1.0F;
    }

    @Override
    protected int getLightColor(float partialTick)
    {
        int i = super.getLightColor(partialTick);
        int j = 240;
        int k = i >> 16 & 255;
        return 240 | k << 16;
    }

    @Override
    public float getQuadSize(float scaleFactor)
    {
        if (!setSized)
        {
            for (LivingEntity entity : EntityHelper.getEntitiesNearbyWithPos(level, getBoundingBox(), new BlockPos(this.x, this.y, this.z), LivingEntity.class, 2, 5, 2, 5))
            {
                if (entity instanceof PyromancerEntity)
                {
                    sizeMultiplyer = entity.getHealth() / entity.getMaxHealth();
                    setSized = true;
                    break;
                }
            }
        }

        return this.quadSize * sizeMultiplyer;
    }

    @Override
    public void tick()
    {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ < this.lifetime && !(this.alpha <= 0.0F))
        {
            this.xd += this.random.nextFloat() / 5000.0F * (float) (this.random.nextBoolean() ? 1 : -1);
            this.zd += this.random.nextFloat() / 5000.0F * (float) (this.random.nextBoolean() ? 1 : -1);
            this.yd += this.gravity;
            this.move(this.xd, this.yd, this.zd);
            if (this.age >= this.lifetime - 60 && this.alpha > 0.01F)
            {
                this.alpha -= 0.015F;
            }

        } else
        {
            this.remove();
        }
    }

    @Override
    public ParticleRenderType getRenderType()
    {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class FireSmokeFactory implements ParticleProvider<SimpleParticleType>
    {
        private final SpriteSet spriteSet;

        public FireSmokeFactory(SpriteSet p_i51180_1_)
        {
            this.spriteSet = p_i51180_1_;
        }

        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            FireParticle campfireparticle = new FireParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, false);
            campfireparticle.pickSprite(this.spriteSet);
            return campfireparticle;
        }
    }
}
