package com.hoopawolf.mwaw.entities.model;

import com.hoopawolf.mwaw.entities.WolpertingerEntity;
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
public class WolpertingerModel<T extends Entity> extends EntityModel<T>
{
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Reference.MOD_ID, "wolpertingermodel"), "main");
    private final ModelPart Head;
    private final ModelPart FrontRightLeg;
    private final ModelPart FrontLeftLeg;
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart Body;
    private final ModelPart RightWing;
    private final ModelPart LeftWing;
    private final ModelPart Tail;

    private float jumpRotation;

    public WolpertingerModel(ModelPart root) {
        this.Head = root.getChild("Head");
        this.FrontRightLeg = root.getChild("FrontRightLeg");
        this.FrontLeftLeg = root.getChild("FrontLeftLeg");
        this.RightLeg = root.getChild("RightLeg");
        this.LeftLeg = root.getChild("LeftLeg");
        this.Body = root.getChild("Body");
        this.RightWing = root.getChild("RightWing");
        this.LeftWing = root.getChild("LeftWing");
        this.Tail = root.getChild("Body").getChild("Tail");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create()
                .texOffs(0, 30).addBox(2.0F, -4.8333F, -1.1345F, 0, 4, 4, new CubeDeformation(0.0F))
                .texOffs(9, 21).addBox(-2.0F, -4.8333F, -1.1345F, 0, 4, 4, new CubeDeformation(0.0F))
                .texOffs(0, 16).addBox(-2.5F, -0.8333F, -3.1345F, 5, 4, 5, new CubeDeformation(0.0F))
                .texOffs(6, 0).addBox(-0.5F, 0.6667F, -3.5201F, 1, 1, 1, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 11.8333F, -6.8655F));

        PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create()
                .texOffs(32, 0).addBox(2.5F, -13.0F, -7.0F, 1, 5, 2, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 12.1667F, 6.8655F));

        PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create()
                .texOffs(31, 32).addBox(-3.5F, -13.0F, -7.0F, 1, 5, 2, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 12.1667F, 6.8655F));

        PartDefinition FrontRightLeg = partdefinition.addOrReplaceChild("FrontRightLeg", CubeListBuilder.create()
                .texOffs(23, 32).addBox(-1.0F, 0.0F, -1.0F, 2, 7, 2, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-3.0F, 17.0F, -6.0F, -0.1745F, 0.0F, 0.0F));

        PartDefinition FrontLeftLeg = partdefinition.addOrReplaceChild("FrontLeftLeg", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 2, 7, 2, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(3.0F, 17.0F, -6.0F, -0.1745F, 0.0F, 0.0F));

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create()
                .texOffs(24, 24).addBox(-1.0F, -0.5F, -5.5F, 2, 1, 7, new CubeDeformation(0.0F)),
                PartPose.offset(-3.0F, 23.5F, 1.0F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create()
                .texOffs(13, 22).addBox(-1.0F, -0.5F, -5.5F, 2, 1, 7, new CubeDeformation(0.0F)),
                PartPose.offset(3.0F, 23.5F, 1.0F));

        PartDefinition Body = partdefinition.addOrReplaceChild("Body", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.0F, -4.0F, 2.6667F, 6, 6, 10, new CubeDeformation(0.0F))
                .texOffs(0, 25).addBox(-4.0F, -1.0F, 8.6667F, 2, 4, 5, new CubeDeformation(0.0F))
                .texOffs(9, 30).addBox(2.0F, -1.0F, 8.6667F, 2, 4, 5, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 15.0F, -8.6667F, -0.4363F, 0.0F, 0.0F));

        PartDefinition Tail = Body.addOrReplaceChild("Tail", CubeListBuilder.create()
                .texOffs(30, 14).addBox(-2.0F, -3.5F, 0.0F, 4, 5, 2, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -2.5F, 12.6667F));

        PartDefinition RightWing = partdefinition.addOrReplaceChild("RightWing", CubeListBuilder.create()
                .texOffs(24, 22).addBox(-6.0F, -0.5F, 0.0F, 6, 1, 1, new CubeDeformation(0.0F))
                .texOffs(14, 16).addBox(-6.0F, 0.0F, 1.0F, 5, 0, 6, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-3.0F, 13.5F, -4.0F, 0.0F, 0.6109F, -1.0472F));

        PartDefinition LeftWing = partdefinition.addOrReplaceChild("LeftWing", CubeListBuilder.create()
                .texOffs(22, 7).addBox(0.0F, -0.5F, 0.0F, 6, 1, 1, new CubeDeformation(0.0F))
                .texOffs(15, 0).addBox(1.0F, 0.0F, 0.0F, 5, 0, 7, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(3.0F, 13.5F, -4.0F, 0.0F, -0.6109F, 1.0472F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        WolpertingerEntity entityIn = (WolpertingerEntity) entity;

        float f = ageInTicks - (float) entityIn.tickCount;

        RightWing.yRot = 0.6109F;
        RightWing.zRot = -1.0472F;
        LeftWing.yRot = -0.6109F;
        LeftWing.zRot = 1.0472F;

        this.Head.xRot = headPitch * ((float) Math.PI / 180F);
        this.Head.yRot = netHeadYaw * ((float) Math.PI / 180F);

        this.jumpRotation = Mth.sin(entityIn.getJumpCompletion(f) * (float) Math.PI);

        if (!entityIn.isOnGround() && jumpRotation == 0)
        {
            RightWing.yRot = 0.47123894F + Mth.cos(ageInTicks * 1.5F) * (float) Math.PI * 0.05F;
            LeftWing.yRot = -RightWing.yRot;
            LeftWing.zRot = -0.47123894F;
            RightWing.zRot = 0.47123894F;

            this.LeftLeg.xRot = 0.8F * 50.0F * ((float) Math.PI / 180F);
            this.RightLeg.xRot = 0.8F * 50.0F * ((float) Math.PI / 180F);
        } else
        {
            if (jumpRotation < 0)
                this.Tail.zRot = Mth.cos(ageInTicks * 1.5F) * (float) Math.PI * 0.05F;

            this.LeftLeg.xRot = this.jumpRotation * 50.0F * ((float) Math.PI / 180F);
            this.RightLeg.xRot = this.jumpRotation * 50.0F * ((float) Math.PI / 180F);
            this.FrontLeftLeg.xRot = (this.jumpRotation * -40.0F - 11.0F) * ((float) Math.PI / 180F);
            this.FrontRightLeg.xRot = (this.jumpRotation * -40.0F - 11.0F) * ((float) Math.PI / 180F);
        }
    }

    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick)
    {
        WolpertingerEntity entityIn = (WolpertingerEntity) entity;
        super.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
        this.jumpRotation = Mth.sin(entityIn.getJumpCompletion(partialTick) * (float) Math.PI);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        Head.render(poseStack, buffer, packedLight, packedOverlay);
        FrontRightLeg.render(poseStack, buffer, packedLight, packedOverlay);
        FrontLeftLeg.render(poseStack, buffer, packedLight, packedOverlay);
        RightLeg.render(poseStack, buffer, packedLight, packedOverlay);
        LeftLeg.render(poseStack, buffer, packedLight, packedOverlay);
        Body.render(poseStack, buffer, packedLight, packedOverlay);
        RightWing.render(poseStack, buffer, packedLight, packedOverlay);
        LeftWing.render(poseStack, buffer, packedLight, packedOverlay);
    }
}