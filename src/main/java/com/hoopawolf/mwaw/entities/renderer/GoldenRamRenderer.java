package com.hoopawolf.mwaw.entities.renderer;

import com.hoopawolf.mwaw.entities.GoldenRamEntity;
import com.hoopawolf.mwaw.entities.model.GoldenRamModel;
import com.hoopawolf.mwaw.entities.renderer.layer.GoldenRamEyeLayer;
import com.hoopawolf.mwaw.entities.renderer.layer.GoldenWoolLayer;
import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GoldenRamRenderer extends MobRenderer<GoldenRamEntity, GoldenRamModel<GoldenRamEntity>>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/goldenram.png");
    private static final ResourceLocation ANGRY_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/goldenramangry.png");

    public GoldenRamRenderer(EntityRendererProvider.Context renderManagerIn)
    {
        super(renderManagerIn, new GoldenRamModel<>(renderManagerIn.bakeLayer(GoldenRamModel.LAYER_LOCATION)), 0.7F);
        this.addLayer(new GoldenWoolLayer(this, renderManagerIn.getModelSet()));
        this.addLayer(new GoldenRamEyeLayer(this));
    }

    @Override
    protected int getBlockLightLevel(GoldenRamEntity entityIn, BlockPos partialTicks)
    {
        return entityIn.getSheared() ? super.getBlockLightLevel(entityIn, partialTicks) : 11;
    }

    @Override
    public ResourceLocation getTextureLocation(GoldenRamEntity _entity)
    {
        return _entity.isAngry() ? ANGRY_TEXTURE : TEXTURE;
    }
}