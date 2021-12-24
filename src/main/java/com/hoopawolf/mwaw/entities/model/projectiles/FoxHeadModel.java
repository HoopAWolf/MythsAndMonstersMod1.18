package com.hoopawolf.mwaw.entities.model.projectiles;

import com.google.common.collect.ImmutableList;
import com.hoopawolf.mwaw.ref.Reference;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FoxHeadModel<T extends Entity> extends EntityModel<T>
{
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Reference.MOD_ID, "foxheadmodel"), "main");
    public final ModelPart head;
    private static final float HEAD_HEIGHT = 16.5F;

    public FoxHeadModel(ModelPart p_170566_)
    {
        this.head = p_170566_.getChild("head");
    }

    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("head", CubeListBuilder.create()
                .texOffs(27, 5).addBox(-4.0F, -5.5F, -3.625F, 2, 2, 1, new CubeDeformation(0.0F))
                .texOffs(25, 17).addBox(2.0F, -5.5F, -3.625F, 2, 2, 1, new CubeDeformation(0.0F))
                .texOffs(38, 0).addBox(-4.0F, -3.5F, -5.625F, 8, 6, 6, new CubeDeformation(0.0F))
                .texOffs(27, 0).addBox(-2.0F, 0.5F, -8.625F, 4, 2, 3, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    public void prepareMobModel(T p_102664_, float p_102665_, float p_102666_, float p_102667_)
    {
        this.head.setPos(-1.0F, 16.5F, -3.0F);
        this.head.yRot = 0.0F;
    }

    protected Iterable<ModelPart> headParts()
    {
        return ImmutableList.of(this.head);
    }

    public void setupAnim(T p_102669_, float p_102670_, float p_102671_, float p_102672_, float p_102673_, float p_102674_)
    {
        this.head.xRot = p_102674_ * ((float) Math.PI / 180F);
        this.head.yRot = p_102673_ * ((float) Math.PI / 180F);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        head.render(poseStack, buffer, packedLight, packedOverlay);
    }
}
