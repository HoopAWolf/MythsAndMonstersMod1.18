package com.hoopawolf.mwaw.entities.renderer.layer;

import com.hoopawolf.mwaw.client.render.MWAWRenderType;
import com.hoopawolf.mwaw.entities.GoldenRamEntity;
import com.hoopawolf.mwaw.entities.model.GoldenRamModel;
import com.hoopawolf.mwaw.entities.model.layer.GoldenWoolModel;
import com.hoopawolf.mwaw.ref.Reference;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GoldenWoolLayer extends RenderLayer<GoldenRamEntity, GoldenRamModel<GoldenRamEntity>>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/goldenramwool.png");
    private final GoldenWoolModel<GoldenRamEntity> sheepModel;

    public GoldenWoolLayer(RenderLayerParent<GoldenRamEntity, GoldenRamModel<GoldenRamEntity>> p_174533_, EntityModelSet p_174534_) {
        super(p_174533_);
        this.sheepModel = new GoldenWoolModel<>(p_174534_.bakeLayer(ModelLayers.SHEEP_FUR));
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, GoldenRamEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {
        if (!entitylivingbaseIn.getSheared() && !entitylivingbaseIn.isInvisible())
        {
            float f = (float) entitylivingbaseIn.tickCount + partialTicks;
            EntityModel<GoldenRamEntity> entitymodel = this.func_225635_b_();
            entitymodel.prepareMobModel(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks);
            this.getParentModel().copyPropertiesTo(entitymodel);
            VertexConsumer ivertexbuilder = bufferIn.getBuffer(MWAWRenderType.getTextureSwirl(this.func_225633_a_(), this.func_225634_a_(f), f * 0.005F));
            entitymodel.setupAnim(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            entitymodel.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

//            renderCopyCutoutModel(this.getParentModel(), this.sheepModel, TEXTURE, matrixStackIn, bufferIn, packedLightIn, entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, partialTicks, 1.0F, 1.0F, 1.0F);
        }
    }

    protected float func_225634_a_(float p_225634_1_)
    {
        return p_225634_1_ * 0.005F;
    }

    protected ResourceLocation func_225633_a_()
    {
        return TEXTURE;
    }

    protected EntityModel<GoldenRamEntity> func_225635_b_()
    {
        return this.sheepModel;
    }
}