package com.hoopawolf.mwaw.network;

import com.hoopawolf.mwaw.network.packets.client.SpawnOrbitingParticleMessage;
import com.hoopawolf.mwaw.network.packets.client.SpawnParticleMessage;
import com.hoopawolf.mwaw.network.packets.client.SpawnSuckingParticleMessage;
import com.hoopawolf.mwaw.network.packets.server.FireParticleSpawnMessage;
import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class MWAWPacketHandler
{
    public static final MWAWPacketHandler packetHandler = new MWAWPacketHandler();
    public static final SimpleChannel channel = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(Reference.MOD_ID, "mwaw_main_channel"))
            .clientAcceptedVersions(MessageHandlerOnClient::isThisProtocolAcceptedByClient)
            .serverAcceptedVersions(MessageHandlerOnServer::isThisProtocolAcceptedByServer)
            .networkProtocolVersion(() -> Reference.MESSAGE_PROTOCOL_VERSION)
            .simpleChannel();

    public static void init()
    {
        int id = 0;
        channel.messageBuilder(FireParticleSpawnMessage.class, id++).encoder(FireParticleSpawnMessage::encode).decoder(FireParticleSpawnMessage::decode).consumer(MessageHandlerOnServer::onMessageReceived).add();
        channel.messageBuilder(SpawnParticleMessage.class, id++).encoder(SpawnParticleMessage::encode).decoder(SpawnParticleMessage::decode).consumer(MessageHandlerOnClient::onMessageReceived).add();
        channel.messageBuilder(SpawnOrbitingParticleMessage.class, id++).encoder(SpawnOrbitingParticleMessage::encode).decoder(SpawnOrbitingParticleMessage::decode).consumer(MessageHandlerOnClient::onMessageReceived).add();
        channel.messageBuilder(SpawnSuckingParticleMessage.class, id++).encoder(SpawnSuckingParticleMessage::encode).decoder(SpawnSuckingParticleMessage::decode).consumer(MessageHandlerOnClient::onMessageReceived).add();
    }

    public void send(PacketDistributor.PacketTarget target, Object message)
    {
        channel.send(target, message);
    }

    public void sendToPlayer(ServerPlayer player, Object message)
    {
        this.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public void sendToDimension(ResourceKey<Level> dimension, Object message)
    {
        this.send(PacketDistributor.DIMENSION.with(() -> dimension), message);
    }

    public void sendToNearbyPlayers(double x, double y, double z, double radius, ResourceKey<Level> dimension, Object message)
    {
        this.sendToNearbyPlayers(new PacketDistributor.TargetPoint(x, y, z, radius, dimension), message);
    }

    public void sendToNearbyPlayers(PacketDistributor.TargetPoint point, Object message)
    {
        this.send(PacketDistributor.NEAR.with(() -> point), message);
    }

    public void sendToAllPlayers(Object message)
    {
        this.send(PacketDistributor.ALL.noArg(), message);
    }

    public void sendToChunk(LevelChunk chunk, Object message)
    {
        this.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), message);
    }
}
