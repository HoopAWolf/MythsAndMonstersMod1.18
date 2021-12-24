package com.hoopawolf.mwaw.entities.renderer;

import com.hoopawolf.mwaw.entities.FairyEntity;
import com.hoopawolf.mwaw.entities.WolpertingerEntity;
import com.hoopawolf.mwaw.entities.model.ClayGolemModel;
import com.hoopawolf.mwaw.entities.model.FairyModel;
import com.hoopawolf.mwaw.ref.Reference;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FairyRenderer extends MobRenderer<FairyEntity, FairyModel<FairyEntity>>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/fairy.png");
    private static final ResourceLocation ANGRY_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/angryfairy.png");

    public FairyRenderer(EntityRendererProvider.Context _manager)
    {
        super(_manager, new FairyModel<>(_manager.bakeLayer(FairyModel.LAYER_LOCATION)), 0.3f);
    }

    @Override
    protected int getBlockLightLevel(FairyEntity entityIn, BlockPos partialTicks)
    {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(FairyEntity _entity)
    {
        return _entity.isAngry() ? ANGRY_TEXTURE : TEXTURE;
    }

    @Override
    public Vec3 getRenderOffset(FairyEntity entityIn, float partialTicks)
    {
        return entityIn.isResting() ? new Vec3(0.0D, -0.2D, 0.0D) : ((entityIn.isPassenger() && entityIn.getVehicle() instanceof WolpertingerEntity) ? new Vec3(entityIn.getVehicle().getForward().x() * 0.7F, -0.9D, entityIn.getVehicle().getForward().z() * 0.7F) : super.getRenderOffset(entityIn, partialTicks));
    }

    @Override
    protected void scale(FairyEntity entitylivingbaseIn, PoseStack matrixStackIn, float partialTickTime)
    {
        matrixStackIn.scale(0.4F, 0.4F, 0.4F);
    }
}


