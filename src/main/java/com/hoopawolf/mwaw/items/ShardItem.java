package com.hoopawolf.mwaw.items;

import com.hoopawolf.mwaw.tab.MWAWItemGroup;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ShardItem extends Item
{
    public ShardItem()
    {
        super(new Item.Properties().stacksTo(16).tab(MWAWItemGroup.instance));

//        this.addPropertyOverride(new ResourceLocation("element"), new IItemPropertyGetter()
//        {
//
//            public float call(ItemStack stack, @Nullable Level worldIn, @Nullable LivingEntity entityIn)
//            {
//                return (float) getDamage(stack);
//            }
//        });
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn)
    {
        String _idWithoutTag = this.getRegistryName().toString().substring(5);

        String _string = "tooltip.mwaw:" + _idWithoutTag;

        tooltip.add(new TranslatableComponent(I18n.get("tooltip.mwaw:shard") + " " + I18n.get(_string)).setStyle(Style.EMPTY.withItalic(true).applyFormat(ChatFormatting.LIGHT_PURPLE)));

    }
}
