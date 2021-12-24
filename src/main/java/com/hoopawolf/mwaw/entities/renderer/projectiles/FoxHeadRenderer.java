package com.hoopawolf.mwaw.entities.renderer.projectiles;

import com.hoopawolf.mwaw.entities.model.projectiles.FoxHeadModel;
import com.hoopawolf.mwaw.entities.projectiles.FoxHeadEntity;
import com.hoopawolf.mwaw.ref.Reference;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class FoxHeadRenderer extends EntityRenderer<FoxHeadEntity>
{
    private static final RenderType KITSUNE_SPARK_TEXTURE_TRANSPARANCY = RenderType.entityTranslucent(new ResourceLocation(Reference.MOD_ID, "textures/entity/kitsune.png"));
    private final Random rnd = new Random();
    private final FoxHeadModel model;

    public FoxHeadRenderer(EntityRendererProvider.Context manager)
    {
        super(manager);
        this.model = new FoxHeadModel(manager.bakeLayer(FoxHeadModel.LAYER_LOCATION));
    }

    @Override
    protected int getBlockLightLevel(FoxHeadEntity entityIn, BlockPos partialTicks)
    {
        return 15;
    }

    @Override
    public void render(FoxHeadEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn)
    {
        matrixStackIn.pushPose();
        float f = Mth.rotLerp(entityIn.yRotO, entityIn.getYRot(), partialTicks);
        float f1 = Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot());
        matrixStackIn.translate(0.0D, 0.3F, 0.0D);
        matrixStackIn.translate(0.0D, 0.3F + (Math.sin(partialTicks) * 0.05F), 0.0D);
        matrixStackIn.scale(1.0F, 1.0F, 1.0F);
        matrixStackIn.mulPose(new Quaternion(180.0F, 180.0F, 0.0F, true));
        this.model.setupAnim(entityIn, 0.0F, 0.0F, 0.0F, f, f1);
        VertexConsumer ivertexbuilder1 = bufferIn.getBuffer(KITSUNE_SPARK_TEXTURE_TRANSPARANCY);
        this.model.renderToBuffer(matrixStackIn, ivertexbuilder1, packedLightIn, OverlayTexture.NO_OVERLAY, 0.5F, 1.0F, 0.5F, 0.7F * entityIn.getSpawnPercentage());
        matrixStackIn.popPose();
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public Vec3 getRenderOffset(FoxHeadEntity entityIn, float partialTicks)
    {
        return new Vec3(this.rnd.nextGaussian() * 0.02D, 0.0D, this.rnd.nextGaussian() * 0.02D);
    }

    @Override
    public ResourceLocation getTextureLocation(FoxHeadEntity entity)
    {
        return null;
    }
}
