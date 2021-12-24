package com.hoopawolf.mwaw.network;

import com.hoopawolf.mwaw.network.packets.server.MessageToServer;
import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MessageHandlerOnServer
{
    /**
     * Called when a message is received of the appropriate type.
     * CALLED BY THE NETWORK THREAD, NOT THE SERVER THREAD
     *
     * @param message The message
     */
    public static void onMessageReceived(final MessageToServer message, Supplier<NetworkEvent.Context> ctxSupplier)
    {
        NetworkEvent.Context ctx = ctxSupplier.get();
        LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
        ctx.setPacketHandled(true);

        if (sideReceived != LogicalSide.SERVER)
        {
            Reference.LOGGER.warn("MessageToServer received on wrong side:" + ctx.getDirection().getReceptionSide());
            return;
        }
        if (!message.isMessageValid())
        {
            Reference.LOGGER.warn("MessageToServer was invalid" + message.toString());
            return;
        }

        final ServerPlayer sendingPlayer = ctx.getSender();
        if (sendingPlayer == null)
        {
            Reference.LOGGER.warn("EntityPlayerMP was null when MessageToServer was received");
        }

        ctx.enqueueWork(() -> processMessage(message, sendingPlayer));
    }


    static void processMessage(MessageToServer message, ServerPlayer sendingPlayer)
    {
        switch (message.getMessageType())
        {
            case 1:
            {

            }
            break;

            default:
                break;
        }
    }

    public static boolean isThisProtocolAcceptedByServer(String protocolVersion)
    {
        return Reference.MESSAGE_PROTOCOL_VERSION.equals(protocolVersion);
    }
}
