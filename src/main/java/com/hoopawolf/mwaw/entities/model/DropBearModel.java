package com.hoopawolf.mwaw.entities.model;

import com.hoopawolf.mwaw.entities.DropBearEntity;
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

public class DropBearModel<T extends Entity> extends EntityModel<T>
{
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Reference.MOD_ID, "dropbearmodel"), "main");
    private final ModelPart Body;
    private final ModelPart Face;
    private final ModelPart FrontRightLeg;
    private final ModelPart FrontLeftLeg;
    private final ModelPart BackLeftLeg;
    private final ModelPart BackRightLeg;

    public DropBearModel(ModelPart root)
    {
        this.Body = root.getChild("Body");
        this.Face = root.getChild("Body").getChild("Face");
        this.FrontRightLeg = root.getChild("Body").getChild("FrontRightLeg");
        this.FrontLeftLeg = root.getChild("Body").getChild("FrontLeftLeg");
        this.BackLeftLeg = root.getChild("Body").getChild("BackLeftLeg");
        this.BackRightLeg = root.getChild("Body").getChild("BackRightLeg");
    }

    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Body = partdefinition.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -3.6363F, -4.7147F, 10.0F, 6.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 15.5F, -0.25F));

        PartDefinition Face = Body.addOrReplaceChild("Face", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.125F, -7.375F, 4.0F, 5.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 17).addBox(-4.0F, -4.125F, -6.375F, 8.0F, 8.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(36, 24).addBox(-7.0F, -7.125F, -3.375F, 6.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(28, 17).addBox(1.0F, -7.125F, -3.375F, 6.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.5113F, -3.3397F));

        PartDefinition FrontRightLeg = Body.addOrReplaceChild("FrontRightLeg", CubeListBuilder.create().texOffs(0, 31).addBox(-2.0F, -0.5F, -2.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 0.9659F, -2.4912F));

        PartDefinition FrontLeftLeg = Body.addOrReplaceChild("FrontLeftLeg", CubeListBuilder.create().texOffs(24, 27).addBox(-2.0F, -0.5F, -2.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 0.9659F, -2.4912F));

        PartDefinition BackLeftLeg = Body.addOrReplaceChild("BackLeftLeg", CubeListBuilder.create().texOffs(31, 0).addBox(-2.0F, -0.5F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(4.0F, 1.0341F, 3.9912F));

        PartDefinition BackRightLeg = Body.addOrReplaceChild("BackRightLeg", CubeListBuilder.create().texOffs(36, 36).addBox(-1.0F, -0.5F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 1.0341F, 3.9912F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        DropBearEntity entityIn = (DropBearEntity) entity;
        Body.xRot = -0.2618F;
        Body.yRot = 0.0F;
        Body.zRot = 0.0F;
        Face.xRot = 0.2618F;
        Face.yRot = 0.0F;
        Face.zRot = 0.0F;
        FrontRightLeg.xRot = 0.2618F;
        FrontRightLeg.yRot = 0.0F;
        FrontRightLeg.zRot = 0.0F;
        FrontLeftLeg.xRot = 0.2618F;
        FrontLeftLeg.yRot = 0.0F;
        FrontLeftLeg.zRot = 0.0F;
        BackLeftLeg.xRot = 0.2618F;
        BackLeftLeg.yRot = 0.0F;
        BackLeftLeg.zRot = 0.0F;
        BackRightLeg.xRot = 0.2618F;
        BackRightLeg.yRot = 0.0F;
        BackRightLeg.zRot = 0.0F;

        if (!entityIn.isHugging())
        {
            this.Face.xRot = this.Face.xRot + (headPitch * ((float) Math.PI / 180F)) + (float) Math.toRadians(entityIn.getHeadRotation().getX());
            this.Face.yRot = (netHeadYaw * (((float) Math.PI / 180F))) + (float) Math.toRadians(entityIn.getHeadRotation().getY());
        } else
        {
            this.Face.xRot = this.Face.xRot + (float) Math.toRadians(entityIn.getHeadRotation().getX());
            this.Face.yRot = (float) Math.toRadians(entityIn.getHeadRotation().getY());
        }

        this.Face.zRot = (float) Math.toRadians(entityIn.getHeadRotation().getZ());

        this.Body.xRot = this.Body.xRot + (float) Math.toRadians(entityIn.getBodyRotation().getX());
        this.Body.yRot = (float) Math.toRadians(entityIn.getBodyRotation().getY());
        this.Body.zRot = (float) Math.toRadians(entityIn.getBodyRotation().getZ());

        this.FrontRightLeg.xRot = (entityIn.grabbedTarget() ? 0 : Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount) + this.FrontRightLeg.xRot + (float) Math.toRadians(entityIn.getRightArmRotation().getX());
        this.FrontRightLeg.yRot = (float) Math.toRadians(entityIn.getRightArmRotation().getY());
        this.FrontRightLeg.zRot = (float) Math.toRadians(entityIn.getRightArmRotation().getZ());

        this.FrontLeftLeg.xRot = (entityIn.grabbedTarget() ? 0 : Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount) + this.FrontLeftLeg.xRot + (float) Math.toRadians(entityIn.getLeftArmRotation().getX());
        this.FrontLeftLeg.yRot = (float) Math.toRadians(entityIn.getLeftArmRotation().getY());
        this.FrontLeftLeg.zRot = (float) Math.toRadians(entityIn.getLeftArmRotation().getZ());

        this.BackRightLeg.xRot = (entityIn.grabbedTarget() ? 0 : Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount) + this.BackRightLeg.xRot + (float) Math.toRadians(entityIn.getRightLegRotation().getX());
        this.BackRightLeg.yRot = (float) Math.toRadians(entityIn.getRightLegRotation().getY());
        this.BackRightLeg.zRot = (float) Math.toRadians(entityIn.getRightLegRotation().getZ());

        this.BackLeftLeg.xRot = (entityIn.grabbedTarget() ? 0 : Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount) + this.BackLeftLeg.xRot + (float) Math.toRadians(entityIn.getLeftLegRotation().getX());
        this.BackLeftLeg.yRot = (float) Math.toRadians(entityIn.getLeftLegRotation().getY());
        this.BackLeftLeg.zRot = (float) Math.toRadians(entityIn.getLeftLegRotation().getZ());

        entityIn.animation.animationTick(entityIn.getEntityData(), entityIn.getAnimationSpeed());
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
    {
        Body.render(poseStack, buffer, packedLight, packedOverlay);
    }
}