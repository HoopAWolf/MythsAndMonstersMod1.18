package com.hoopawolf.mwaw.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FireSuckingParticle extends SuckingParticle
{
    protected FireSuckingParticle(ClientLevel worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, double spreadIn)
    {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, spreadIn);
    }

    protected FireSuckingParticle(ClientLevel worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn)
    {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        this.scale(3.0F);
        this.setSize(0.25F, 0.25F);
        this.lifetime = this.random.nextInt(10);
        alpha = 1.0F;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType>
    {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet p_i50442_1_)
        {
            this.spriteSet = p_i50442_1_;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, double spread)
        {
            FireSuckingParticle yellowenchantmentparticle = new FireSuckingParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spread);
            yellowenchantmentparticle.pickSprite(this.spriteSet);
            return yellowenchantmentparticle;
        }

        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            FireSuckingParticle yellowenchantmentparticle = new FireSuckingParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            yellowenchantmentparticle.pickSprite(this.spriteSet);
            return yellowenchantmentparticle;
        }
    }
}
