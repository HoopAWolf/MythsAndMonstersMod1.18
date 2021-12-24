package com.hoopawolf.mwaw.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NatureAuraSuckingParticle extends SuckingParticle
{
    protected NatureAuraSuckingParticle(ClientLevel worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, double spreadIn)
    {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, spreadIn);
    }

    protected NatureAuraSuckingParticle(ClientLevel worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn)
    {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        float f = this.random.nextFloat() * 0.6F + 0.4F;
        this.rCol = 0.4F;
        this.gCol = 0.9F * f;
        this.bCol = 0.4F;
    }

    @OnlyIn(Dist.CLIENT)
    public static class NatureAuraSucking implements ParticleProvider<SimpleParticleType>
    {
        private final SpriteSet spriteSet;

        public NatureAuraSucking(SpriteSet p_i50441_1_)
        {
            this.spriteSet = p_i50441_1_;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, double spread)
        {
            NatureAuraSuckingParticle particle = new NatureAuraSuckingParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spread);
            particle.pickSprite(this.spriteSet);
            return particle;
        }

        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            NatureAuraSuckingParticle particle = new NatureAuraSuckingParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.pickSprite(this.spriteSet);
            return particle;
        }
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
            NatureAuraSuckingParticle particle = new NatureAuraSuckingParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spread);
            particle.pickSprite(this.spriteSet);
            return particle;
        }

        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            NatureAuraSuckingParticle particle = new NatureAuraSuckingParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            particle.pickSprite(this.spriteSet);
            return particle;
        }
    }
}
