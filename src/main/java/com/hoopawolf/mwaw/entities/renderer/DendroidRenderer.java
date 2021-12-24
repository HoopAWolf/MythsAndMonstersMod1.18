package com.hoopawolf.mwaw.entities.renderer;

import com.hoopawolf.mwaw.entities.DendroidEntity;
import com.hoopawolf.mwaw.entities.model.DendroidModel;
import com.hoopawolf.mwaw.entities.renderer.layer.DendroidEyeLayer;
import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DendroidRenderer extends MobRenderer<DendroidEntity, DendroidModel<DendroidEntity>>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/dendroid.png");

    public DendroidRenderer(EntityRendererProvider.Context _manager)
    {
        super(_manager, new DendroidModel<>(_manager.bakeLayer(DendroidModel.LAYER_LOCATION)), 1.0f);
        this.addLayer(new DendroidEyeLayer(this));
    }


    @Override
    public ResourceLocation getTextureLocation(DendroidEntity _entity)
    {
        return TEXTURE;
    }
}
