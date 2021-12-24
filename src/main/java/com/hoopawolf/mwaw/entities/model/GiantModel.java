package com.hoopawolf.mwaw.entities.model;

import com.hoopawolf.mwaw.ref.Reference;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class GiantModel<T extends Entity> extends EntityModel<T>
{
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Reference.MOD_ID, "giantmodel"), "main");
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart Body;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;

    public GiantModel(ModelPart root) {
        this.RightLeg = root.getChild("RightLeg");
        this.LeftLeg = root.getChild("LeftLeg");
        this.Body = root.getChild("Body");
        this.RightArm = root.getChild("RightArm");
        this.LeftArm = root.getChild("LeftArm");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create()
                .texOffs(58, 0).addBox(-3.0F, -1.0F, -4.0F, 6.0F, 16.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 9.0F, 0.0F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(0, 43).addBox(-3.0F, -1.0F, -4.0F, 6.0F, 16.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 9.0F, 0.0F));

        PartDefinition Body = partdefinition.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(0, 24).addBox(-9.0F, -14.7576F, -5.6743F, 18.0F, 8.0F, 11.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-8.0F, -6.7576F, -7.6743F, 16.0F, 11.0F, 13.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 3.75F, 0.5F));

        PartDefinition Head = Body.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(62, 40).addBox(-4.0F, -4.5F, -4.0F, 8.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -14.1628F, 0.5038F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(28, 60).addBox(-2.5F, -3.0F, -3.0F, 5.0F, 22.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-11.5F, -8.0F, 2.0F));

        PartDefinition Mace = RightArm.addOrReplaceChild("Mace", CubeListBuilder.create().texOffs(38, 38).addBox(0.0F, -0.5872F, -11.0038F, 2.0F, 2.0F, 20.0F, new CubeDeformation(0.0F))
                .texOffs(62, 24).addBox(-2.0F, -3.5872F, -18.0038F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(28, 43).addBox(-1.0F, -1.0F, -10.0F, 4.0F, 4.0F, 7.0F, new CubeDeformation(0.0F))
                .texOffs(72, 72).addBox(-2.0F, 4.4128F, -18.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(20, 36).addBox(1.0F, 2.4128F, -10.0F, 0.0F, 4.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.5F, 16.5872F, -0.9962F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(50, 60).addBox(-2.5F, -3.0F, -3.0F, 5.0F, 22.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(11.5F, -8.0F, 2.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        RightLeg.render(poseStack, buffer, packedLight, packedOverlay);
        LeftLeg.render(poseStack, buffer, packedLight, packedOverlay);
        Body.render(poseStack, buffer, packedLight, packedOverlay);
        RightArm.render(poseStack, buffer, packedLight, packedOverlay);
        LeftArm.render(poseStack, buffer, packedLight, packedOverlay);
    }
}
