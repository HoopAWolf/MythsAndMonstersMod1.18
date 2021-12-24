package com.hoopawolf.mwaw.client.particles;

import com.hoopawolf.mwaw.entities.helper.MathFuncHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;

public class SuckingParticle extends TextureSheetParticle
{
    private double spread;

    protected SuckingParticle(ClientLevel worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, double spreadIn)
    {
        this(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
        spread = 1.0D;
    }

    protected SuckingParticle(ClientLevel worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn)
    {
        super(worldIn, xCoordIn, yCoordIn, zCoordIn);
        spread = 1.75D; //TODO will be here until i find a better way

        double coordX = (worldIn.random.nextInt(100) < 50) ? xCoordIn - spread : xCoordIn + spread;
        double coordY = (worldIn.random.nextInt(100) < 50) ? yCoordIn - spread : yCoordIn + spread;
        double coordZ = (worldIn.random.nextInt(100) < 50) ? zCoordIn - spread : zCoordIn + spread;

        this.xd = MathFuncHelper.sign(xCoordIn - coordX) * xSpeedIn;
        this.yd = MathFuncHelper.sign(yCoordIn - coordY) * ySpeedIn;
        this.zd = MathFuncHelper.sign(zCoordIn - coordZ) * zSpeedIn;
        this.xo = coordX;
        this.yo = coordY;
        this.zo = coordZ;
        this.x = coordX;
        this.y = coordY;
        this.z = coordZ;
        this.quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.2F);
        this.setAlpha(0.5F);
        this.hasPhysics = false;
        this.lifetime = (int) (Math.random() * 10.0D) + 10;
        this.gravity = 0.0F;
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
            x += xd;
            y += yd;
            z += zd;
        }
    }
}

