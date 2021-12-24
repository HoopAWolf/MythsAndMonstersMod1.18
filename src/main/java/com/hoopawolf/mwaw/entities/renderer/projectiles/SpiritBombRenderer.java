package com.hoopawolf.mwaw.entities.renderer.projectiles;

import com.hoopawolf.mwaw.client.render.MWAWRenderType;
import com.hoopawolf.mwaw.entities.model.projectiles.SpiritBombModel;
import com.hoopawolf.mwaw.entities.projectiles.SpiritBombEntity;
import com.hoopawolf.mwaw.ref.Reference;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SpiritBombRenderer extends EntityRenderer<SpiritBombEntity>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/spiritbomb.png");
    private static final float field_229047_f_ = (float) Math.sin((Math.PI / 4D));
    private final SpiritBombModel<SpiritBombEntity> model;

    public SpiritBombRenderer(EntityRendererProvider.Context renderManagerIn)
    {
        super(renderManagerIn);
        this.shadowRadius = 0.5F;
        this.model = new SpiritBombModel<>(renderManagerIn.bakeLayer(SpiritBombModel.LAYER_LOCATION));
    }

    public static float func_229051_a_(SpiritBombEntity p_229051_0_, float p_229051_1_)
    {
        float f = (float) p_229051_0_.innerRotation + p_229051_1_;
        float f1 = Mth.sin(f * 0.2F) / 2.0F + 0.5F;
        f1 = (f1 * f1 + f1) * 0.4F;
        return f1 - 1.4F;
    }

    @Override
    public void render(SpiritBombEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn)
    {
        matrixStackIn.pushPose();
        float f = func_229051_a_(entityIn, partialTicks);
        float _tick = (float) entityIn.tickCount + partialTicks;
        float f1 = ((float) entityIn.innerRotation + partialTicks) * 3.0F;
        VertexConsumer ivertexbuilder = bufferIn.getBuffer(MWAWRenderType.getTextureSwirl(this.func_225633_a_(), this.func_225634_a_(_tick), this.func_225634_a_(_tick)));
        float size = 5.0F * entityIn.getCharge();
        matrixStackIn.translate(0.0F, 1.5F, 0.0F);
        matrixStackIn.scale(size, size, size);
        int i = OverlayTexture.NO_OVERLAY;
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(f1));
        matrixStackIn.mulPose(new Quaternion(new Vector3f(field_229047_f_, 0.0F, field_229047_f_), 60.0F, true));
        this.model.renderToBuffer(matrixStackIn, ivertexbuilder, packedLightIn, i, 1.0F, 1.0F, 1.0F, 1.0F);
        matrixStackIn.popPose();

        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    protected float func_225634_a_(float p_225634_1_)
    {
        return p_225634_1_ * 0.001F;
    }

    protected ResourceLocation func_225633_a_()
    {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getTextureLocation(SpiritBombEntity entity)
    {
        return TEXTURE;
    }

}
