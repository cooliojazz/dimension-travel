package com.up.dt.item;

import com.up.dt.DimensionTravelMod;
import com.up.dt.dimension.math.AttractedRealityVector;
import net.minecraft.world.item.Item;
import com.up.dt.dimension.RealityManager;
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
    public boolean isFoil(ItemStack stack) {
        AttractedRealityVector coord = stack.get(DimensionTravelMod.REALITY_VECTOR.get());
        if (coord != null) return RealityManager.currentReality().equals(coord);
        return false;
    }
    
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        AttractedRealityVector coord = stack.get(DimensionTravelMod.REALITY_VECTOR.get());
        if (coord != null) tooltipComponents.add(Component.translatableWithFallback("thisshouldhopefullyalwaysbeuntranslateable", coord.toString()));
    }
    
}
