package com.hoopawolf.mwaw.entities.model;

import com.hoopawolf.mwaw.entities.FairyEntity;
import com.hoopawolf.mwaw.ref.Reference;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FairyModel<T extends Entity> extends EntityModel<T>
{
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Reference.MOD_ID, "fairymodel"), "main");
    private final ModelPart Head;
    private final ModelPart Body;
    private final ModelPart LegRight;
    private final ModelPart LegLeft;
    private final ModelPart ArmRight;
    private final ModelPart ArmLeft;
    private final ModelPart WingRight;
    private final ModelPart WingLeft;

    public FairyModel(ModelPart root) {
        this.Head = root.getChild("Head");
        this.Body = root.getChild("Body");
        this.LegRight = root.getChild("LegRight");
        this.LegLeft = root.getChild("LegLeft");
        this.ArmRight = root.getChild("ArmRight");
        this.ArmLeft = root.getChild("ArmLeft");
        this.WingRight = root.getChild("WingRight");
        this.WingLeft = root.getChild("WingLeft");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Body = partdefinition.addOrReplaceChild("Body", CubeListBuilder.create()
                .texOffs(0, 16).addBox(-4.0F, -4.0F, -2.0F, 8, 12, 4, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 4.0F, 0.0F));

        PartDefinition LegRight = partdefinition.addOrReplaceChild("LegRight", CubeListBuilder.create()
                .texOffs(24, 34).addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, new CubeDeformation(0.0F)),
                PartPose.offset(-2.0F, 12.0F, 0.0F));

        PartDefinition LegLeft = partdefinition.addOrReplaceChild("LegLeft", CubeListBuilder.create()
                .texOffs(24, 34).addBox(-2.0F, 0.0F, -2.0F, 4, 12, 4, new CubeDeformation(0.0F)),
                PartPose.offset(2.0F, 12.0F, 0.0F));

        PartDefinition ArmRight = partdefinition.addOrReplaceChild("ArmRight", CubeListBuilder.create()
                .texOffs(0, 42).addBox(-3.0F, -2.0F, -2.0F, 3, 12, 4, new CubeDeformation(0.0F)),
                PartPose.offset(-4.0F, 2.0F, 0.0F));

        PartDefinition ArmLeft = partdefinition.addOrReplaceChild("ArmLeft", CubeListBuilder.create()
                .texOffs(0, 42).addBox(0.0F, -3.0F, -2.0F, 3, 12, 4, new CubeDeformation(0.0F)),
                PartPose.offset(4.0F, 3.0F, 0.0F));

        PartDefinition WingRight = partdefinition.addOrReplaceChild("WingRight", CubeListBuilder.create()
                .texOffs(0, 32).addBox(-11.0F, -7.0F, 2.0F, 12, 10, 0, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(-1.0F, 5.0F, 0.0F, 0.0F, 0.1745F, 0.0F));

        PartDefinition WingLeft = partdefinition.addOrReplaceChild("WingLeft", CubeListBuilder.create()
                .texOffs(40, 22).addBox(-1.0F, -7.0F, 2.0F, 12, 10, 0, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(1.0F, 5.0F, 0.0F, 0.0F, -0.1745F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        FairyEntity entityIn = (FairyEntity) entity;

        Head.setPos(0.0F, 0.0F, 0.0F);
        Body.setPos(0.0F, 4.0F, 0.0F);
        LegRight.setPos(-2.0F, 12.0F, 0.0F);
        LegLeft.setPos(2.0F, 12.0F, 0.0F);
        ArmRight.setPos(-4.0F, 3.0F, 0.0F);
        ArmLeft.setPos(4.0F, 3.0F, 0.0F);

        Head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        Head.xRot = headPitch * ((float) Math.PI / 180F);

        this.WingRight.xRot = 0.0f;
        this.WingRight.yRot = 0.2745F;
        this.WingRight.zRot = 0.0f;
        this.WingLeft.xRot = 0.0f;
        this.WingLeft.yRot = -0.2745F;
        this.WingLeft.zRot = 0.0f;
        this.Body.xRot = 0.0F;
        this.ArmLeft.xRot = 0.0F;
        this.LegRight.xRot = 0.0F;
        this.LegRight.yRot = 0.0F;
        this.LegRight.zRot = 0.0F;
        this.LegLeft.xRot = 0.0F;
        this.LegLeft.yRot = 0.0F;
        this.LegLeft.zRot = 0.0F;

        if (entityIn.isResting() && entityIn.isOnGround())
        {
            this.ArmRight.xRot = -(float) Math.PI / 5F;
            this.ArmLeft.xRot = -(float) Math.PI / 5F;
            this.ArmRight.xRot += (-0.07F * Mth.sin(ageInTicks / -3F)) * 0.5F;
            this.ArmLeft.xRot += (0.07F * Mth.sin(ageInTicks / -3F)) * 0.5F;
            this.LegRight.xRot = -1.4137167F;
            this.LegRight.yRot = ((float) Math.PI / 10F);
            this.LegRight.zRot = 0.07853982F;
            this.LegLeft.xRot = -1.4137167F;
            this.LegLeft.yRot = (-(float) Math.PI / 10F);
            this.LegLeft.zRot = -0.07853982F;
        } else
        {
            if (entityIn.level.isEmptyBlock(new BlockPos(entityIn.getX(), entityIn.getY() - 0.5D, entityIn.getZ())))
            {
                if (!entityIn.isPassenger())
                {
                    WingRight.yRot = 0.47123894F + Mth.cos(ageInTicks * 1.5F) * (float) Math.PI * 0.05F;
                    WingLeft.yRot = -WingRight.yRot;
                    WingLeft.zRot = -0.47123894F;
                    WingRight.zRot = 0.47123894F;
                }

                this.Body.xRot = 0.5F;
                this.Body.y = 2.2F;
                this.LegRight.z = 3.0F;
                this.LegLeft.z = 3.0F;
                this.LegRight.y = 8.0F;
                this.LegLeft.y = 8.0F;
                this.Head.z = -3.2F;
                this.ArmLeft.y = 1.7F;
                this.ArmRight.y = 0.5F;
                this.ArmLeft.z = -1.2F;
                this.ArmRight.z = -1.2F;

                this.LegRight.xRot = 0.2137167F;
                this.LegLeft.xRot = 0.2137167F;
                this.LegRight.zRot = 0.07853982F;
                this.LegLeft.zRot = -0.07853982F;
            }
        }
    }

    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick)
    {
        FairyEntity entityIn = (FairyEntity) entity;
        int i = entityIn.getAttackTimer();
        if (i > 0)
        {
            this.ArmRight.xRot = -4.0618F - (-2.0F + 1.5F * this.triangleWave((float) i - partialTick, 10.0F));
        } else
        {
            this.ArmRight.xRot = 0.0F;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        Head.render(poseStack, buffer, packedLight, packedOverlay);
        Body.render(poseStack, buffer, packedLight, packedOverlay);
        LegRight.render(poseStack, buffer, packedLight, packedOverlay);
        LegLeft.render(poseStack, buffer, packedLight, packedOverlay);
        ArmRight.render(poseStack, buffer, packedLight, packedOverlay);
        ArmLeft.render(poseStack, buffer, packedLight, packedOverlay);
        WingRight.render(poseStack, buffer, packedLight, packedOverlay);
        WingLeft.render(poseStack, buffer, packedLight, packedOverlay);
    }

    private float triangleWave(float p_78172_1_, float p_78172_2_)
    {
        return (Math.abs(p_78172_1_ % p_78172_2_ - p_78172_2_ * 0.5F) - p_78172_2_ * 0.25F) / (p_78172_2_ * 0.25F);
    }
}