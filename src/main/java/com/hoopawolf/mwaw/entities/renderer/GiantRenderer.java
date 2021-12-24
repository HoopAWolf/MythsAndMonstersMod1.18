package com.hoopawolf.mwaw.entities.renderer;

import com.hoopawolf.mwaw.entities.GiantEntity;
import com.hoopawolf.mwaw.entities.model.GiantModel;
import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class GiantRenderer extends MobRenderer<GiantEntity, GiantModel<GiantEntity>>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/giant.png");

    public GiantRenderer(EntityRendererProvider.Context renderManagerIn)
    {
        super(renderManagerIn, new GiantModel<>(renderManagerIn.bakeLayer(GiantModel.LAYER_LOCATION)), 0.7F);
    }

    @Override
    public ResourceLocation getTextureLocation(GiantEntity entity)
    {
        return TEXTURE;
    }
}