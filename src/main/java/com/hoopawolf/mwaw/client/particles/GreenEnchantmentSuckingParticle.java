package com.hoopawolf.mwaw.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GreenEnchantmentSuckingParticle extends SuckingParticle
{
    protected GreenEnchantmentSuckingParticle(ClientLevel worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, double spreadIn)
    {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, spreadIn);
    }

    protected GreenEnchantmentSuckingParticle(ClientLevel worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn)
    {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        float f = this.random.nextFloat() * 0.6F + 0.4F;
        this.rCol = 0.4F;
        this.gCol = 0.9F * f;
        this.bCol = 0.4F;
    }

    @OnlyIn(Dist.CLIENT)
    public static class YellowEnchantmentSucking implements ParticleProvider<SimpleParticleType>
    {
        private final SpriteSet spriteSet;

        public YellowEnchantmentSucking(SpriteSet p_i50441_1_)
        {
            this.spriteSet = p_i50441_1_;
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, double spread)
        {
            GreenEnchantmentSuckingParticle yellowenchantmentparticle = new GreenEnchantmentSuckingParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spread);
            yellowenchantmentparticle.pickSprite(this.spriteSet);
            return yellowenchantmentparticle;
        }

        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            GreenEnchantmentSuckingParticle yellowenchantmentparticle = new GreenEnchantmentSuckingParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            yellowenchantmentparticle.pickSprite(this.spriteSet);
            return yellowenchantmentparticle;
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
            GreenEnchantmentSuckingParticle yellowenchantmentparticle = new GreenEnchantmentSuckingParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, spread);
            yellowenchantmentparticle.pickSprite(this.spriteSet);
            return yellowenchantmentparticle;
        }

        @Override
        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed)
        {
            GreenEnchantmentSuckingParticle yellowenchantmentparticle = new GreenEnchantmentSuckingParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
            yellowenchantmentparticle.pickSprite(this.spriteSet);
            return yellowenchantmentparticle;
        }
    }
}
