package com.up.dt.item;

import com.up.dt.DimensionTravelMod;
import com.up.dt.dimension.RealityCoordinate;
import com.up.dt.dimension.RealityDirection;
import com.up.dt.dimension.RealityManager;
import com.up.dt.util.PortalHelper;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodConstants;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import qouteall.imm_ptl.core.api.PortalAPI;
import qouteall.imm_ptl.core.portal.Portal;

/**
 *
 * @author Ricky
 */
public class DimensionStickItem extends Item {
    
    public DimensionStickItem(Properties properties) {
        super(properties);
    }
    
    public RealityCoordinate nextCoord(Level level, InteractionHand hand) {
        return RealityCoordinate.random(RealityDirection.size());
//        return RealityManager.realities.get(level).clone().offset(RealityDirection.BIOME_SCALE, (short)1);
//        return ResourceKey<Level> key = DimensionManager.createLevel(DimensionManager.realities.get(level).clone().offset(RealityDirection.H_SCALE, (short)(64 * (event.getHand() == InteractionHand.MAIN_HAND ? 1 : -1))));
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level level = context.getLevel();
        if (!context.getLevel().isClientSide()) {
            ItemStack offhand = context.getPlayer().getInventory().offhand.getFirst();
            RealityCoordinate coord = offhand.getItem() instanceof CoordinatePaperItem ? offhand.get(DimensionTravelMod.REALITY_COORDINATE.get()) : null;
            if (coord == null) coord = nextCoord(level, context.getHand());
            ResourceKey<Level> key = RealityManager.createLevel(coord);
            
            Portal in = PortalHelper.makeAligned(level, context.getClickedPos(), context.getClickedFace(), key);
            Portal out = PortalAPI.createReversePortal(in);

            in.level().addFreshEntity(in);
            out.level().addFreshEntity(out);
            
            context.getPlayer().getInventory().add(new ItemStack(DimensionTravelMod.COORDINATE_PAPER, 1, DataComponentPatch.builder().set(DimensionTravelMod.REALITY_COORDINATE.get(), coord).build()));
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide()) {
            ItemStack offhand = player.getInventory().offhand.getFirst();
            RealityCoordinate coord = offhand.getItem() instanceof CoordinatePaperItem ? offhand.get(DimensionTravelMod.REALITY_COORDINATE.get()) : null;
            if (coord == null) coord = nextCoord(level, usedHand);
            ResourceKey<Level> key = RealityManager.createLevel(coord);
            
            Portal in = PortalHelper.makePersonal(level, player, key);
            Portal out = PortalAPI.createReversePortal(in);

            in.level().addFreshEntity(in);
            out.level().addFreshEntity(out);
            
            player.getInventory().add(new ItemStack(DimensionTravelMod.COORDINATE_PAPER, 1, DataComponentPatch.builder().set(DimensionTravelMod.REALITY_COORDINATE.get(), coord).build()));
        }
        
        player.swing(usedHand);
        return InteractionResultHolder.consume(player.getInventory().getSelected());
    }
    
}
