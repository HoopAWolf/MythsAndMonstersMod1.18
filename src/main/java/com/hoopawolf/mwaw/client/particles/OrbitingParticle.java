package com.hoopawolf.mwaw.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;

public class OrbitingParticle extends TextureSheetParticle
{
    private final double coordX;
    private final double coordY;
    private final double coordZ;
    private final double
            orbitSpeed;
    private double spread;
    private double orbitAngle;

    protected OrbitingParticle(ClientLevel worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, double spreadIn)
    {
        this(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        spread = 1.0D;
    }

    protected OrbitingParticle(ClientLevel worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn)
    {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn);
        spread = 0.75D; //TODO will be here until i find a better way

        this.xd = xSpeedIn;
        this.yd = ySpeedIn;
        this.zd = zSpeedIn;
        this.coordX = xCoordIn;
        this.coordY = yCoordIn;
        this.coordZ = zCoordIn;
        this.xo = coordX + Math.cos(orbitAngle) * spread;
        this.yo = yCoordIn + ySpeedIn;
        this.zo = coordZ + Math.sin(orbitAngle) * spread;
        this.quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.2F);
        this.hasPhysics = false;
        this.lifetime = (int) (Math.random() * 10.0D) + 40;
        this.gravity = 0.0F;
        orbitAngle = 10D * 10D * Math.PI;
        orbitSpeed = (level.random.nextDouble() * 0.3217D + 0.1954D) * (random.nextBoolean() ? 1 : -1);

        this.x = coordX + Math.cos(orbitAngle) * spread;
        this.y = this.yo;
        this.z = coordZ + Math.sin(orbitAngle) * spread;
    }

    @Override
    public ParticleRenderType getRenderType()
    {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public void move(double x, double y, double z)
    {
        this.setBoundingBox(this.getBoundingBox().move(x, y, z));
        this.setLocationFromBoundingbox();
    }

    @Override
    public int getLightColor(float partialTick)
    {
        float f = ((float) this.age + partialTick) / (float) this.lifetime;
        f = Mth.clamp(f, 0.0F, 1.0F);
        int i = super.getLightColor(partialTick);
        int j = i & 255;
        int k = i >> 16 & 255;
        j = j + (int) (f * 15.0F * 16.0F);
        if (j > 240)
        {
            j = 240;
        }

        return j | k << 16;
    }

    @Override
    public void tick()
    {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime)
        {
            this.remove();
        } else
        {
            x = coordX + Math.cos(orbitAngle) * spread;
            y += yd;
            z = coordZ + Math.sin(orbitAngle) * spread;

            orbitAngle += orbitSpeed;
        }
    }
}
