package com.hoopawolf.mwaw.entities.renderer;

import com.hoopawolf.mwaw.entities.DropBearEntity;
import com.hoopawolf.mwaw.entities.model.DropBearModel;
import com.hoopawolf.mwaw.ref.Reference;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class DropBearRenderer extends MobRenderer<DropBearEntity, DropBearModel<DropBearEntity>>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/dropbear.png");
    private static final ResourceLocation SLEEP_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/dropbearsleep.png");

    public DropBearRenderer(EntityRendererProvider.Context _manager)
    {
        super(_manager, new DropBearModel<>(_manager.bakeLayer(DropBearModel.LAYER_LOCATION)), 0.3f);
    }

    @Override
    public ResourceLocation getTextureLocation(DropBearEntity _entity)
    {
        return _entity.isTired() ? SLEEP_TEXTURE : TEXTURE;
    }

    @Override
    protected void setupRotations(DropBearEntity entityLiving, PoseStack matrixStackIn, float ageInTicks, float rotation, float partialTicks)
    {
        if (entityLiving.isHugging())
        {
            float _offset = 0.1F;
            matrixStackIn.translate((entityLiving.getHuggingDir() == 1) ? (double) _offset : (entityLiving.getHuggingDir() == 3) ? (double) -_offset : 0.0F, 0.0D, (entityLiving.getHuggingDir() == 0) ? (double) -_offset : (entityLiving.getHuggingDir() == 2) ? (double) _offset : 0.0F);
        }

        super.setupRotations(entityLiving, matrixStackIn, ageInTicks, rotation, partialTicks);
    }
}


