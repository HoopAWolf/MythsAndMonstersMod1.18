package com.hoopawolf.mwaw.entities.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.hoopawolf.mwaw.entities.HunterEntity;
import com.hoopawolf.mwaw.ref.Reference;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class HunterModel<T extends LivingEntity> extends HumanoidModel<T>
{
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(Reference.MOD_ID, "huntermodel"), "main");

    public final ModelPart leftSleeve;
    public final ModelPart rightSleeve;
    public final ModelPart leftPants;
    public final ModelPart rightPants;
    public final ModelPart jacket;
    private final ModelPart Head;
    private final ModelPart Backpack;

    private final List<ModelPart> parts;

    public HunterModel(ModelPart p_170821_) {
        super(p_170821_, RenderType::entityTranslucent);
        this.leftSleeve = p_170821_.getChild("left_sleeve");
        this.rightSleeve = p_170821_.getChild("right_sleeve");
        this.leftPants = p_170821_.getChild("left_pants");
        this.rightPants = p_170821_.getChild("right_pants");
        this.jacket = p_170821_.getChild("jacket");
        this.Head = p_170821_.getChild("Head");
        this.Backpack = p_170821_.getChild("Backpack");

        this.parts = p_170821_.getAllParts().filter((p_170824_) -> {
            return !p_170824_.isEmpty();
        }).collect(ImmutableList.toImmutableList());
    }

    public static MeshDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

            partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 2.5F, 0.0F));
            partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(40, 16).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 2.5F, 0.0F));
            partdefinition.addOrReplaceChild("left_sleeve", CubeListBuilder.create().texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.0F).extend(0.25F)), PartPose.offset(5.0F, 2.5F, 0.0F));
            partdefinition.addOrReplaceChild("right_sleeve", CubeListBuilder.create().texOffs(40, 32).addBox(-2.0F, -2.0F, -2.0F, 3.0F, 12.0F, 4.0F, new CubeDeformation(0.0F).extend(0.25F)), PartPose.offset(-5.0F, 2.5F, 0.0F));


        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(1.9F, 12.0F, 0.0F));
        partdefinition.addOrReplaceChild("left_pants", CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F).extend(0.25F)), PartPose.offset(1.9F, 12.0F, 0.0F));
        partdefinition.addOrReplaceChild("right_pants", CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F).extend(0.25F)), PartPose.offset(-1.9F, 12.0F, 0.0F));
        partdefinition.addOrReplaceChild("jacket", CubeListBuilder.create().texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F).extend(0.25F)), PartPose.ZERO);

        PartDefinition HeadPart = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create()
                .texOffs(69, 66).addBox(-5.0F, -11.0F, -5.0F, 10, 12, 10, new CubeDeformation(0.0F))
                .texOffs(75, 22).addBox(-4.0F, -10.0F, -4.0F, 8, 10, 8, new CubeDeformation(0.0F))
                .texOffs(69, 66).addBox(-1.0F, -3.0F, -6.0F, 2, 4, 2, new CubeDeformation(0.0F)), PartPose.ZERO);

        PartDefinition BagPart = partdefinition.addOrReplaceChild("Backpack", CubeListBuilder.create().texOffs(75, 48).addBox(-4.0F, -20.0F, 2.0F, 8, 9, 3, new CubeDeformation(0.0F))
                .texOffs(75, 40).addBox(-6.0F, -24.0F, 2.0F, 12, 4, 4, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        return meshdefinition;
    }

    protected Iterable<ModelPart> bodyParts() {
        return Iterables.concat(super.bodyParts(), ImmutableList.of(this.leftPants, this.rightPants, this.leftSleeve, this.rightSleeve, this.jacket));
    }

    public void setupAnim(T p_103395_, float p_103396_, float p_103397_, float p_103398_, float p_103399_, float p_103400_)
    {
        HunterEntity entity = (HunterEntity) p_103395_;
        this.crouching = entity.isShiftKeyDown();
        super.setupAnim(p_103395_, p_103396_, p_103397_, p_103398_, p_103399_, p_103400_);
        this.leftPants.copyFrom(this.leftLeg);
        this.rightPants.copyFrom(this.rightLeg);
        this.leftSleeve.copyFrom(this.leftArm);
        this.rightSleeve.copyFrom(this.rightArm);
        this.jacket.copyFrom(this.body);
        this.Head.yRot = p_103399_ * ((float) Math.PI / 180F);
        this.Head.xRot = p_103400_ * ((float) Math.PI / 180F);
        if (this.crouching)
        {
            this.Head.y = 4.2F;
            this.Backpack.xRot = 0.5F;
            this.Backpack.z = 12.0F;
        } else
        {
            this.Head.y = 0.0F;
            this.Backpack.xRot = 0.0F;
            this.Backpack.z = 0.0F;
        }

    }

    @Override
    public void renderToBuffer(PoseStack p_102034_, VertexConsumer p_102035_, int p_102036_, int p_102037_, float p_102038_, float p_102039_, float p_102040_, float p_102041_)
    {
        super.renderToBuffer(p_102034_, p_102035_, p_102036_, p_102037_, p_102038_, p_102039_, p_102040_, p_102041_);
        Head.render(p_102034_, p_102035_, p_102036_, p_102037_);
        Backpack.render(p_102034_, p_102035_, p_102036_, p_102037_);
    }

    public void setAllVisible(boolean p_103419_) {
        super.setAllVisible(p_103419_);
        this.leftSleeve.visible = p_103419_;
        this.rightSleeve.visible = p_103419_;
        this.leftPants.visible = p_103419_;
        this.rightPants.visible = p_103419_;
        this.jacket.visible = p_103419_;
        this.Backpack.visible = p_103419_;
        this.Head.visible = p_103419_;
    }

    public void translateToHand(HumanoidArm p_103392_, PoseStack p_103393_) {
        ModelPart modelpart = this.getArm(p_103392_);
        modelpart.translateAndRotate(p_103393_);
    }
}
