package com.hoopawolf.mwaw.entities.model;

import com.hoopawolf.mwaw.entities.JackalopeEntity;
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

public class JackalopeModel<T extends Entity> extends EntityModel<T>
{
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Reference.MOD_ID, "jackalopemodel"), "main");
    private final ModelPart Body;
    private final ModelPart Head;
    private final ModelPart RightEar;
    private final ModelPart LeftEar;
    private final ModelPart FrontRightLeg;
    private final ModelPart FrontLeftLeg;
    private final ModelPart BackRightLeg;
    private final ModelPart BackLeftLeg;

    public JackalopeModel(ModelPart root) {
        this.Body = root.getChild("Body");
        this.Head = root.getChild("Body").getChild("Head");
        this.RightEar = root.getChild("Body").getChild("Head").getChild("RightEar");
        this.LeftEar = root.getChild("Body").getChild("Head").getChild("LeftEar");
        this.FrontRightLeg = root.getChild("Body").getChild("FrontRightLeg");
        this.FrontLeftLeg = root.getChild("Body").getChild("FrontLeftLeg");
        this.BackRightLeg = root.getChild("Body").getChild("BackRightLeg");
        this.BackLeftLeg = root.getChild("Body").getChild("BackLeftLeg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Body = partdefinition.addOrReplaceChild("Body", CubeListBuilder.create()
                .texOffs(31, 11).addBox(-2.0F, -2.8191F, 8.5F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-3.0F, -3.0F, -0.342F, 6.0F, 5.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 14.0F, -4.0F));

        PartDefinition Head = Body.addOrReplaceChild("Head", CubeListBuilder.create()
                .texOffs(0, 20).addBox(-2.0F, -8.25F, -5.5F, 0.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(19, 8).addBox(2.0F, -8.25F, -5.5F, 0.0F, 5.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(18, 20).addBox(-2.5F, -3.25F, -4.5F, 5.0F, 4.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 14).addBox(-0.5F, -1.25F, -5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.75F, -1.5F));

        PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create()
                .texOffs(21, 0).addBox(-1.7588F, -6.4158F, 0.4623F, 3.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, -2.75F, 0.0F));

        PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-1.2412F, -6.4158F, 0.4623F, 3.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(1.5F, -2.75F, 0.0F));

        PartDefinition FrontRightLeg = Body.addOrReplaceChild("FrontRightLeg", CubeListBuilder.create()
                .texOffs(30, 0).addBox(-1.0F, 0.85F, 1.0F, 2.0F, 9.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 0.5F, -1.0F));

        PartDefinition FrontLeftLeg = Body.addOrReplaceChild("FrontLeftLeg", CubeListBuilder.create()
                .texOffs(0, 31).addBox(-1.0F, 0.85F, 1.0F, 2.0F, 9.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 0.5F, -1.0F));

        PartDefinition BackRightLeg = Body.addOrReplaceChild("BackRightLeg", CubeListBuilder.create()
                .texOffs(9, 28).addBox(-1.5F, -0.5F, -0.5F, 3.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, 1.6206F, 5.816F));

        PartDefinition BackLeftLeg = Body.addOrReplaceChild("BackLeftLeg", CubeListBuilder.create()
                .texOffs(21, 30).addBox(-1.5F, -0.5F, -0.5F, 3.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, 1.6206F, 5.816F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        JackalopeEntity entityIn = (JackalopeEntity) entity;

        this.Body.xRot = -0.3491F;

        this.Head.xRot = 0.3491F + (headPitch * ((float) Math.PI / 180F));
        this.Head.yRot = netHeadYaw * ((float) Math.PI / 180F);

        this.RightEar.xRot = 0.0873F;
        this.RightEar.yRot = 0.2618F;
        this.RightEar.zRot = -0.3491F;

        this.LeftEar.xRot = 0.0873F;
        this.LeftEar.yRot = -0.2618F;
        this.LeftEar.zRot = 0.3491F;

        this.FrontRightLeg.xRot = 0.3491F;
        this.FrontLeftLeg.xRot = 0.3491F;
        this.BackRightLeg.xRot = 0.3491F;
        this.BackLeftLeg.xRot = 0.3491F;

        this.BackRightLeg.xRot = this.BackRightLeg.xRot + Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.BackLeftLeg.xRot = this.BackLeftLeg.xRot + Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        this.FrontRightLeg.xRot = this.FrontRightLeg.xRot + Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        this.FrontLeftLeg.xRot = this.FrontLeftLeg.xRot + Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;

        if (entityIn.isEscaping())
        {
            this.Body.xRot -= ageInTicks * 0.5F;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (this.young)
        {
            poseStack.pushPose();
            poseStack.scale(0.56666666F, 0.56666666F, 0.56666666F);
            poseStack.translate(0.0D, 1.35D, 0.15D);
            Body.render(poseStack, buffer, packedLight, packedOverlay);
            poseStack.popPose();
        } else
        {
            Body.render(poseStack, buffer, packedLight, packedOverlay);
        }
    }
}