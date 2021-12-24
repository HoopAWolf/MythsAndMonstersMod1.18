package com.hoopawolf.mwaw.entities.model;

import com.hoopawolf.mwaw.entities.GoldenRamEntity;
import com.hoopawolf.mwaw.ref.Reference;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GoldenRamModel<T extends Entity> extends EntityModel<T>
{
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Reference.MOD_ID, "goldenrammodel"), "main");
    private final ModelPart Body;
    private final ModelPart Head;
    private final ModelPart FrontRightLeg;
    private final ModelPart FrontLeftLeg;
    private final ModelPart BackRightLeg;
    private final ModelPart BackLeftLeg;
    private float headRotationAngleX;

    public GoldenRamModel(ModelPart root)
    {
        this.Body = root.getChild("Body");
        this.Head = root.getChild("Head");
        this.FrontRightLeg = root.getChild("FrontRightLeg");
        this.FrontLeftLeg = root.getChild("FrontLeftLeg");
        this.BackRightLeg = root.getChild("BackRightLeg");
        this.BackLeftLeg = root.getChild("BackLeftLeg");
    }

    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Body = partdefinition.addOrReplaceChild("Body", CubeListBuilder.create()
                .texOffs(0, 27).addBox(-4.0F, -3.0F, -8.25F, 8, 6, 16, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 10.0F, -0.75F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create()
                .texOffs(0, 27).addBox(3.0F, -0.3333F, -10.7083F, 3, 3, 3, new CubeDeformation(0.0F))
                .texOffs(0, 49).addBox(3.0F, -4.3333F, -7.7083F, 3, 7, 7, new CubeDeformation(0.0F))
                .texOffs(0, 33).addBox(-6.0F, -0.3333F, -10.7083F, 3, 3, 3, new CubeDeformation(0.0F))
                .texOffs(41, 42).addBox(-6.0F, -4.3333F, -7.7083F, 3, 7, 7, new CubeDeformation(0.0F))
                .texOffs(41, 0).addBox(-3.0F, -3.3333F, -9.7083F, 6, 6, 9, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 7.3333F, -5.2917F));

        PartDefinition FrontRightLeg = partdefinition.addOrReplaceChild("FrontRightLeg", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2.0F, -0.5F, -2.0F, 4, 11, 4, new CubeDeformation(0.0F)),
                PartPose.offset(-3.0F, 13.5F, -6.0F));

        PartDefinition FrontLeftLeg = partdefinition.addOrReplaceChild("FrontLeftLeg", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2.0F, -0.5F, -2.0F, 4, 11, 4, new CubeDeformation(0.0F)),
                PartPose.offset(3.0F, 13.5F, -6.0F));

        PartDefinition BackRightLeg = partdefinition.addOrReplaceChild("BackRightLeg", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2.0F, -0.5F, -2.0F, 4, 11, 4, new CubeDeformation(0.0F)),
                PartPose.offset(-3.0F, 13.5F, 6.0F));

        PartDefinition BackLeftLeg = partdefinition.addOrReplaceChild("BackLeftLeg", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-2.0F, -0.5F, -2.0F, 4, 11, 4, new CubeDeformation(0.0F)),
                PartPose.offset(3.0F, 13.5F, 6.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float partialTick, float netHeadYaw, float headPitch)
    {
        this.Head.xRot = headPitch * ((float) Math.PI / 180F);
        this.Head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.BackRightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.BackLeftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        this.FrontRightLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        this.FrontLeftLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;

        this.Head.xRot = headRotationAngleX;
    }

    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick)
    {
        GoldenRamEntity entityIn = (GoldenRamEntity)entity;
        this.Body.xRot = 0.0F;
        this.Head.y = 6.0F + entityIn.getHeadRotationPointY(partialTick) * 9.0F;
        this.headRotationAngleX = entityIn.getHeadRotationAngleX(partialTick);

        this.Head.zRot = entityIn.getShakeAngle(partialTick, 0.0F);
        this.Body.zRot = entityIn.getShakeAngle(partialTick, -0.16F);

        float f6 = entityIn.getRearingAmount(partialTick);
        float f7 = 1.0F - f6;

        this.Head.y = f6 + (1.0F - f6) * this.Head.y;
        this.Body.xRot = f6 * (-(float) Math.PI / 4F) + f7 * this.Body.xRot;
        this.FrontLeftLeg.y = 2.0F * f6 + 14.0F * f7;
        this.FrontRightLeg.y = 2.0F * f6 + 14.0F * f7;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        Body.render(poseStack, buffer, packedLight, packedOverlay);
        Head.render(poseStack, buffer, packedLight, packedOverlay);
        FrontRightLeg.render(poseStack, buffer, packedLight, packedOverlay);
        FrontLeftLeg.render(poseStack, buffer, packedLight, packedOverlay);
        BackRightLeg.render(poseStack, buffer, packedLight, packedOverlay);
        BackLeftLeg.render(poseStack, buffer, packedLight, packedOverlay);
    }
}
