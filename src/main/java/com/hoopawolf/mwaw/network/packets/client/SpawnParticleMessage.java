package com.hoopawolf.mwaw.network.packets.client;

import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

public class SpawnParticleMessage extends MessageToClient
{
    private Vec3 targetCoordinates, targetSpeed;
    private int iteration, particle_type;
    private double spread;

    public SpawnParticleMessage(Vec3 i_targetCoordinates, Vec3 i_targetSpeed, int _iteration, int _particle_type, double _spread)
    {
        messageIsValid = true;
        messageType = 1;
        targetCoordinates = i_targetCoordinates;
        targetSpeed = i_targetSpeed;
        iteration = _iteration;
        particle_type = _particle_type;
        spread = _spread;
    }

    // for use by the message handler only.
    public SpawnParticleMessage()
    {
        messageIsValid = false;
    }

    public static SpawnParticleMessage decode(FriendlyByteBuf buf)
    {
        int iterationAmount, particletype;
        double x;
        double y;
        double z;

        double speedx;
        double speedy;
        double speedz;

        double spreadDist;
        try
        {
            particletype = buf.readInt();
            iterationAmount = buf.readInt();
            x = buf.readDouble();
            y = buf.readDouble();
            z = buf.readDouble();
            speedx = buf.readDouble();
            speedy = buf.readDouble();
            speedz = buf.readDouble();
            spreadDist = buf.readDouble();

            // these methods may also be of use for your code:
            // for Itemstacks - ByteBufUtils.readItemStack()
            // for NBT tags ByteBufUtils.readTag();
            // for Strings: ByteBufUtils.readUTF8String();
            // NB that FriendlyByteBuf is a derived class of ByteBuf

        } catch (IllegalArgumentException | IndexOutOfBoundsException e)
        {
            Reference.LOGGER.warn("Exception while reading SpawnParticleMessageToClient: " + e);
            return new SpawnParticleMessage();
        }

        return new SpawnParticleMessage(new Vec3(x, y, z), new Vec3(speedx, speedy, speedz), iterationAmount, particletype, spreadDist);
    }

    public Vec3 getTargetCoordinates()
    {
        return targetCoordinates;
    }

    public Vec3 getTargetSpeed()
    {
        return targetSpeed;
    }

    public int getIteration()
    {
        return iteration;
    }

    public int getPartcleType()
    {
        return particle_type;
    }

    public double getParticleSpread()
    {
        return spread;
    }

    @Override
    public void encode(FriendlyByteBuf buf)
    {
        if (!messageIsValid) return;
        buf.writeInt(particle_type);
        buf.writeInt(iteration);
        buf.writeDouble(targetCoordinates.x);
        buf.writeDouble(targetCoordinates.y);
        buf.writeDouble(targetCoordinates.z);
        buf.writeDouble(targetSpeed.x);
        buf.writeDouble(targetSpeed.y);
        buf.writeDouble(targetSpeed.z);
        buf.writeDouble(spread);
    }

    @Override
    public String toString()
    {
        return "SpawnParticleMessageToClient[targetCoordinates=" + targetCoordinates + "]";
    }
}
