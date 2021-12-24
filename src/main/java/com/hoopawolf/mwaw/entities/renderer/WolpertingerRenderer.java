package com.hoopawolf.mwaw.entities.renderer;

import com.hoopawolf.mwaw.entities.WolpertingerEntity;
import com.hoopawolf.mwaw.entities.model.WolpertingerModel;
import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WolpertingerRenderer extends MobRenderer<WolpertingerEntity, WolpertingerModel<WolpertingerEntity>>
{
    private static final ResourceLocation[] TEXTURE = {
            new ResourceLocation(Reference.MOD_ID, "textures/entity/wolpertinger.png"),
            new ResourceLocation(Reference.MOD_ID, "textures/entity/wolpertinger2.png"),
            new ResourceLocation(Reference.MOD_ID, "textures/entity/wolpertinger3.png"),
            new ResourceLocation(Reference.MOD_ID, "textures/entity/wolpertinger4.png")
    };

    public WolpertingerRenderer(EntityRendererProvider.Context _manager)
    {
        super(_manager, new WolpertingerModel<>(_manager.bakeLayer(WolpertingerModel.LAYER_LOCATION)), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(WolpertingerEntity _entity)
    {
        return TEXTURE[_entity.getWolpertingerType()];
    }
}
