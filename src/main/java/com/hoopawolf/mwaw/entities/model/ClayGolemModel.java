package com.hoopawolf.mwaw.entities.model;

import com.hoopawolf.mwaw.entities.ClayGolemEntity;
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
public class ClayGolemModel<T extends Entity> extends EntityModel<T>
{
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Reference.MOD_ID, "claygolemmodel"), "main");
    private final ModelPart bb_main;
    private final ModelPart Body;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;

    public ClayGolemModel(ModelPart root) {
        this.bb_main = root.getChild("bb_main");
        this.Body = root.getChild("Body");
        this.RightArm = root.getChild("RightArm");
        this.LeftArm = root.getChild("LeftArm");
        this.RightLeg = root.getChild("RightLeg");
        this.LeftLeg = root.getChild("LeftLeg");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create()
                        .texOffs(0, 89).addBox(-5.0F, -4.0F, -7.0F, 10, 8, 8, new CubeDeformation(0.0F))
                .texOffs(76, 0).addBox(-7.0F, -6.0F, -8.0F, 14, 5, 9, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-2.0F, -1.0F, -10.0F, 4, 6, 3, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -11.0F, -11.0F));

        PartDefinition Body = partdefinition.addOrReplaceChild("Body", CubeListBuilder.create()
                .texOffs(0, 34).addBox(-11.0F, -28.0F, -7.0F, 23, 11, 12, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-15.0F, -44.0F, -10.0F, 30, 18, 16, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create()
                .texOffs(32, 57).addBox(0.0F, -3.0F, -4.5F, 7, 23, 9, new CubeDeformation(0.0F)),
                PartPose.offset(15.0F, -12.0F, -0.5F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create()
                .texOffs(0, 57).addBox(-7.0F, -5.0F, -4.5F, 7, 23, 9, new CubeDeformation(0.0F)),
                PartPose.offset(-15.0F, -10.0F, -0.5F));

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create()
                .texOffs(64, 64).addBox(-4.0F, -1.0F, -5.0F, 8, 17, 10, new CubeDeformation(0.0F)),
                PartPose.offset(8.0F, 8.0F, 0.0F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create()
                .texOffs(70, 34).addBox(-4.0F, -1.0F, -5.0F, 8, 17, 10, new CubeDeformation(0.0F)),
                PartPose.offset(-7.0F, 8.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick)
    {
        ClayGolemEntity entityIn = (ClayGolemEntity) entity;
        int i = entityIn.getAttackTimer();
        if (i > 0)
        {
            this.RightArm.xRot = -4.0618F - (-2.0F + 1.5F * this.triangleWave((float) i - partialTick, 10.0F));
            this.LeftArm.xRot = -4.0618F - (-2.0F + 1.5F * this.triangleWave((float) i - partialTick, 10.0F));
        } else
        {
            this.RightArm.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F;
            this.LeftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
        }
    }

    @Override
    public void setupAnim(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        this.bb_main.yRot = netHeadYaw * ((float) Math.PI / 450F);
        this.bb_main.xRot = headPitch * ((float) Math.PI / 450F);

        this.RightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.LeftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount;
    }

    private float triangleWave(float p_78172_1_, float p_78172_2_)
    {
        return (Math.abs(p_78172_1_ % p_78172_2_ - p_78172_2_ * 0.5F) - p_78172_2_ * 0.25F) / (p_78172_2_ * 0.25F);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        bb_main.render(poseStack, buffer, packedLight, packedOverlay);
        Body.render(poseStack, buffer, packedLight, packedOverlay);
        RightArm.render(poseStack, buffer, packedLight, packedOverlay);
        LeftArm.render(poseStack, buffer, packedLight, packedOverlay);
        RightLeg.render(poseStack, buffer, packedLight, packedOverlay);
        LeftLeg.render(poseStack, buffer, packedLight, packedOverlay);
    }
}
