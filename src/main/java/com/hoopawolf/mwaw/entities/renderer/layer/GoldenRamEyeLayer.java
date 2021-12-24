package com.hoopawolf.mwaw.entities.renderer.layer;

import com.hoopawolf.mwaw.entities.GoldenRamEntity;
import com.hoopawolf.mwaw.entities.model.GoldenRamModel;
import com.hoopawolf.mwaw.ref.Reference;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GoldenRamEyeLayer extends EyesLayer<GoldenRamEntity, GoldenRamModel<GoldenRamEntity>>
{
    private static final RenderType RENDER_TYPE = RenderType.eyes(new ResourceLocation(Reference.MOD_ID, "textures/entity/goldenrameye.png"));
    private static final RenderType RENDER_ANGRY_TYPE = RenderType.eyes(new ResourceLocation(Reference.MOD_ID, "textures/entity/goldenrameyeangry.png"));
    private static RenderType RENDER_TYPE_FINAL;

    public GoldenRamEyeLayer(RenderLayerParent<GoldenRamEntity, GoldenRamModel<GoldenRamEntity>> rendererIn)
    {
        super(rendererIn);
        RENDER_TYPE_FINAL = RENDER_TYPE;
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, GoldenRamEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {
        super.render(matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch);

        if (entitylivingbaseIn.isAngry())
            RENDER_TYPE_FINAL = RENDER_ANGRY_TYPE;
        else
            RENDER_TYPE_FINAL = RENDER_TYPE;
    }

    @Override
    public RenderType renderType()
    {
        return RENDER_TYPE_FINAL;
    }
}