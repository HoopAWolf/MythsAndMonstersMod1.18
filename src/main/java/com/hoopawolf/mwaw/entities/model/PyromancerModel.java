package com.hoopawolf.mwaw.entities.model;

import com.hoopawolf.mwaw.entities.PyromancerEntity;
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

public class PyromancerModel<T extends Entity> extends EntityModel<T>
{
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Reference.MOD_ID, "pyromancermodel"), "main");
    private final ModelPart Head;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart Body;

    public PyromancerModel(ModelPart root)
    {
        this.Head = root.getChild("Head");
        this.RightArm = root.getChild("RightArm");
        this.LeftArm = root.getChild("LeftArm");
        this.RightLeg = root.getChild("RightLeg");
        this.LeftLeg = root.getChild("LeftLeg");
        this.Body = root.getChild("Body");
    }

    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -13.0833F, -5.0F, 10.0F, 13.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(0, 23).addBox(-4.0F, -10.0833F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(58, 58).addBox(-7.0F, -19.0833F, -1.0F, 2.0F, 17.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(58, 19).addBox(5.0F, -19.0833F, -1.0F, 2.0F, 17.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(12, 53).addBox(-1.0F, -19.0833F, 5.0F, 2.0F, 17.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(58, 38).addBox(-1.0F, -19.0833F, -7.0F, 2.0F, 17.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.9167F, 1.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(0, 41).addBox(-4.75F, -4.0F, -3.25F, 5.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(34, 51).addBox(-3.25F, -3.0F, -1.25F, 3.0F, 16.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.25F, -5.0F, 1.25F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(34, 19).addBox(-0.25F, -4.0F, -3.25F, 5.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(22, 48).addBox(0.25F, -3.0F, -1.25F, 3.0F, 16.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(5.25F, -5.0F, 1.25F));

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(0, 53).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 16.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, 8.0F, 1.5F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(46, 51).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 16.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, 8.0F, 1.5F));

        PartDefinition Body = partdefinition.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(32, 32).addBox(-5.0F, -8.0F, -0.75F, 10.0F, 16.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.75F));

        PartDefinition Cape = Body.addOrReplaceChild("Cape", CubeListBuilder.create().texOffs(40, 0).addBox(-5.0F, -1.0F, -0.5F, 10.0F, 18.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.0F, 2.75F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        PyromancerEntity entityIn = (PyromancerEntity) entity;

        this.Head.xRot = (float) Math.toRadians(entityIn.getHeadRotation().getX());
        this.Head.yRot = (float) Math.toRadians(entityIn.getHeadRotation().getY());
        this.Head.zRot = (float) Math.toRadians(entityIn.getHeadRotation().getZ());

        this.Body.xRot = (float) Math.toRadians(entityIn.getBodyRotation().getX());
        this.Body.yRot = (float) Math.toRadians(entityIn.getBodyRotation().getY());
        this.Body.zRot = (float) Math.toRadians(entityIn.getBodyRotation().getZ());

        this.RightArm.xRot = (float) Math.toRadians(entityIn.getRightArmRotation().getX());
        this.RightArm.yRot = (float) Math.toRadians(entityIn.getRightArmRotation().getY());
        this.RightArm.zRot = (float) Math.toRadians(entityIn.getRightArmRotation().getZ());

        this.LeftArm.xRot = (float) Math.toRadians(entityIn.getLeftArmRotation().getX());
        this.LeftArm.yRot = (float) Math.toRadians(entityIn.getLeftArmRotation().getY());
        this.LeftArm.zRot = (float) Math.toRadians(entityIn.getLeftArmRotation().getZ());

        this.RightLeg.xRot = (float) Math.toRadians(entityIn.getRightLegRotation().getX());
        this.RightLeg.yRot = (float) Math.toRadians(entityIn.getRightLegRotation().getY());
        this.RightLeg.zRot = (float) Math.toRadians(entityIn.getRightLegRotation().getZ());

        this.LeftLeg.xRot = (float) Math.toRadians(entityIn.getLeftLegRotation().getX());
        this.LeftLeg.yRot = (float) Math.toRadians(entityIn.getLeftLegRotation().getY());
        this.LeftLeg.zRot = (float) Math.toRadians(entityIn.getLeftLegRotation().getZ());

        this.Head.yRot = Head.yRot + (netHeadYaw * ((float) Math.PI / 180F));
        this.Head.xRot = Head.xRot + (headPitch * ((float) Math.PI / 180F));

        if (!entityIn.isFlying())
        {
            this.RightArm.xRot = RightArm.xRot + Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F;
            this.LeftArm.xRot = LeftArm.xRot + Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
            this.RightLeg.xRot = RightLeg.xRot + Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
            this.LeftLeg.xRot = LeftLeg.xRot + Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        } else
        {
            this.RightArm.yRot = RightArm.yRot + (float) Math.toRadians(35);
            this.RightArm.zRot = RightArm.zRot + (float) Math.toRadians(12.5);
            this.LeftArm.yRot = LeftArm.yRot + (float) Math.toRadians(-35);
            this.LeftArm.zRot = LeftArm.zRot + (float) Math.toRadians(-12.5);

            this.RightLeg.xRot = RightLeg.xRot + (float) Math.toRadians(15);
            this.RightLeg.yRot = RightLeg.yRot + (float) Math.toRadians(10);
            this.LeftLeg.xRot = LeftLeg.xRot + (float) Math.toRadians(15);
            this.LeftLeg.yRot = LeftLeg.yRot + (float) Math.toRadians(-10);
        }

        entityIn.animation.animationTick(entityIn.getEntityData(), entityIn.getAnimationSpeed());
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        Head.render(poseStack, buffer, packedLight, packedOverlay);
        RightArm.render(poseStack, buffer, packedLight, packedOverlay);
        LeftArm.render(poseStack, buffer, packedLight, packedOverlay);
        RightLeg.render(poseStack, buffer, packedLight, packedOverlay);
        LeftLeg.render(poseStack, buffer, packedLight, packedOverlay);
        Body.render(poseStack, buffer, packedLight, packedOverlay);
    }
}