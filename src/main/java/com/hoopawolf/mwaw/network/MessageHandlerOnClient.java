package com.hoopawolf.mwaw.network;

import com.hoopawolf.mwaw.network.packets.client.MessageToClient;
import com.hoopawolf.mwaw.network.packets.client.SpawnOrbitingParticleMessage;
import com.hoopawolf.mwaw.network.packets.client.SpawnParticleMessage;
import com.hoopawolf.mwaw.network.packets.client.SpawnSuckingParticleMessage;
import com.hoopawolf.mwaw.ref.Reference;
import com.hoopawolf.mwaw.util.ParticleRegistryHandler;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;


public class MessageHandlerOnClient
{
    static final SimpleParticleType[] types = //0 - FIREWORKS, 1 - HEART, 2 - BLOCK TYPE, 3 - VILLAGER ANGRY, 4 - CLOUD, 5 - HAPPY VILLAGER, 6 - YELLOW ENCHANTMENT, 7 - NATURE AURA, 8 - MYCELIM, 9 - FLAME, 10 - FIRE PARTICLE
            {
                    ParticleTypes.FIREWORK,
                    ParticleTypes.HEART,
                    ParticleTypes.ITEM_SLIME, // PLACE HOLDER FOR BLOCK
                    ParticleTypes.ANGRY_VILLAGER,
                    ParticleTypes.CLOUD,
                    ParticleTypes.HAPPY_VILLAGER,
                    ParticleRegistryHandler.YELLOW_ENCHANTMENT_PARTICLE.get(),
                    ParticleRegistryHandler.NATURE_AURA_PARTICLE.get(),
                    ParticleTypes.MYCELIUM,
                    ParticleTypes.FLAME,
                    ParticleRegistryHandler.FIRE_PARTICLE.get()
            };

    static final SimpleParticleType[] orbiting_types = //0 - YELLOW ENCHANTMENT ORBITING
            {
                    ParticleRegistryHandler.YELLOW_ORBITING_ENCHANTMENT_PARTICLE.get()
            };

    static final SimpleParticleType[] sucking_types = //0 - GREEN SUCKING, 1 - NATURE AURA SUCKING, 2 - FIRE SUCKING
            {
                    ParticleRegistryHandler.GREEN_SUCKING_ENCHANTMENT_PARTICLE.get(),
                    ParticleRegistryHandler.NATURE_AURA_SUCKING_PARTICLE.get(),
                    ParticleRegistryHandler.FIRE_SUCKING_PARTICLE.get()
            };

    public static void onMessageReceived(final MessageToClient message, Supplier<NetworkEvent.Context> ctxSupplier)
    {
        NetworkEvent.Context ctx = ctxSupplier.get();
        LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
        ctx.setPacketHandled(true);

        if (sideReceived != LogicalSide.CLIENT)
        {
            Reference.LOGGER.warn("MessageToClient received on wrong side:" + ctx.getDirection().getReceptionSide());
            return;
        }
        if (!message.isMessageValid())
        {
            Reference.LOGGER.warn("MessageToClient was invalid" + message.toString());
            return;
        }

        Optional<Level> clientWorld = LogicalSidedProvider.CLIENTWORLD.get(sideReceived);
        if (!clientWorld.isPresent())
        {
            Reference.LOGGER.warn("MessageToClient context could not provide a ClientLevel.");
            return;
        }

        ctx.enqueueWork(() -> processMessage((ClientLevel) clientWorld.get(), message));
    }

    private static void processMessage(ClientLevel worldClient, MessageToClient message)
    {
        switch (message.getMessageType())
        {
            case 1:
            {
                SpawnParticleMessage _message = (((SpawnParticleMessage) message));
                for (int i = 0; i < _message.getIteration(); ++i)
                {
                    Vec3 targetCoordinates = _message.getTargetCoordinates();
                    Vec3 targetSpeed = _message.getTargetSpeed();
                    double spread = _message.getParticleSpread();
                    double spawnXpos = targetCoordinates.x;
                    double spawnYpos = targetCoordinates.y;
                    double spawnZpos = targetCoordinates.z;
                    double speedX = targetSpeed.x;
                    double speedY = targetSpeed.y;
                    double speedZ = targetSpeed.z;

                    worldClient.addParticle(_message.getPartcleType() == 2 ? new BlockParticleOption(ParticleTypes.BLOCK, worldClient.getBlockState(new BlockPos(spawnXpos, spawnYpos - 1.0F, spawnZpos))) : types[_message.getPartcleType()],
                            Mth.lerp(worldClient.random.nextDouble(), spawnXpos + spread,
                                    spawnXpos - spread), spawnYpos,
                            Mth.lerp(worldClient.random.nextDouble(), spawnZpos + spread, spawnZpos - spread),
                            speedX, speedY, speedZ);
                }
            }
            break;
            case 2:
            {
                SpawnOrbitingParticleMessage _message = (((SpawnOrbitingParticleMessage) message));
                for (int i = 0; i < _message.getIteration(); ++i)
                {
                    Vec3 targetCoordinates = _message.getTargetCoordinates();
                    Vec3 targetSpeed = _message.getTargetSpeed();
                    double spawnXpos = targetCoordinates.x;
                    double spawnYpos = targetCoordinates.y;
                    double spawnZpos = targetCoordinates.z;
                    double speedX = targetSpeed.x;
                    double speedY = targetSpeed.y;
                    double speedZ = targetSpeed.z;

                    worldClient.addParticle(orbiting_types[_message.getPartcleType()],
                            spawnXpos,
                            spawnYpos,
                            spawnZpos,
                            speedX, speedY, speedZ);
                }
            }
            break;
            case 3:
            {
                SpawnSuckingParticleMessage _message = (((SpawnSuckingParticleMessage) message));
                for (int i = 0; i < _message.getIteration(); ++i)
                {
                    Vec3 targetCoordinates = _message.getTargetCoordinates();
                    Vec3 targetSpeed = _message.getTargetSpeed();
                    double spawnXpos = targetCoordinates.x;
                    double spawnYpos = targetCoordinates.y;
                    double spawnZpos = targetCoordinates.z;
                    double speedX = targetSpeed.x;
                    double speedY = targetSpeed.y;
                    double speedZ = targetSpeed.z;

                    worldClient.addParticle(sucking_types[_message.getPartcleType()],
                            spawnXpos,
                            spawnYpos,
                            spawnZpos,
                            speedX, speedY, speedZ);
                }
            }
            break;
            default:
                break;
        }

    }

    public static boolean isThisProtocolAcceptedByClient(String protocolVersion)
    {
        return Reference.MESSAGE_PROTOCOL_VERSION.equals(protocolVersion);
    }
}
