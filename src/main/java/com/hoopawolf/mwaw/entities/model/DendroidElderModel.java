package com.hoopawolf.mwaw.entities.model;

import com.hoopawolf.mwaw.entities.DendroidElderEntity;
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
public class DendroidElderModel<T extends Entity> extends EntityModel<T>
{
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Reference.MOD_ID, "dendroideldermodel"), "main");
    private final ModelPart Head;
    private final ModelPart RightArm;
    private final ModelPart RightArmJoint;
    private final ModelPart LeftArm;
    private final ModelPart LeftArmJoint;
    private final ModelPart RightFoot;
    private final ModelPart LeftFoot;
    private final ModelPart UpperBody;
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;

    public DendroidElderModel(ModelPart root)
    {
        this.Head = root.getChild("UpperBody").getChild("Head");
        this.RightArm = root.getChild("UpperBody").getChild("RightArm");
        this.RightArmJoint = root.getChild("UpperBody").getChild("RightArm").getChild("RightArmJoint");
        this.LeftArm = root.getChild("UpperBody").getChild("LeftArm");
        this.LeftArmJoint = root.getChild("UpperBody").getChild("LeftArm").getChild("LeftArmJoint");
        this.RightFoot = root.getChild("RightLeg").getChild("RightFoot");
        this.LeftFoot = root.getChild("LeftLeg").getChild("LeftFoot");
        this.UpperBody = root.getChild("UpperBody");
        this.RightLeg = root.getChild("RightLeg");
        this.LeftLeg = root.getChild("LeftLeg");
    }

    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition UpperBody = partdefinition.addOrReplaceChild("UpperBody", CubeListBuilder.create().texOffs(74, 74).addBox(-6.6667F, -28.1667F, 6.5F, 6.0F, 17.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-8.6667F, -21.1667F, -3.5F, 18.0F, 13.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(0, 39).addBox(-7.6667F, -9.1667F, -2.5F, 16.0F, 9.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.3333F, 1.1667F, -0.5F));

        PartDefinition bb_main = UpperBody.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(36, 45).addBox(-4.0F, -40.0F, -6.0F, 8.0F, 8.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(0.3333F, 22.8333F, -0.5F));

        PartDefinition Head = UpperBody.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 23).addBox(-12.0F, -15.25F, -1.25F, 24.0F, 16.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(0, 41).addBox(0.0F, -16.25F, -5.25F, 0.0F, 15.0F, 14.0F, new CubeDeformation(0.0F))
                .texOffs(72, 52).addBox(-4.0F, -7.25F, -6.25F, 8.0F, 9.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.3333F, -22.9167F, -1.25F));

        PartDefinition RightArm = UpperBody.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(12, 94).addBox(-6.0F, -2.625F, -1.3333F, 6.0F, 13.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-8.6667F, -19.5417F, 1.8333F));

        PartDefinition RightArmJoint = RightArm.addOrReplaceChild("RightArmJoint", CubeListBuilder.create().texOffs(42, 83).addBox(-2.0F, 1.0F, -2.0F, 4.0F, 18.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 9.375F, 0.6667F));

        PartDefinition RightClaw = RightArmJoint.addOrReplaceChild("RightClaw", CubeListBuilder.create().texOffs(0, 70).addBox(-4.0F, -8.0F, -5.0F, 2.0F, 28.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(20, 62).addBox(-4.0F, -9.0F, 0.0F, 3.0F, 24.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition LeftArm = UpperBody.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(96, 40).addBox(0.0F, -2.625F, -0.75F, 6.0F, 13.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(9.3333F, -19.5417F, 1.25F));

        PartDefinition Mushroom = LeftArm.addOrReplaceChild("Mushroom", CubeListBuilder.create().texOffs(92, 69).addBox(6.0F, -52.0F, 5.0F, 10.0F, 7.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(92, 59).addBox(11.0F, -52.0F, 0.0F, 0.0F, 7.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offset(-9.0F, 42.375F, -1.75F));

        PartDefinition LeftArmJoint = LeftArm.addOrReplaceChild("LeftArmJoint", CubeListBuilder.create().texOffs(42, 83).addBox(-2.0F, 0.0F, -3.0F, 4.0F, 18.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 10.375F, 2.25F));

        PartDefinition LeftClaw = LeftArmJoint.addOrReplaceChild("LeftClaw", CubeListBuilder.create().texOffs(20, 62).addBox(1.0F, -9.0F, -1.0F, 3.0F, 24.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(0, 70).addBox(1.0F, -8.0F, -6.0F, 2.0F, 28.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 11.0F, 0.0F));

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(92, 92).addBox(-3.5F, -0.25F, -3.0F, 6.0F, 11.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-4.5F, 1.25F, 0.0F));

        PartDefinition RightFoot = RightLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create().texOffs(42, 63).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 12.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 10.75F, 1.0F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(92, 92).addBox(-3.5F, -0.25F, -3.0F, 6.0F, 11.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(5.5F, 1.25F, 0.0F));

        PartDefinition LeftFoot = LeftLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create().texOffs(42, 63).addBox(-4.0F, 0.0F, -4.0F, 8.0F, 12.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.5F, 10.75F, 1.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        DendroidElderEntity entityIn = ((DendroidElderEntity)entity);
        this.Head.xRot = (float) Math.toRadians(entityIn.getHeadRotation().getX());
        this.Head.yRot = (float) Math.toRadians(entityIn.getHeadRotation().getY());
        this.Head.zRot = (float) Math.toRadians(entityIn.getHeadRotation().getZ());

        this.UpperBody.xRot = (float) Math.toRadians(entityIn.getBodyRotation().getX());
        this.UpperBody.yRot = (float) Math.toRadians(entityIn.getBodyRotation().getY());
        this.UpperBody.zRot = (float) Math.toRadians(entityIn.getBodyRotation().getZ());

        this.RightArm.xRot = (float) Math.toRadians(entityIn.getRightArmRotation().getX());
        this.RightArm.yRot = (float) Math.toRadians(entityIn.getRightArmRotation().getY());
        this.RightArm.zRot = (float) Math.toRadians(entityIn.getRightArmRotation().getZ());
        this.RightArmJoint.xRot = (float) Math.toRadians(entityIn.getRightJointRotation().getX());
        this.RightArmJoint.yRot = (float) Math.toRadians(entityIn.getRightJointRotation().getY());
        this.RightArmJoint.zRot = (float) Math.toRadians(entityIn.getRightJointRotation().getZ());

        this.LeftArm.xRot = (float) Math.toRadians(entityIn.getLeftArmRotation().getX());
        this.LeftArm.yRot = (float) Math.toRadians(entityIn.getLeftArmRotation().getY());
        this.LeftArm.zRot = (float) Math.toRadians(entityIn.getLeftArmRotation().getZ());
        this.LeftArmJoint.xRot = (float) Math.toRadians(entityIn.getLeftJointRotation().getX());
        this.LeftArmJoint.yRot = (float) Math.toRadians(entityIn.getLeftJointRotation().getY());
        this.LeftArmJoint.zRot = (float) Math.toRadians(entityIn.getLeftJointRotation().getZ());

        this.RightLeg.xRot = (float) Math.toRadians(entityIn.getRightLegRotation().getX());
        this.RightLeg.yRot = (float) Math.toRadians(entityIn.getRightLegRotation().getY());
        this.RightLeg.zRot = (float) Math.toRadians(entityIn.getRightLegRotation().getZ());
        this.RightFoot.xRot = (float) Math.toRadians(entityIn.getRightFootRotation().getX());
        this.RightFoot.yRot = (float) Math.toRadians(entityIn.getRightFootRotation().getY());
        this.RightFoot.zRot = (float) Math.toRadians(entityIn.getRightFootRotation().getZ());

        this.LeftLeg.xRot = (float) Math.toRadians(entityIn.getLeftLegRotation().getX());
        this.LeftLeg.yRot = (float) Math.toRadians(entityIn.getLeftLegRotation().getY());
        this.LeftLeg.zRot = (float) Math.toRadians(entityIn.getLeftLegRotation().getZ());
        this.LeftFoot.xRot = (float) Math.toRadians(entityIn.getLeftFootRotation().getX());
        this.LeftFoot.yRot = (float) Math.toRadians(entityIn.getLeftFootRotation().getY());
        this.LeftFoot.zRot = (float) Math.toRadians(entityIn.getLeftFootRotation().getZ());

        if (entityIn.getState() == 0)
        {
            this.Head.yRot = Head.yRot + netHeadYaw * ((float) Math.PI / 180F);
            this.Head.xRot = Head.xRot + headPitch * ((float) Math.PI / 180F);

            this.RightArm.xRot = RightArm.xRot + Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F;
            this.LeftArm.xRot = LeftArm.xRot + Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
            if (!entityIn.isDefensiveMode())
            {
                this.RightArmJoint.xRot = RightArmJoint.xRot + Mth.clamp(Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount, -0.5F, 0);
            }
            this.LeftArmJoint.xRot = LeftArmJoint.xRot + Mth.clamp(Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount, -0.5F, 0);
            this.RightLeg.xRot = RightLeg.xRot + Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
            this.LeftLeg.xRot = LeftLeg.xRot + Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
            this.RightFoot.xRot = RightFoot.xRot + Mth.clamp(Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount, 0, 0.5F);
            this.LeftFoot.xRot = LeftFoot.xRot + Mth.clamp(Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount, 0, 0.5F);
        }

        entityIn.animation.animationTick(entityIn.getEntityData(), entityIn.getAnimationSpeed());
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        UpperBody.render(poseStack, buffer, packedLight, packedOverlay);
        RightLeg.render(poseStack, buffer, packedLight, packedOverlay);
        LeftLeg.render(poseStack, buffer, packedLight, packedOverlay);
    }
}