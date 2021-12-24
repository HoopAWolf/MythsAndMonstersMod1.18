package com.hoopawolf.mwaw.entities.renderer;

import com.hoopawolf.mwaw.entities.DendroidElderEntity;
import com.hoopawolf.mwaw.entities.model.DendroidElderModel;
import com.hoopawolf.mwaw.entities.renderer.layer.DendroidElderEyeLayer;
import com.hoopawolf.mwaw.ref.Reference;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class DendroidElderRenderer extends MobRenderer<DendroidElderEntity, DendroidElderModel<DendroidElderEntity>>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/entity/dendroidelder.png");
    private final Random rnd = new Random();

    public DendroidElderRenderer(EntityRendererProvider.Context _manager)
    {
        super(_manager, new DendroidElderModel<>(_manager.bakeLayer(DendroidElderModel.LAYER_LOCATION)), 1.0f);
        this.addLayer(new DendroidElderEyeLayer(this));
    }

    @Override
    public Vec3 getRenderOffset(DendroidElderEntity entityIn, float partialTicks)
    {
        return (entityIn.getState() == 1 && entityIn.getAbsorbTimer() > 0) ? ((entityIn.getAbsorbTimer() > entityIn.getAbsorbTimerMax() * 0.90F) ? new Vec3(this.rnd.nextGaussian() * 0.03D, 0.0D, this.rnd.nextGaussian() * 0.03D) :
                new Vec3(this.rnd.nextGaussian() * 0.005D, 0.0D, this.rnd.nextGaussian() * 0.005D))
                : Vec3.ZERO;
    }

    @Override
    public ResourceLocation getTextureLocation(DendroidElderEntity _entity)
    {
        return TEXTURE;
    }
}

