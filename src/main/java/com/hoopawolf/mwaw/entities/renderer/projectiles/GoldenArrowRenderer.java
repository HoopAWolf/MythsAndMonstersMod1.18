package com.hoopawolf.mwaw.entities.renderer.projectiles;

import com.hoopawolf.mwaw.entities.projectiles.GoldenArrowEntity;
import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GoldenArrowRenderer extends ArrowRenderer<GoldenArrowEntity>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/goldenarrow.png");

    public GoldenArrowRenderer(EntityRendererProvider.Context renderManagerIn)
    {
        super(renderManagerIn);
    }

    @Override
    protected int getBlockLightLevel(GoldenArrowEntity entityIn, BlockPos partialTicks)
    {
        return 15;
    }

    @Override
    public ResourceLocation getTextureLocation(GoldenArrowEntity _entity)
    {
        return TEXTURE;
    }

}