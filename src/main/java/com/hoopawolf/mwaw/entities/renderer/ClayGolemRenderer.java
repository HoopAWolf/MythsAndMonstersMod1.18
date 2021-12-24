package com.hoopawolf.mwaw.entities.renderer;

import com.hoopawolf.mwaw.entities.ClayGolemEntity;
import com.hoopawolf.mwaw.entities.model.ClayGolemModel;
import com.hoopawolf.mwaw.ref.Reference;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClayGolemRenderer extends MobRenderer<ClayGolemEntity, ClayGolemModel<ClayGolemEntity>>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/claygolem.png");
    private static final ResourceLocation HARDEN_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/claygolemharden.png");

    public ClayGolemRenderer(EntityRendererProvider.Context _manager)
    {
        super(_manager, new ClayGolemModel<>(_manager.bakeLayer(ClayGolemModel.LAYER_LOCATION)), 1.0f);
    }

    @Override
    public void render(ClayGolemEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn)
    {
        if (entityIn.isMinion())
        {
            this.shadowRadius = 0.5F;
            matrixStackIn.pushPose();
            matrixStackIn.scale(0.3F, 0.3F, 0.3F);
            matrixStackIn.translate(0.0D, 0.28D, 0.0D);
            super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
            matrixStackIn.popPose();
        } else
        {
            this.shadowRadius = 1.0F;
            super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(ClayGolemEntity _entity)
    {
        return _entity.isHardenForm() ? HARDEN_TEXTURE : TEXTURE;
    }
}