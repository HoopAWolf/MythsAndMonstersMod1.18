package com.hoopawolf.mwaw.entities.renderer;

import com.hoopawolf.mwaw.entities.FireSpiritEntity;
import com.hoopawolf.mwaw.ref.Reference;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.ShulkerBulletModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class FireSpiritRenderer extends EntityRenderer<FireSpiritEntity>
{
    private static final ResourceLocation SHULKER_SPARK_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/firespirit.png");
    private static final RenderType field_229123_e_ = RenderType.entityTranslucent(SHULKER_SPARK_TEXTURE);
    private final ShulkerBulletModel<FireSpiritEntity> model;

    public FireSpiritRenderer(EntityRendererProvider.Context manager)
    {
        super(manager);
        this.model = new ShulkerBulletModel<>(manager.bakeLayer(ModelLayers.SHULKER_BULLET));
    }

    @Override
    protected int getBlockLightLevel(FireSpiritEntity entityIn, BlockPos partialTicks)
    {
        return 15;
    }

    @Override
    public void render(FireSpiritEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn)
    {
        matrixStackIn.pushPose();
        float f = Mth.rotLerp(entityIn.yRotO, entityIn.getYRot(), partialTicks);
        float f1 = Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot());
        float f2 = (float) entityIn.tickCount + partialTicks;
        matrixStackIn.translate(0.0D, 0.15F, 0.0D);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(Mth.sin(f2 * 0.1F) * 180.0F));
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(Mth.cos(f2 * 0.1F) * 180.0F));
        matrixStackIn.mulPose(Vector3f.ZP.rotationDegrees(Mth.sin(f2 * 0.15F) * 360.0F));
        matrixStackIn.scale(-0.5F, -0.5F, 0.5F);
        this.model.setupAnim(entityIn, 0.0F, 0.0F, 0.0F, f, f1);
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(this.model.renderType(SHULKER_SPARK_TEXTURE));
        this.model.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStackIn.scale(1.5F, 1.5F, 1.5F);
        VertexConsumer ivertexbuilder1 = bufferIn.getBuffer(field_229123_e_);
        this.model.renderToBuffer(matrixStackIn, ivertexbuilder1, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.15F);
        matrixStackIn.popPose();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getTextureLocation(FireSpiritEntity entity)
    {
        return SHULKER_SPARK_TEXTURE;
    }
}