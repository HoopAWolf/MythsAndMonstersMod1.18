package com.hoopawolf.mwaw.entities.renderer;

import com.hoopawolf.mwaw.entities.HunterEntity;
import com.hoopawolf.mwaw.entities.model.HunterModel;
import com.hoopawolf.mwaw.ref.Reference;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HunterRenderer extends HumanoidMobRenderer<HunterEntity, HunterModel<HunterEntity>>
{
    private static final ResourceLocation Hunter_Texture = new ResourceLocation(Reference.MOD_ID, "textures/entity/hunter.png");

    public HunterRenderer(EntityRendererProvider.Context renderManagerIn)
    {
        super(renderManagerIn, new HunterModel<>(renderManagerIn.bakeLayer(HunterModel.LAYER_LOCATION)), 0.5F);
        this.addLayer(new HumanoidArmorLayer<>(this, new HumanoidModel(renderManagerIn.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)),
                new HumanoidModel(renderManagerIn.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR))));
    }

    @Override
    public void render(HunterEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn)
    {
        this.setModelVisibilities(entityIn);
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    private void setModelVisibilities(HunterEntity clientPlayer)
    {
        HunterModel<HunterEntity> playermodel = this.getModel();

        HumanoidModel.ArmPose bipedmodel$armpose = this.getArmPose(clientPlayer, InteractionHand.MAIN_HAND);
        HumanoidModel.ArmPose bipedmodel$armpose1 = this.getArmPose(clientPlayer, InteractionHand.OFF_HAND);

        if (clientPlayer.getMainArm() == HumanoidArm.RIGHT)
        {
            playermodel.rightArmPose = bipedmodel$armpose;
            playermodel.leftArmPose = bipedmodel$armpose1;
        } else
        {
            playermodel.rightArmPose = bipedmodel$armpose1;
            playermodel.leftArmPose = bipedmodel$armpose;
        }
    }

    private HumanoidModel.ArmPose getArmPose(HunterEntity playerIn, InteractionHand handIn)
    {
        ItemStack itemstack = playerIn.getItemInHand(handIn);
        if (itemstack.isEmpty())
        {
            return HumanoidModel.ArmPose.EMPTY;
        } else
        {
            if (playerIn.getUsedItemHand() == handIn && playerIn.getUseItemRemainingTicks() > 0)
            {
                UseAnim useanim = itemstack.getUseAnimation();
                if (useanim == UseAnim.BLOCK)
                {
                    return HumanoidModel.ArmPose.BLOCK;
                }

                if (useanim == UseAnim.BOW)
                {
                    return HumanoidModel.ArmPose.BOW_AND_ARROW;
                }

                if (useanim == UseAnim.SPEAR)
                {
                    return HumanoidModel.ArmPose.THROW_SPEAR;
                }

                if (useanim == UseAnim.CROSSBOW && handIn == playerIn.getUsedItemHand())
                {
                    return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                }

                if (useanim == UseAnim.SPYGLASS)
                {
                    return HumanoidModel.ArmPose.SPYGLASS;
                }
            } else if (!playerIn.swinging && itemstack.is(Items.CROSSBOW) && CrossbowItem.isCharged(itemstack))
            {
                return HumanoidModel.ArmPose.CROSSBOW_HOLD;
            }

            return HumanoidModel.ArmPose.ITEM;
        }
    }

    @Override
    public ResourceLocation getTextureLocation(HunterEntity _entity)
    {
        return Hunter_Texture;
    }
}
