package com.hoopawolf.mwaw.proxy;

import com.hoopawolf.mwaw.util.EntityRegistryHandler;
import com.hoopawolf.mwaw.util.ItemBlockRegistryHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientProxy
{
    @SubscribeEvent
    public static void onClientSetUp(final FMLClientSetupEvent event)
    {
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ItemBlockRegistryHandler::registerItemModelProperties);
    }

    @SubscribeEvent
    public static void EntityRegisterLayer(EntityRenderersEvent.RegisterLayerDefinitions event)
    {
        EntityRegistryHandler.registerEntityLayer(event);
    }

    @SubscribeEvent
    public static void EntityRegisterRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        EntityRegistryHandler.registerEntityRenderer(event);
    }
}