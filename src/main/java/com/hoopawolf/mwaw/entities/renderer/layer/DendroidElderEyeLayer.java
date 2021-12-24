package com.hoopawolf.mwaw.entities.renderer.layer;

import com.hoopawolf.mwaw.entities.DendroidElderEntity;
import com.hoopawolf.mwaw.entities.model.DendroidElderModel;
import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DendroidElderEyeLayer extends EyesLayer<DendroidElderEntity, DendroidElderModel<DendroidElderEntity>>
{
    private static final RenderType RENDER_TYPE = RenderType.eyes(new ResourceLocation(Reference.MOD_ID, "textures/entity/dendroid_elder_eye.png"));

    public DendroidElderEyeLayer(RenderLayerParent<DendroidElderEntity, DendroidElderModel<DendroidElderEntity>> rendererIn)
    {
        super(rendererIn);
    }

    @Override
    public RenderType renderType()
    {
        return RENDER_TYPE;
    }
}