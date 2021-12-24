package com.hoopawolf.mwaw.entities.model;

import com.hoopawolf.mwaw.entities.KitsuneEntity;
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
public class KitsuneModel<T extends Entity> extends EntityModel<T>
{
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Reference.MOD_ID, "kitsunemodel"), "main");
    public final ModelPart Head;
    private final ModelPart Body;
    private final ModelPart RightTail;
    private final ModelPart LeftTail;
    private final ModelPart CenterTail;
    private final ModelPart FrontRightLeg;
    private final ModelPart FrontLeftLeg;
    private final ModelPart BackLeftLeg;
    private final ModelPart BackRightLeg;

    private final ModelPart villagerHead;
    private final ModelPart villagerBody;
    private final ModelPart rightVillagerLeg;
    private final ModelPart leftVillagerLeg;
    private final ModelPart villagerArms;

    public KitsuneModel(ModelPart root) {
        this.Head = root.getChild("Head");
        this.Body = root.getChild("Body");
        this.RightTail = root.getChild("RightTail");
        this.LeftTail = root.getChild("LeftTail");
        this.CenterTail = root.getChild("CenterTail");
        this.FrontRightLeg = root.getChild("FrontRightLeg");
        this.FrontLeftLeg = root.getChild("FrontLeftLeg");
        this.BackLeftLeg = root.getChild("BackLeftLeg");
        this.BackRightLeg = root.getChild("BackRightLeg");

        this.villagerHead = root.getChild("head");
        this.villagerBody = root.getChild("body");
        this.rightVillagerLeg = root.getChild("right_leg");
        this.leftVillagerLeg = root.getChild("left_leg");
        this.villagerArms = root.getChild("arms");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create()
                .texOffs(27, 5).addBox(-4.0F, -5.5F, -3.625F, 2, 2, 1, new CubeDeformation(0.0F))
                .texOffs(25, 17).addBox(2.0F, -5.5F, -3.625F, 2, 2, 1, new CubeDeformation(0.0F))
                .texOffs(38, 0).addBox(-4.0F, -3.5F, -5.625F, 8, 6, 6, new CubeDeformation(0.0F))
                .texOffs(27, 0).addBox(-2.0F, 0.5F, -8.625F, 4, 2, 3, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 11.5F, -8.375F));

        PartDefinition Body = partdefinition.addOrReplaceChild("Body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0F, -13.0F, -8.0F, 8, 6, 11, new CubeDeformation(0.0F))
                .texOffs(0, 17).addBox(-5.0F, -14.0F, -8.5F, 10, 6, 5, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition RightTail = partdefinition.addOrReplaceChild("RightTail", CubeListBuilder.create()
                .texOffs(0, 28).addBox(-2.316F, -4.2033F, 0.7943F, 4, 5, 9, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-3.0F, 14.5F, 3.0F, 0.5236F, 0.0F, -0.3491F));

        PartDefinition LeftTail = partdefinition.addOrReplaceChild("LeftTail", CubeListBuilder.create()
                .texOffs(21, 21).addBox(-2.0F, -4.2006F, 1.2807F, 4, 5, 9, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(2.0F, 14.5F, 3.0F, 0.6109F, 0.0F, 0.5236F));

        PartDefinition CenterTail = partdefinition.addOrReplaceChild("CenterTail", CubeListBuilder.create()
                .texOffs(17, 35).addBox(-2.0F, -1.5F, 0.0F, 4, 5, 9, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 12.5F, 3.0F, 0.8727F, 0.0F, 0.0F));

        PartDefinition FrontRightLeg = partdefinition.addOrReplaceChild("FrontRightLeg", CubeListBuilder.create()
                .texOffs(38, 12).addBox(-1.0F, 0.5F, -1.0F, 2, 7, 2, new CubeDeformation(0.0F)),
                PartPose.offset(-2.0F, 16.5F, -7.0F));

        PartDefinition FrontLeftLeg = partdefinition.addOrReplaceChild("FrontLeftLeg", CubeListBuilder.create()
                .texOffs(34, 35).addBox(-1.0F, 0.5F, -1.0F, 2, 7, 2, new CubeDeformation(0.0F)),
                PartPose.offset(2.0F, 16.5F, -7.0F));

        PartDefinition BackLeftLeg = partdefinition.addOrReplaceChild("BackLeftLeg", CubeListBuilder.create()
                .texOffs(0, 28).addBox(-1.0F, 0.5F, -1.0F, 2, 7, 2, new CubeDeformation(0.0F)),
                PartPose.offset(2.0F, 16.5F, 2.0F));

        PartDefinition BackRightLeg = partdefinition.addOrReplaceChild("BackRightLeg", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-1.0F, 0.5F, -1.0F, 2, 7, 2, new CubeDeformation(0.0F)),
                PartPose.offset(-2.0F, 16.5F, 2.0F));

        PartDefinition partdefinition1 = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F), PartPose.ZERO);
        partdefinition1.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F), PartPose.offset(0.0F, -2.0F, 0.0F));
        PartDefinition partdefinition3 = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F), PartPose.ZERO);
        partdefinition.addOrReplaceChild("arms", CubeListBuilder.create().texOffs(44, 22).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F).texOffs(44, 22).addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, true).texOffs(40, 38).addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F), PartPose.offsetAndRotation(0.0F, 3.0F, -1.0F, -0.75F, 0.0F, 0.0F));
        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(0, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(-2.0F, 12.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(0, 22).mirror().addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F), PartPose.offset(2.0F, 12.0F, 0.0F));


        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        KitsuneEntity entityIn = (KitsuneEntity) entity;
        if (entityIn.isShouting())
        {
            this.Head.xRot = -45.0F;
        } else
        {
            this.Head.xRot = headPitch * ((float) Math.PI / 180F);
            this.Head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        }

        this.FrontRightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.FrontLeftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        this.BackRightLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
        this.BackLeftLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;

        if (entityIn.getDeltaMovement().horizontalDistanceSqr() > 0.0001D)
        {
            this.CenterTail.xRot = -0.5727F;
            this.CenterTail.yRot = 0.0F;
            this.CenterTail.zRot = 0.0F;

            this.LeftTail.xRot = -0.5109F;
            this.LeftTail.yRot = 0.0F;
            this.LeftTail.zRot = 0.5236F;

            this.RightTail.xRot = -0.4236F;
            this.RightTail.yRot = 0.0F;
            this.RightTail.zRot = -0.3491F;
        } else
        {
            this.CenterTail.xRot = 0.8727F + Mth.cos(ageInTicks * 0.06F) * (float) Math.PI * 0.01F;
            this.CenterTail.yRot = Mth.cos(ageInTicks * -0.06F) * (float) Math.PI * 0.01F;
            this.CenterTail.zRot = Mth.cos(ageInTicks * -0.06F) * (float) Math.PI * 0.01F;

            this.LeftTail.xRot = 0.6109F - Mth.cos(ageInTicks * -0.06F) * (float) Math.PI * 0.01F;
            this.LeftTail.yRot = Mth.cos(ageInTicks * -0.06F) * (float) Math.PI * 0.01F;
            this.LeftTail.zRot = 0.5236F + Mth.cos(ageInTicks * -0.06F) * (float) Math.PI * 0.01F;

            this.RightTail.xRot = 0.5236F + Mth.cos(ageInTicks * 0.06F) * (float) Math.PI * 0.01F;
            this.RightTail.yRot = Mth.cos(ageInTicks * 0.06F) * (float) Math.PI * 0.01F;
            this.RightTail.zRot = -0.3491F - Mth.cos(ageInTicks * -0.06F) * (float) Math.PI * 0.01F;
        }

        this.villagerHead.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.villagerHead.xRot = headPitch * ((float)Math.PI / 180F);

        this.rightVillagerLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
        this.leftVillagerLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount * 0.5F;
        this.rightVillagerLeg.yRot = 0.0F;
        this.leftVillagerLeg.yRot = 0.0F;
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick)
    {
        KitsuneEntity entityIn = (KitsuneEntity)entity;
        if (entityIn.isVillagerForm())
        {
            showFoxModel(false, entityIn.getFoxPhase());
            showVillagerModel(true);
        } else
        {
            showFoxModel(true, entityIn.getFoxPhase());
            showVillagerModel(false);
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        Head.render(poseStack, buffer, packedLight, packedOverlay);
        Body.render(poseStack, buffer, packedLight, packedOverlay);
        RightTail.render(poseStack, buffer, packedLight, packedOverlay);
        LeftTail.render(poseStack, buffer, packedLight, packedOverlay);
        CenterTail.render(poseStack, buffer, packedLight, packedOverlay);
        FrontRightLeg.render(poseStack, buffer, packedLight, packedOverlay);
        FrontLeftLeg.render(poseStack, buffer, packedLight, packedOverlay);
        BackLeftLeg.render(poseStack, buffer, packedLight, packedOverlay);
        BackRightLeg.render(poseStack, buffer, packedLight, packedOverlay);

        villagerHead.render(poseStack, buffer, packedLight, packedOverlay);
        villagerBody.render(poseStack, buffer, packedLight, packedOverlay);
        villagerArms.render(poseStack, buffer, packedLight, packedOverlay);
        rightVillagerLeg.render(poseStack, buffer, packedLight, packedOverlay);
        leftVillagerLeg.render(poseStack, buffer, packedLight, packedOverlay);
    }

    private void showFoxModel(boolean _isShowing, int foxPhase)
    {
        Body.visible = _isShowing;
        RightTail.visible = (foxPhase > 2) && _isShowing;
        LeftTail.visible = (foxPhase > 1) && _isShowing;
        CenterTail.visible = (foxPhase > 0) && _isShowing;
        Head.visible = _isShowing;
        FrontRightLeg.visible = _isShowing;
        FrontLeftLeg.visible = _isShowing;
        BackLeftLeg.visible = _isShowing;
        BackRightLeg.visible = _isShowing;
    }

    private void showVillagerModel(boolean _isShowing)
    {
        villagerHead.visible = _isShowing;
        villagerBody.visible = _isShowing;
        villagerArms.visible = _isShowing;
        rightVillagerLeg.visible = _isShowing;
        leftVillagerLeg.visible = _isShowing;
    }
}