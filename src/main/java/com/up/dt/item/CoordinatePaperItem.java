package com.up.dt.item;

import com.up.dt.DimensionTravelMod;
import net.minecraft.world.item.Item;
import com.up.dt.dimension.RealityCoordinate;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/**
 *
 * @author Ricky
 */
public class CoordinatePaperItem extends Item {
    
    public CoordinatePaperItem(Properties properties) {
        super(properties);
    }
    
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        RealityCoordinate coord = stack.get(DimensionTravelMod.REALITY_COORDINATE.get());
        tooltipComponents.add(Component.translatableWithFallback("thisshouldhopefullyalwaysbeuntranslateable", coord.toString()));
    }
    
}
