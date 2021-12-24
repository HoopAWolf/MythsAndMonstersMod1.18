package com.hoopawolf.mwaw.proxy;

import com.hoopawolf.mwaw.items.MWAWSpawnEggItem;
import com.hoopawolf.mwaw.network.MWAWPacketHandler;
import com.hoopawolf.mwaw.util.EntityRegistryHandler;
import com.hoopawolf.mwaw.util.ItemBlockRegistryHandler;
//import com.hoopawolf.mwaw.util.StructureRegistryHandler;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonProxy
{
    @SubscribeEvent
    public static void onCommonSetupEvent(FMLCommonSetupEvent event)
    {
        MWAWPacketHandler.init();
        //DeferredWorkQueue.runLater(ItemBlockRegistryHandler::generateBlockWorldSpawn);
    }

    @SubscribeEvent
    public static void onLoadComplete(FMLLoadCompleteEvent event)
    {
      //  DeferredWorkQueue.runLater(StructureRegistryHandler::generateStructureWorldSpawn);
    }

    @SubscribeEvent
    public static void EntityAttributeCreation(EntityAttributeCreationEvent event)
    {
        EntityRegistryHandler.registerEntityAttributes(event);
    }
}