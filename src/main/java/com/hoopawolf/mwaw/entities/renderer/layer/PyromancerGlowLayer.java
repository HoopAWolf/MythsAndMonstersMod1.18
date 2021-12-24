package com.hoopawolf.mwaw.entities.renderer.layer;

import com.hoopawolf.mwaw.entities.PyromancerEntity;
import com.hoopawolf.mwaw.entities.model.PyromancerModel;
import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

public class PyromancerGlowLayer extends EyesLayer<PyromancerEntity, PyromancerModel<PyromancerEntity>>
{
    private static final RenderType RENDER_TYPE = RenderType.eyes(new ResourceLocation(Reference.MOD_ID, "textures/entity/pyromancer_glow.png"));

    public PyromancerGlowLayer(RenderLayerParent<PyromancerEntity, PyromancerModel<PyromancerEntity>> rendererIn)
    {
        super(rendererIn);
    }

    @Override
    public RenderType renderType()
    {
        return RENDER_TYPE;
    }
}