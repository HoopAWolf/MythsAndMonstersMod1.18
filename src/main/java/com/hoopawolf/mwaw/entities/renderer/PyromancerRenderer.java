package com.hoopawolf.mwaw.entities.renderer;

import com.hoopawolf.mwaw.entities.PyromancerEntity;
import com.hoopawolf.mwaw.entities.model.ClayGolemModel;
import com.hoopawolf.mwaw.entities.model.PyromancerModel;
import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class PyromancerRenderer extends MobRenderer<PyromancerEntity, PyromancerModel<PyromancerEntity>>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/pyromancer.png");

    public PyromancerRenderer(EntityRendererProvider.Context _manager)
    {
        super(_manager, new PyromancerModel<>(_manager.bakeLayer(PyromancerModel.LAYER_LOCATION)), 0.5f);
        //this.addLayer(new PyromancerGlowLayer(this));
    }

    @Override
    protected int getBlockLightLevel(PyromancerEntity entityIn, BlockPos partialTicks)
    {
        return (int) (15.0F * (entityIn.getHealth() / entityIn.getMaxHealth()));
    }

    @Override
    public ResourceLocation getTextureLocation(PyromancerEntity _entity)
    {
        return TEXTURE;
    }
}