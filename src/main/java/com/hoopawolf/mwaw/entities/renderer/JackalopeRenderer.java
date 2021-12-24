package com.hoopawolf.mwaw.entities.renderer;

import com.hoopawolf.mwaw.entities.JackalopeEntity;
import com.hoopawolf.mwaw.entities.model.JackalopeModel;
import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class JackalopeRenderer extends MobRenderer<JackalopeEntity, JackalopeModel<JackalopeEntity>>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/jackalope.png");

    public JackalopeRenderer(EntityRendererProvider.Context renderManagerIn)
    {
        super(renderManagerIn, new JackalopeModel<>(renderManagerIn.bakeLayer(JackalopeModel.LAYER_LOCATION)), 0.7F);
    }

//    @Override
//    public void render(JackalopeEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn)
//    {
//        if(entityIn.isEscaping())
//        {
//            matrixStackIn.pushPose();
//            matrixStackIn.translate(0.0D, entityIn.getEscapingTimer() * 2.0D, 0.0D);
//            super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
//            matrixStackIn.popPose();
//        }
//        else
//        {
//            super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
//        }
//    }

    @Override
    public ResourceLocation getTextureLocation(JackalopeEntity entity)
    {
        return TEXTURE;
    }
}