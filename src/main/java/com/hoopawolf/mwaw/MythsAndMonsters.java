package com.hoopawolf.mwaw;

import com.hoopawolf.mwaw.proxy.ClientProxy;
import com.hoopawolf.mwaw.proxy.CommonProxy;
import com.hoopawolf.mwaw.ref.Reference;
import com.hoopawolf.mwaw.util.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Reference.MOD_ID)
public class MythsAndMonsters
{
    public MythsAndMonsters()
    {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(ClientProxy::onClientSetUp);
        modEventBus.addListener(CommonProxy::onCommonSetupEvent);

        ItemBlockRegistryHandler.init(modEventBus);
        EntityRegistryHandler.ENTITIES.register(modEventBus);
        ParticleRegistryHandler.PARTICLES.register(modEventBus);
        PotionRegistryHandler.init(modEventBus);
        //StructureRegistryHandler.init(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }
}