package com.hoopawolf.mwaw.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class MWAWRenderType extends RenderStateShard
{
    public MWAWRenderType(String nameIn, Runnable setupTaskIn, Runnable clearTaskIn)
    {
        super(nameIn, setupTaskIn, clearTaskIn);
    }

    public static RenderType getTextureSwirl(ResourceLocation locationIn, float uIn, float vIn)
    {
        return RenderType.create("texture_offsetting", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(locationIn, false, false)).setTexturingState(new RenderStateShard.OffsetTexturingStateShard(uIn, vIn)).setTransparencyState(RenderStateShard.NO_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(NO_LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false));
    }
}
