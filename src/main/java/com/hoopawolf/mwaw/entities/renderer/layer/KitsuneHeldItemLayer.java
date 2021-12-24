package com.hoopawolf.mwaw.entities.renderer.layer;

import com.hoopawolf.mwaw.entities.KitsuneEntity;
import com.hoopawolf.mwaw.entities.model.KitsuneModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KitsuneHeldItemLayer extends RenderLayer<KitsuneEntity, KitsuneModel<KitsuneEntity>>
{
    public KitsuneHeldItemLayer(RenderLayerParent<KitsuneEntity, KitsuneModel<KitsuneEntity>> p_i50938_1_)
    {
        super(p_i50938_1_);
    }

    @Override
    public void render(PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, KitsuneEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch)
    {
        matrixStackIn.pushPose();
        matrixStackIn.translate((this.getParentModel()).Head.x / 16.0F, (this.getParentModel()).Head.y / 16.0F, (this.getParentModel()).Head.z / 16.0F);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(netHeadYaw));
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(headPitch));

        matrixStackIn.translate(0.0F, 0.1F, -0.5D);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(180.0F));
        matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90.0F));

        ItemStack itemstack = entitylivingbaseIn.getItemBySlot(EquipmentSlot.MAINHAND);
        Minecraft.getInstance().getItemInHandRenderer().renderItem(entitylivingbaseIn, itemstack, ItemTransforms.TransformType.GROUND, false, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.popPose();
    }
}
