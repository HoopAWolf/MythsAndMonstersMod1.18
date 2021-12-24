package com.hoopawolf.mwaw.entities.model;

import com.hoopawolf.mwaw.entities.DendroidEntity;
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
public class DendroidModel<T extends Entity> extends EntityModel<T>
{
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Reference.MOD_ID, "dendroidmodel"), "main");
    private final ModelPart Jaw;
    private final ModelPart TopJaw;
    private final ModelPart BottomJaw;
    private final ModelPart Eye;
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart FrontLeg;
    private final ModelPart Hip;

    public DendroidModel(ModelPart root) {
        this.Jaw = root.getChild("Jaw");
        this.TopJaw = root.getChild("Jaw").getChild("TopJaw");
        this.BottomJaw = root.getChild("Jaw").getChild("BottomJaw");
        this.Eye = root.getChild("Jaw").getChild("Eye");
        this.RightLeg = root.getChild("RightLeg");
        this.LeftLeg = root.getChild("LeftLeg");
        this.FrontLeg = root.getChild("FrontLeg");
        this.Hip = root.getChild("Hip");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Jaw = partdefinition.addOrReplaceChild("Jaw", CubeListBuilder.create(), PartPose.offset(0.0F, 13.0F, 1.0F));

        PartDefinition TopJaw = Jaw.addOrReplaceChild("TopJaw", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-8.0F, -6.0F, -17.5F, 16, 6, 17, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -7.0F, 7.5F));

        PartDefinition BottomJaw = Jaw.addOrReplaceChild("BottomJaw", CubeListBuilder.create()
                .texOffs(0, 23).addBox(-8.0F, -0.05F, -17.5F, 16, 6, 17, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -7.0F, 7.5F));

        PartDefinition Eye = Jaw.addOrReplaceChild("Eye", CubeListBuilder.create()
                .texOffs(44, 46).addBox(-4.0F, -4.0F, -5.0F, 8, 8, 10, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, -8.0F, -1.0F));

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create()
                .texOffs(49, 23).addBox(-3.0F, -2.0F, 0.0F, 5, 4, 7, new CubeDeformation(0.0F))
                .texOffs(0, 23).addBox(-2.0F, 0.0F, 6.0F, 3, 8, 4, new CubeDeformation(0.0F)),
                PartPose.offset(-5.0F, 16.0F, 0.0F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create()
                .texOffs(49, 0).addBox(-2.0F, -2.0F, 0.0F, 5, 4, 7, new CubeDeformation(0.0F))
                .texOffs(24, 58).addBox(-1.0F, 0.0F, 6.0F, 3, 8, 4, new CubeDeformation(0.0F)),
                PartPose.offset(5.0F, 16.0F, 0.0F));

        PartDefinition FrontLeg = partdefinition.addOrReplaceChild("FrontLeg", CubeListBuilder.create()
                        .texOffs(0, 58).addBox(-3.0F, -2.0F, -5.5F, 6, 4, 6, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-2.0F, 0.0F, -7.5F, 4, 8, 4, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 16.0F, -0.5F));

        PartDefinition Hip = partdefinition.addOrReplaceChild("Hip", CubeListBuilder.create()
                .texOffs(0, 46).addBox(-6.0F, -1.0F, -5.0F, 12, 2, 10, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 13.0F, 1.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch)
    {
        DendroidEntity entityIn = (DendroidEntity) entity;
        float f3 = -(Mth.cos(limbSwing * 0.6662F * 2.0F + 0.0F) * 0.2F) * limbSwingAmount;
        float f5 = -(Mth.cos(limbSwing * 0.6662F + ((float) Math.PI / 2F)) * 1.8F) * limbSwingAmount;
        float f7 = -(Mth.sin(limbSwing * 0.6662F + ((float) Math.PI / 1.5F)) * 1.8F) * limbSwingAmount;

        this.TopJaw.xRot = 0.0F;
        this.BottomJaw.xRot = 0.0F;
        this.FrontLeg.xRot = 0.0F;
        this.RightLeg.yRot = 0.0F;
        this.LeftLeg.yRot = 0.0F;

        if (entityIn.isShooting())
        {
            this.TopJaw.xRot = -0.40F;
            this.BottomJaw.xRot = 0.1F;
        }

        this.Jaw.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.Jaw.xRot = headPitch * ((float) Math.PI / 180F);
        this.Eye.yRot = netHeadYaw * ((float) Math.PI / 270F);

        this.FrontLeg.xRot += f3;
        this.RightLeg.yRot += f5;
        this.LeftLeg.yRot += -f7;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        Jaw.render(poseStack, buffer, packedLight, packedOverlay);
        RightLeg.render(poseStack, buffer, packedLight, packedOverlay);
        LeftLeg.render(poseStack, buffer, packedLight, packedOverlay);
        FrontLeg.render(poseStack, buffer, packedLight, packedOverlay);
        Hip.render(poseStack, buffer, packedLight, packedOverlay);
    }
}