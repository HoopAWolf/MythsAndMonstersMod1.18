package com.hoopawolf.mwaw.entities.model;

import com.hoopawolf.mwaw.entities.SandWyrmEntity;
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

import java.util.ArrayList;

@OnlyIn(Dist.CLIENT)
public class SandWyrmModel<T extends Entity> extends EntityModel<T>
{
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Reference.MOD_ID, "sandwyrmmodel"), "main");
    private final ModelPart Head;
    private final ModelPart Body1;
    private final ModelPart LeftFrontLeg;
    private final ModelPart RightFrontLeg;
    private final ModelPart LeftBackLeg;
    private final ModelPart RightBackLeg;
    private final ModelPart Mouth;


    private final ArrayList<ModelPart> modelParts = new ArrayList<ModelPart>();

    public SandWyrmModel(ModelPart root) {
        this.Head = root.getChild("Head");
        this.Body1 = root.getChild("Body1");
        this.LeftFrontLeg = root.getChild("Body1").getChild("LeftFrontLeg");
        this.RightFrontLeg = root.getChild("Body1").getChild("RightFrontLeg");
        this.LeftBackLeg = root.getChild("Body1").getChild("Body2").getChild("LeftBackLeg");
        this.RightBackLeg = root.getChild("Body1").getChild("Body2").getChild("RightBackLeg");
        this.Mouth = root.getChild("Head").getChild("Mouth");
        modelParts.add(root.getChild("Body1"));
        modelParts.add(root.getChild("Body1").getChild("Body2"));
        modelParts.add(root.getChild("Body1").getChild("Body2").getChild("Tail1"));
        modelParts.add(root.getChild("Body1").getChild("Body2").getChild("Tail1").getChild("Tail2"));
        modelParts.add(root.getChild("Body1").getChild("Body2").getChild("Tail1").getChild("Tail2").getChild("Tail3"));
        modelParts.add(root.getChild("Body1").getChild("Body2").getChild("Tail1").getChild("Tail2").getChild("Tail3").getChild("EndTail"));
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create()
                .texOffs(73, 91).addBox(-7.0F, -3.0F, -9.0F, 14, 9, 5, new CubeDeformation(0.0F))
                .texOffs(0, 58).addBox(-9.0F, -6.0F, -21.0F, 18, 5, 21, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 14.0F, -20.0F));

        PartDefinition Mouth = Head.addOrReplaceChild("Mouth", CubeListBuilder.create()
                .texOffs(0, 84).addBox(-6.0F, -1.1603F, -12.5484F, 12, 3, 12, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 4.0F, -7.0F, -0.2618F, 0.0F, 0.0F));

        PartDefinition TuskRight = Head.addOrReplaceChild("TuskRight", CubeListBuilder.create()
                .texOffs(31, 84).addBox(-15.0F, -2.0F, -32.0F, 2, 4, 17, new CubeDeformation(0.0F)),
                PartPose.offset(7.0F, 1.0F, 9.0F));

        PartDefinition TuskLeft = Head.addOrReplaceChild("TuskLeft", CubeListBuilder.create()
                .texOffs(52, 91).addBox(13.0F, -2.0F, -31.0F, 2, 4, 17, new CubeDeformation(0.0F)),
                PartPose.offset(-7.0F, 1.0F, 8.0F));

        PartDefinition Body1 = partdefinition.addOrReplaceChild("Body1", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-12.0F, -4.0F, -4.0F, 24, 11, 19, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 14.0F, -20.0F));

        PartDefinition LeftFrontLeg = Body1.addOrReplaceChild("LeftFrontLeg", CubeListBuilder.create()
                .texOffs(0, 99).addBox(-2.0F, -1.0F, -2.5F, 5, 10, 7, new CubeDeformation(0.0F)),
                PartPose.offset(14.0F, 1.0F, -0.5F));

        PartDefinition RightFrontLeg = Body1.addOrReplaceChild("RightFrontLeg", CubeListBuilder.create()
                .texOffs(0, 99).addBox(-3.0F, -1.0F, -2.5F, 5, 10, 7, new CubeDeformation(0.0F)),
                PartPose.offset(-14.0F, 1.0F, -0.5F));

        PartDefinition Body2 = Body1.addOrReplaceChild("Body2", CubeListBuilder.create()
                .texOffs(0, 30).addBox(-11.0F, -5.0F, 2.0F, 22, 10, 18, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 2.0F, 13.0F));

        PartDefinition LeftBackLeg = Body2.addOrReplaceChild("LeftBackLeg", CubeListBuilder.create()
                .texOffs(24, 105).addBox(22.0F, -1.0F, 53.5F, 5, 7, 7, new CubeDeformation(0.0F)),
                PartPose.offset(-11.0F, 2.0F, -41.5F));

        PartDefinition RightBackLeg = Body2.addOrReplaceChild("RightBackLeg", CubeListBuilder.create()
                .texOffs(24, 105).addBox(-27.0F, -1.0F, 53.5F, 5, 7, 7, new CubeDeformation(0.0F)),
                PartPose.offset(11.0F, 2.0F, -41.5F));

        PartDefinition Tail1 = Body2.addOrReplaceChild("Tail1", CubeListBuilder.create()
                .texOffs(67, 0).addBox(-9.0F, -4.0F, 2.0F, 18, 8, 9, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 18.0F));

        PartDefinition Tail2 = Tail1.addOrReplaceChild("Tail2", CubeListBuilder.create()
                .texOffs(57, 58).addBox(-9.0F, -4.0F, 2.0F, 18, 7, 10, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 9.0F));

        PartDefinition Tail3 = Tail2.addOrReplaceChild("Tail3", CubeListBuilder.create()
                .texOffs(68, 75).addBox(-8.0F, -4.0F, 2.0F, 16, 6, 10, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 10.0F));

        PartDefinition EndTail = Tail3.addOrReplaceChild("EndTail", CubeListBuilder.create()
                .texOffs(46, 30).addBox(-14.0F, -1.0F, -1.0F, 28, 0, 16, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 9.0F));

        return LayerDefinition.create(meshdefinition, 123, 121);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        SandWyrmEntity entityIn = (SandWyrmEntity) entity;

        float rotation;

        switch (entityIn.getRotation())
        {
            case 1:
                rotation = -0.7F;
                break;
            case 2:
                rotation = 0.7F;
                break;
            default:
                rotation = 0.0F;
                break;
        }

        Head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        Head.xRot = (headPitch * ((float) Math.PI / 180F)) + rotation;

        for (int i = 0; i < entityIn.getAllRotateX().length; ++i)
        {
            this.modelParts.get(i).xRot = entityIn.getAllRotateX()[i] + ((i == 0) ? rotation : 0.0F);
        }

        this.LeftFrontLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 2.4F * limbSwingAmount;
        this.RightFrontLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.4F * limbSwingAmount;
        this.LeftBackLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.4F * limbSwingAmount;
        this.RightBackLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 2.4F * limbSwingAmount;
    }

    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick)
    {
        SandWyrmEntity entityIn = (SandWyrmEntity) entity;
        int i = entityIn.getAttackTimer();
        if (i > 0)
        {
            this.Mouth.xRot = 0.2618F - (-2.0F + 1.5F * this.triangleWave((float) i - partialTick, 10.0F));
        } else
        {
            this.Mouth.xRot = -0.2618F;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        Head.render(poseStack, buffer, packedLight, packedOverlay);
        Body1.render(poseStack, buffer, packedLight, packedOverlay);
    }

    private float triangleWave(float p_78172_1_, float p_78172_2_)
    {
        return (Math.abs(p_78172_1_ % p_78172_2_ - p_78172_2_ * 0.5F) - p_78172_2_ * 0.25F) / (p_78172_2_ * 0.25F);
    }
}