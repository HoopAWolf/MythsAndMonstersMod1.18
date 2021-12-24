package com.hoopawolf.mwaw.entities.renderer;

import com.hoopawolf.mwaw.entities.SandWyrmEntity;
import com.hoopawolf.mwaw.entities.model.SandWyrmModel;
import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SandWyrmRenderer extends MobRenderer<SandWyrmEntity, SandWyrmModel<SandWyrmEntity>>
{
    private static final ResourceLocation[] TEXTURE = {
            new ResourceLocation(Reference.MOD_ID, "textures/entity/sandwyrm.png"),
            new ResourceLocation(Reference.MOD_ID, "textures/entity/redsandwyrm.png")
    };

    public SandWyrmRenderer(EntityRendererProvider.Context _manager)
    {
        super(_manager, new SandWyrmModel<>(_manager.bakeLayer(SandWyrmModel.LAYER_LOCATION)), 1.0f);
    }

    @Override
    protected int getBlockLightLevel(SandWyrmEntity entityIn, BlockPos partialTicks)
    {
        return ((entityIn.level.getDayTime() > 1000 && entityIn.level.getDayTime() < 13000) ? 15 : super.getBlockLightLevel(entityIn, partialTicks));
    }

    @Override
    protected float getFlipDegrees(SandWyrmEntity entityLivingBaseIn)
    {
        return 180.0F;
    }

    @Override
    public ResourceLocation getTextureLocation(SandWyrmEntity _entity)
    {
        return TEXTURE[_entity.getSandWyrmType()];
    }
}